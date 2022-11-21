package com.mycompany.serviciorest.resources;

import com.google.gson.Gson;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import Database.*;
import data.*;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;


/**
 *
 * @author 
 */
@Path("javaee8")
public class JavaEE8Resource {
    
    @GET
    public Response ping(){
        return Response
                .ok("ping")
                .build();
    }
    /**
     * POST method to login in the application
     * @param username
     * @param password 
     * @return
     */
     @Path("login")
     @POST
     @Consumes (MediaType.APPLICATION_FORM_URLENCODED)
     @Produces (MediaType.APPLICATION_JSON)
     public Response Login(@FormParam("username") String username,
             @FormParam("password") String password) {
         
         user u = new user();
         u.setUsername(username);
         u.setPassword(password);
         
         boolean pepe = Loginquery.login(u);
         System.out.println(u.getUsername());

         if (pepe) {
             return Response.ok(1, MediaType.APPLICATION_JSON).build();
         }
         
         else {
             return Response.ok(-1, MediaType.APPLICATION_JSON).build();
         }
     }
     
    /**
     * POST method to register in the application
     * @param username
     * @param password 
     * @return
     */
     @Path("signup")
     @POST
     @Consumes (MediaType.APPLICATION_FORM_URLENCODED)
     @Produces (MediaType.APPLICATION_JSON)
     public Response Register(@FormParam("username") String username,
             @FormParam("password") String password) {
         
         user u = new user();
         u.setUsername(username);
         u.setPassword(password);
         
         Loginquery r = new Loginquery();

         if (r.signup(u)) {
             return Response.ok(1, MediaType.APPLICATION_JSON).build();
         }
         
         else {
             return Response.ok(-1, MediaType.APPLICATION_JSON).build();
         }
     }
     
     /**
     * POST method to register a new image 
     * @param title
     * @param description
     * @param keywords
     * @param author
     * @param creator
     * @param capt_date
     * @param filename
     * @return
     */
    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerImage(@FormParam("title") String title, 
            @FormParam("description") String description, 
            @FormParam("keywords") String keywords, 
            @FormParam("author") String author, 
            @FormParam("creator") String creator, 
            @FormParam("capture") String capt_date,
            @FormParam("filename") String filename) {
    
        image i = new image();
        i.setTitle(title);
        i.setAuthor(author);
        i.setDescription(description);
        i.setKeywords(keywords);
        i.setCreator(creator);
        i.setCapture_date(capt_date);
        i.setFilename(filename);
        
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.now();
        String storage_date = (date.format(formatter1));
        
        i.setStorage_date(storage_date);
       
        int id = Imagequery.nextId();
        i.setId(id);
        
        int lastIndex = filename.lastIndexOf('.');
        String fileNamewithoutextension = filename;
        String extension = "";
        if (lastIndex != -1){
            fileNamewithoutextension = filename.substring(0, lastIndex);
            extension = filename.substring(lastIndex);
        }
   
        i.setFilename(fileNamewithoutextension + Integer.toString(id) + extension);   
        
         if (Imagequery.register(i)) {
             return Response.ok(1, MediaType.APPLICATION_JSON).build();
         }
         
         else {
             return Response.ok(-1, MediaType.APPLICATION_JSON).build();
         }
         
    }
    
    /**
     *POST method to modify an existing image
     * @param id
     * @param title
     * @param description
     * @param keywords
     * @param author
     * @param creator, used for checking image ownership 
     * @param capt_date
     * @return
     */
    @Path("modify")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyImage(@FormParam("id") String id, 
            @FormParam("title") String title, 
            @FormParam("description") String description, 
            @FormParam("keywords") String keywords, 
            @FormParam("author") String author, 
            @FormParam("creator") String creator, 
            @FormParam("capture") String capt_date) {
        
        int x = Integer.valueOf(id);
        image i = Imagequery.getImagefromID(x);
        
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        i.setTitle(title);
        i.setDescription(description);
        i.setKeywords(keywords);
        i.setAuthor(author);
        i.setCreator(creator);
        i.setCapture_date(capt_date);
        
        LocalDate date = LocalDate.now();
        String storage_date = (date.format(formatter1));
        i.setStorage_date(storage_date);
        
        if (Imagequery.modify(i)) {
            return Response.ok(1, MediaType.APPLICATION_JSON).build();
        }
        else {
            return Response.ok(-1, MediaType.APPLICATION_JSON).build();
        }
    }
    
/**
* POST method to delete an existing image
* @param id
* @param creator, used for checking image ownership
* @return
*/
@Path("delete")
@POST
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public Response deleteImage (@FormParam("id") String id,
            @FormParam("creator") String creator) { 
    
    image i = Imagequery.getImagefromID(Integer.valueOf(id));
    if (Imagequery.delete(i)) {
        return Response.ok(1, MediaType.APPLICATION_JSON).build();
    }
    else {
        return Response.ok(-1, MediaType.APPLICATION_JSON).build();
    }
}

/**
* GET method to get an image from its ID
* @param id
* @return
*/
@Path("getImageFromID/{id}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response getImageFromID (@PathParam("id") int id) {

    image i = Imagequery.getImagefromID(id);
    String failed = "-1";
     if (i == null)
        return Response.ok(failed, MediaType.APPLICATION_JSON).build();
     
     if (i.getCreator() == null)
        return Response.ok(failed, MediaType.APPLICATION_JSON).build();
    
    if (i.getCreator().length() < 1)
        return Response.ok(failed, MediaType.APPLICATION_JSON).build();
     
    Gson gson = new Gson();
    String imageJS = gson.toJson(i);
    
     return Response.ok(200, MediaType.APPLICATION_JSON).entity(imageJS).build();

}

/**
* GET method to list images
* @return
*/
@Path("list")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response listImages () {
    Gson gson = new Gson();
    ResultSet rs = Imagequery.selectAll();
    CjtImages cjt = new CjtImages();
    try {
        while (rs.next()){
        image i = new image();
        i.setId(rs.getInt("id"));
        i.setTitle(rs.getString("title"));
        i.setDescription(rs.getString("description"));
        i.setKeywords(rs.getString("keywords"));
        i.setAuthor(rs.getString("author"));
        i.setCreator(rs.getString("creator"));
        i.setCapture_date(rs.getString("capture_date"));
        i.setStorage_date(rs.getString("storage_date"));
        i.setFilename("filename");
        cjt.setImage(i.getId(), i);
    }
    }catch(Exception e){
       rs = null;
       return Response.ok(-1,MediaType.APPLICATION_JSON).build();
    }
    
    String json = gson.toJson(cjt);
    return Response.ok(200, MediaType.APPLICATION_JSON).entity(json).build();
}

/**
* GET method to search images by id
* @param id
* @return
*/
@Path("searchID/{id}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response searchByID (@PathParam("id") int id) {
    Gson gson = new Gson();
    image x = new image();
    x.setId(id);
    ResultSet rs = Imagequery.search(x);
    CjtImages cjt = new CjtImages();
    try {
        while (rs.next()){
        image i = new image();
        i.setId(rs.getInt("id"));
        i.setTitle(rs.getString("title"));
        i.setDescription(rs.getString("description"));
        i.setKeywords(rs.getString("keywords"));
        i.setAuthor(rs.getString("author"));
        i.setCreator(rs.getString("creator"));
        i.setCapture_date(rs.getString("capture_date"));
        i.setStorage_date(rs.getString("storage_date"));
        i.setFilename("filename");
        cjt.setImage(i.getId(), i);
    }
    }catch(Exception e){
       rs = null;
       return Response.ok(-1,MediaType.APPLICATION_JSON).build();
    }
    
    String json = gson.toJson(cjt);
    return Response.ok(200, MediaType.APPLICATION_JSON).entity(json).build();
    
}

/**
* GET method to search images by title
* @param title
* @return
*/
@Path("searchTitle/{title}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response searchByTitle (@PathParam("title") String title) {

    Gson gson = new Gson();
    image x = new image();
    x.setTitle(title);
    ResultSet rs = Imagequery.search(x);
    CjtImages cjt = new CjtImages();
    try {
        while (rs.next()){
        image i = new image();
        i.setId(rs.getInt("id"));
        i.setTitle(rs.getString("title"));
        i.setDescription(rs.getString("description"));
        i.setKeywords(rs.getString("keywords"));
        i.setAuthor(rs.getString("author"));
        i.setCreator(rs.getString("creator"));
        i.setCapture_date(rs.getString("capture_date"));
        i.setStorage_date(rs.getString("storage_date"));
        i.setFilename("filename");
        cjt.setImage(i.getId(), i);
    }
    }catch(Exception e){
       rs = null;
       return Response.ok(-1,MediaType.APPLICATION_JSON).build();
    }
    
    String json = gson.toJson(cjt);
    return Response.ok(200, MediaType.APPLICATION_JSON).entity(json).build();
    
}

/**
* GET method to search images by creation date. Date format should be
* yyyy-mm-dd
* @param date
* @return
*/
@Path("searchCreationDate/{date}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response searchByCreationDate (@PathParam("date") String date) {

    Gson gson = new Gson();
    image x = new image();
    x.setCapture_date(date);
    ResultSet rs = Imagequery.search(x);
    CjtImages cjt = new CjtImages();
    try {
        while (rs.next()){
        image i = new image();
        i.setId(rs.getInt("id"));
        i.setTitle(rs.getString("title"));
        i.setDescription(rs.getString("description"));
        i.setKeywords(rs.getString("keywords"));
        i.setAuthor(rs.getString("author"));
        i.setCreator(rs.getString("creator"));
        i.setCapture_date(rs.getString("capture_date"));
        i.setStorage_date(rs.getString("storage_date"));
        i.setFilename("filename");
        cjt.setImage(i.getId(), i);
    }
    }catch(Exception e){
       rs = null;
       return Response.ok(-1,MediaType.APPLICATION_JSON).build();
    }
    
    String json = gson.toJson(cjt);
    return Response.ok(200, MediaType.APPLICATION_JSON).entity(json).build();
    
}

/**
* GET method to search images by author
* @param author
* @return
*/
@Path("searchAuthor/{author}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response searchByAuthor (@PathParam("author") String author) {

    Gson gson = new Gson();
    image x = new image();
    x.setAuthor(author);
    ResultSet rs = Imagequery.search(x);
    CjtImages cjt = new CjtImages();
    try {
        while (rs.next()){
        image i = new image();
        i.setId(rs.getInt("id"));
        i.setTitle(rs.getString("title"));
        i.setDescription(rs.getString("description"));
        i.setKeywords(rs.getString("keywords"));
        i.setAuthor(rs.getString("author"));
        i.setCreator(rs.getString("creator"));
        i.setCapture_date(rs.getString("capture_date"));
        i.setStorage_date(rs.getString("storage_date"));
        i.setFilename("filename");
        cjt.setImage(i.getId(), i);
    }
    }catch(Exception e){
       rs = null;
       return Response.ok(-1,MediaType.APPLICATION_JSON).build();
    }
    
    String json = gson.toJson(cjt);
    return Response.ok(200, MediaType.APPLICATION_JSON).entity(json).build();
    
}

/**
* GET method to search images by keyword
* @param keywords
* @return
*/
@Path("searchKeywords/{keywords}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response searchByKeywords (@PathParam("keywords") String keywords) {
 
    Gson gson = new Gson();
    image x = new image();
    x.setKeywords(keywords);
    ResultSet rs = Imagequery.search(x);
    CjtImages cjt = new CjtImages();
    try {
        while (rs.next()){
        image i = new image();
        i.setId(rs.getInt("id"));
        i.setTitle(rs.getString("title"));
        i.setDescription(rs.getString("description"));
        i.setKeywords(rs.getString("keywords"));
        i.setAuthor(rs.getString("author"));
        i.setCreator(rs.getString("creator"));
        i.setCapture_date(rs.getString("capture_date"));
        i.setStorage_date(rs.getString("storage_date"));
        i.setFilename("filename");
        cjt.setImage(i.getId(), i);
    }
    }catch(Exception e){
       rs = null;
       return Response.ok(-1,MediaType.APPLICATION_JSON).build();
    }
    
    String json = gson.toJson(cjt);
    return Response.ok(200, MediaType.APPLICATION_JSON).entity(json).build();
    
}

@Path("searchMultiple/{title}/{author}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response multipleSearch (@PathParam("title") String title,
                                @PathParam("author") String author) {
    Gson gson = new Gson();
    image x = new image();
    x.setTitle(title);
    x.setAuthor(author);
    ResultSet rs = Imagequery.search(x);
    CjtImages cjt = new CjtImages();
    try {
        while (rs.next()){
        image i = new image();
        i.setId(rs.getInt("id"));
        i.setTitle(rs.getString("title"));
        i.setDescription(rs.getString("description"));
        i.setKeywords(rs.getString("keywords"));
        i.setAuthor(rs.getString("author"));
        i.setCreator(rs.getString("creator"));
        i.setCapture_date(rs.getString("capture_date"));
        i.setStorage_date(rs.getString("storage_date"));
        i.setFilename("filename");
        cjt.setImage(i.getId(), i);
    }
    }catch(Exception e){
       rs = null;
       return Response.ok(-1,MediaType.APPLICATION_JSON).build();
    }
    
    String json = gson.toJson(cjt);
    return Response.ok(200, MediaType.APPLICATION_JSON).entity(json).build();
    
}

}
