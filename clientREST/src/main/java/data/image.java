/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alumne
 */
public class image {
    private int id;
    private String title;
    private String description;
    private String keywords;
    private String author;
    private String creator;
    private String capture_date;
    private String storage_date;
    private String filename;
    private Map<String, String> hm = new HashMap<>();
    
    public image(){
        hm.put("title","");
        hm.put("description","");
        hm.put("keywords","");
        hm.put("author","");
        hm.put("creator","");
        hm.put("capture_date","");
        //hm.put("storage_date",null);
        hm.put("filename","");
    }
    
    public Map getAttributes(){
        return hm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        hm.put("title",title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        hm.put("description",description);
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
        hm.put("keywords",keywords);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
        hm.put("author",author);
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
        hm.put("creator",creator);
    }

    public String getCapture_date() {
        return capture_date;
    }

    public void setCapture_date(String capture_date) {
        this.capture_date = capture_date;
        hm.put("capture_date",capture_date);
    }

    public String getStorage_date() {
        return storage_date;
    }

    public void setStorage_date(String storage_date) {
        this.storage_date = storage_date;
        //hm.put("storage_date",storage_date);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
        hm.put("filename",filename);
    }
    
    
}
