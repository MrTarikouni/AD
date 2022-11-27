package database.models;

import database.DatabaseModel;
import database.TableColumn;
import database.ModelException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.gson.annotations.Expose;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alumne
 */
public class Image extends DatabaseModel {
    @Expose
    @TableColumn(dataType="INTEGER", primaryKey=true, autoPK=true)
    private int id;
    @Expose
    @TableColumn(dataType="VARCHAR (128)", nullable=false)
    private String title;
    @Expose
    @TableColumn(dataType="VARCHAR (1024)", nullable=false)
    private String description;
    @Expose
    @TableColumn(dataType="VARCHAR (256)", nullable=false)
    private String keywords;
    @Expose
    @TableColumn(dataType="VARCHAR (256)", nullable=false)
    private String author;
    @Expose
    @TableColumn(dataType="VARCHAR (256)", foreignKeyColumn="username", foreignKeyTable=User.Meta.tablename, nullable=false)
    private String creator;
    @Expose
    @TableColumn(dataType="VARCHAR (10)", nullable=false)
    private String capture_date;
    @Expose
    @TableColumn(dataType="VARCHAR (10)", nullable=false)
    private String storage_date;
    @Expose
    @TableColumn(dataType="VARCHAR (512)", nullable=false)
    private String file_name;
    
    static class Meta{
        public static final String tablename = "IMAGES";
        public static final String[] unique_together = {"creator", "file_name"};
    }
    
    public Image() throws SQLException {
        super();
        id = 0;
        title = null;
        description = null;
        keywords = null;
        author = null;
        creator = null;
        capture_date = null;
        storage_date = null;
        file_name = null;
    }
    
    public Image(boolean connectionless) {
        super(true);
        id = 0;
        title = "-";
        description = "-";
        keywords = "-";
        author = "-";
        creator = "-";
        capture_date = "-";
        file_name = "-";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        storage_date = dtf.format(LocalDate.now());

    }
    
    @Override
    public ArrayList<Image> getAll() throws SQLException, ModelException {
        return getAll(Image.class);
    }
    
    public Image getEmpty() {
        return new Image(true);
    }
    
    public void setTitle(String title) {
        checkForNoConnection();
        this.title = title;
    }
    
    public void setDescription(String description) {
        checkForNoConnection();
        this.description = description;
    }
    
    public void setKeywords(String keywords) {
        checkForNoConnection();
        this.keywords = keywords;
    }
    
    public void setAuthor(String author) {
        checkForNoConnection();
        this.author = author;
    }
    
    public void setCreator(String creator) {
        checkForNoConnection();
        this.creator = creator;
    }
    
    public void setCaptureDate(String capture_date) {
        checkForNoConnection();
        this.capture_date = capture_date;
    }
    
    public void setFilename(String file_name) {
        checkForNoConnection();
        this.file_name = file_name;
    }

    public int getId(){
        return id;
    }

    public String getCreator() {
        return creator;
    }
    
    public String getFilename() {
        return file_name;
    }
    public String getCaptureDate() {
        return capture_date;
    }
    public String getAuthor() {
        return author;
    }
    public String getKeywords() {
        return keywords;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }

    public String getStorageDate() {
        return storage_date;
    }

    public String getStoredFileName() {
        return creator + "_" + file_name;
    }
    
    public void addImage(String title, String description, String keywords, String author, String creator, String capture_date, String file_name) throws SQLException, ModelException {
        checkForConnection();
        Image new_image = new Image(true);
        new_image.title = (title==null || title.isEmpty()) ? new_image.title : title;
        new_image.description = (description==null ||  description.isEmpty()) ? new_image.description : description;
        new_image.keywords = (keywords==null || keywords.isEmpty()) ? new_image.keywords : keywords;
        new_image.author = (author==null || author.isEmpty()) ? new_image.author : author;
        new_image.creator = (creator==null || creator.isEmpty()) ? new_image.creator : creator;
        new_image.capture_date = (capture_date==null || capture_date.isEmpty()) ? new_image.capture_date : capture_date;
        new_image.file_name = (file_name==null || file_name.isEmpty()) ? new_image.file_name : file_name;
        saveToDB(new_image);
    }

    public Image getImageByID(int id) throws SQLException, ModelException {
        ArrayList<Image> imgs = getFiltered(Image.class, "id = ?", new Object[] {id});
        if (imgs.isEmpty()) return null;
        return imgs.get(0);
    }

    public Image getImageByNameAndCreator(String fname, String creator) throws SQLException, ModelException {
        if (fname == null || creator == null) return null;

        String filter = "creator = ? AND file_name = ?";
        Object[] values = {creator, fname};
        ArrayList<Image> img = getFiltered(Image.class, filter, values);
        if (img.isEmpty()) return null;
        return img.get(0);
    }
    
    public ArrayList<Image> getImagesByCreator(String creator) throws SQLException, ModelException {        
        return getFiltered(Image.class, "creator = ?", new Object[] {creator});
    }

    public ArrayList<Image> SearchImages(String file_name, String title, String autor, String creator, String storage_date, String keywords) throws SQLException, ModelException {
        String filter = "";
        ArrayList<Object> values = new ArrayList<>();
        if (file_name != null && !file_name.isEmpty()) {
            filter += "UPPER(file_name) LIKE UPPER(?)";
            values.add( "%" + file_name +  "%");
        }
        if (title != null && !title.isEmpty()) {
            if (!filter.isEmpty()) filter += " AND ";
            filter += "UPPER(title) LIKE UPPER(?)";
            values.add("%" + title + "%");
        }
        if (autor != null && !autor.isEmpty()) {
            if (!filter.isEmpty()) filter += " AND ";
            filter += "UPPER(author) LIKE UPPER(?)";
            values.add("%" + autor + "%");
        }
        if (creator != null && !creator.isEmpty()) {
            if (!filter.isEmpty()) filter += " AND ";
            filter += "UPPER(creator) LIKE UPPER(?)";
            values.add("%" + creator + "%");
        }
        if (storage_date != null && !storage_date.isEmpty())  {
            if (!filter.isEmpty()) filter += " AND ";
            filter += "storage_date = ?";
            values.add(storage_date);
        }
        if (keywords != null && !keywords.isEmpty())  {
            if (!filter.isEmpty()) filter += " AND ";
            filter += "UPPER(keywords) LIKE UPPER(?)";
            values.add("%" + keywords + "%");
        }
        if (filter.isEmpty()) return new ArrayList<>();
        System.out.println("IMG FILTER: " + filter + " IMG VALUE " + values.get(0));
        return getFiltered(Image.class, filter, values.toArray());
    }

    public String toJSON(){
        checkForNoConnection();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    public static String toJSONArray(ArrayList<Image> imageArray){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(imageArray);
    }
}
