/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.restad.resources;

/**
 *
 * @author alumne
 */

import database.models.Image;
import database.models.User;

import utils.ApiError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import java.io.InputStream;

@Path("api")
public class ServicioRest {
    private static final String imagePath = "/var/webapp/practica2/images";
    /**
    * POST method to login in the application      
    * @param username     
    * @param password     
    * @return      
    */
    @Path("login")      
    @POST      
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)      
    @Produces(MediaType.APPLICATION_JSON)     
    public Response Login(@FormParam("username") String username,              
            @FormParam("password") String password) {
            
        try (User usertab = new User()) {
            User res_user = usertab.getUserByUsername(username);
            if (res_user == null || !res_user.checkPassword(password)) {
                return Response.status(401).entity(ApiError.errorJSON("Invalid credentials", 1)).build();
            }
        }
        catch (SQLException e) {
            return Response.status(500).entity(ApiError.errorJSON(e.getMessage(), 0)).build();
        }
        return Response.ok().build();
    }
    
    /**
    * POST method to login in the application      
    * @param username     
    * @param password     
    * @return      
    */
    @Path("registerUser")      
    @POST      
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)      
    @Produces(MediaType.APPLICATION_JSON)     
    public Response Register(@FormParam("username") String username,
            @FormParam("password") String password) {
            
        try(User usertab = new User()) {
            User res_user = usertab.getUserByUsername(username);
            if (res_user != null) {
                return Response.status(400).entity(ApiError.errorJSON("User does exist", 1)).build();
            }
            usertab.addUser(username, password);
        }
        catch (SQLException e){
            return Response.status(500).entity(ApiError.errorJSON(e.getMessage(), 0)).build();
        }
        return Response.status(201).build();
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
    public Response registerImage (@FormDataParam("title") String title, 
            @FormDataParam("description") String description, 
            @FormDataParam("keywords") String keywords, 
            @FormDataParam("author") String author, 
            @FormDataParam("creator") String creator, 
            @FormDataParam("capture") String capt_date,
            @FormDataParam("filename") String filename) {

        try(Image imagestab = new Image()) {
            Image im = imagestab.getImageByNameAndCreator(filename, creator);
            if (im != null)
                return Response.status(400).entity(ApiError.errorJSON("This image already exists", 1)).build();
            imagestab.addImage(title, description, keywords, author, creator, capt_date, filename);
        }
        catch (SQLException e){
            return Response.status(500).entity(ApiError.errorJSON(e.getMessage(), 0)).build();
        }
        return Response.status(201).build();
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
     * @param fileInputStream
     * @param fileMetaData
     * @return
     */
    @Path("registerWF")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerImageWithFile (@FormDataParam("title") String title,
           @FormDataParam("description") String description,
           @FormDataParam("keywords") String keywords,
           @FormDataParam("author") String author,
           @FormDataParam("creator") String creator,
           @FormDataParam("capture") String capt_date,
           @FormDataParam("filename") String filename,
           @FormDataParam("file") InputStream fileInputStream,
           @FormDataParam("file") FormDataContentDisposition fileMetaData) throws IOException {

        FileOutputStream outfile = null;

        try(Image imagestab = new Image()) {
            Image im = imagestab.getImageByNameAndCreator(filename, creator);
            if (im != null)
                return Response.status(400).entity(ApiError.errorJSON("This image already exists", 1)).build();
            imagestab.addImage(title, description, keywords, author, creator, capt_date, filename);
            String stored_imageName = creator + "_" + filename;
            outfile = new FileOutputStream(imagePath + File.separator + stored_imageName);
            byte[] bytes = new byte[1024];
            while ((fileInputStream.read(bytes)) > 0) {
                outfile.write(bytes);
            }

        }
        catch (SQLException e){
            return Response.status(500).entity(ApiError.errorJSON(e.getMessage(), 0)).build();
        }
        finally{
            if (outfile != null) {
                outfile.close();
            }
            fileInputStream.close();
        }
        return Response.status(201).build();
    }
    
    /**     
    * POST method to modify an existing image     
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
    public Response modifyImage (@FormParam("id") String id,              
            @FormParam("title") String title,             
            @FormParam("description") String description,             
            @FormParam("keywords") String keywords,             
            @FormParam("author") String author,             
            @FormParam("creator") String creator,             
            @FormParam("capture") String capt_date) {

        try(Image imagestab = new Image()){
            int i_id = Integer.parseInt(id);

            Image im = imagestab.getImageByID(i_id);

            if  (im == null) {
                return Response.status(404).entity(ApiError.errorJSON("No image found with this id", 0)).build();
            }

            if  (!im.getCreator().equals(creator)) {
                return Response.status(403).entity(ApiError.errorJSON("The image does not belong to this creator", 0)).build();
            }

            if (title != null && !title.isEmpty()) {
                im.setTitle(title);
            }
            if (description != null && !description.isEmpty()) {
                im.setDescription(description);
            }
            if (keywords != null && !keywords.isEmpty()) {
                im.setKeywords(keywords);
            }
            if (author != null && !author.isEmpty()) {
                im.setAuthor(author);
            }
            if (capt_date != null && !capt_date.isEmpty()) {
                im.setCaptureDate(capt_date);
            }
            System.out.println("WANNA  UPDATE IMAGE " + im.getId());
            imagestab.saveToDB(im);

        }
        catch (SQLException e) {
            return Response.status(500).entity(ApiError.errorJSON(e.getMessage(), 0)).build();
        }
        catch (NumberFormatException e){
            return Response.status(400).entity(ApiError.errorJSON("Field id must be an integer", 1)).build();
        }
        
        return Response.ok().build();
    }
    
    /**     
    * POST method to delete an existing image     
    * @param id        
    * @return     
    */     
    @Path("delete")     
    @POST     
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)     
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteImage (@FormParam("id") String id,
        @FormParam("creator") String creator) {
        try(Image imagestab = new Image()){
            int i_id = Integer.parseInt(id);

            Image im = imagestab.getImageByID(i_id);

            if  (im == null) {
                return Response.status(404).entity(ApiError.errorJSON("No image found with this id", 0)).build();
            }

            if  (!im.getCreator().equals(creator)) {
                return Response.status(403).entity(ApiError.errorJSON("The image does not belong to this creator", 0)).build();
            }
            String imagePath = "/var/webapp/practica2/images";
            String img_path = imagePath + File.separator + im.getCreator() + "_" + im.getFilename();
            File f = new File(img_path);
            if (f.exists()) {
                java.nio.file.Path p1 = Paths.get(img_path);
                Files.delete(p1);
            }
            imagestab.deleteFromDB(im);
        }
        catch (SQLException e) {
            return Response.status(500).entity(ApiError.errorJSON(e.getMessage(), 0)).build();
        }
        catch (NumberFormatException e){
            return Response.status(400).entity(ApiError.errorJSON("Field id must be an integer", 1)).build();
        }
        catch (IOException e) {
            return Response.status(500).entity(ApiError.errorJSON("Image not deleted from disk", 2)).build();
        }
        return Response.ok().build();
    }
    
    /**     
    * GET method to list images      
    * @return     
    */     
    @Path("list")     
    @GET         
    @Produces(MediaType.APPLICATION_JSON)     
    public Response listImages () {
        try(Image imagestab = new Image()){
            ArrayList<Image> image_list = imagestab.getAll();
            String images_json = Image.toJSONArray(image_list);
            return Response.ok(images_json).build();
        }
        catch (SQLException e){
            return Response.status(500).entity(ApiError.errorJSON(e.getMessage(), 0)).build();
        }

    }
    
    /**     
    * GET method to search images by id     
    * @param id     
    * @return     
    */     
    @Path("searchID/{id}")     
    @GET         
    @Produces(MediaType.APPLICATION_JSON)     
    public Response searchByID (@PathParam("id") int id){
        try(Image imagestab = new Image()){
            Image img = imagestab.getImageByID(id);
            if (img == null){
                return Response.status(404).entity(ApiError.errorJSON("Image not found", 1)).build();
            }
            String img_json = img.toJSON();
            return Response.ok(img_json).build();
        }
        catch (SQLException e) {
            return Response.status(500).entity(ApiError.errorJSON(e.getMessage(), 0)).build();

        }
    }
    
    /**     
    * GET method to search images by title     
    * @param title         
    * @return     
    */     
    @Path("searchTitle/{title}")     
    @GET         
    @Produces(MediaType.APPLICATION_JSON)     
    public Response searchByTitle (@PathParam("title") String title)  {
        try(Image imagestab = new Image()){
            ArrayList<Image> image_list = imagestab.SearchImages(null, title, null, null, null, null);
            String images_json = Image.toJSONArray(image_list);
            return Response.ok(images_json).build();
        }
        catch (SQLException e) {
            return Response.status(500).entity(ApiError.errorJSON(e.getMessage(), 0)).build();
        }
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
        try(Image imagestab = new Image()){
            ArrayList<Image> image_list = imagestab.SearchImages(null, null, null, null, date, null);
            String images_json = Image.toJSONArray(image_list);
            return Response.ok(images_json).build();
        }
        catch (SQLException e) {
            return Response.status(500).entity(ApiError.errorJSON(e.getMessage(), 0)).build();
        }
    }    
    
    /**     
    * GET method to search images by author     
    * @param author         
    * @return     
    */     
    @Path("searchAuthor/{author}")     
    @GET         
    @Produces(MediaType.APPLICATION_JSON)     
    public Response searchByAuthor (@PathParam("author") String author){
        try(Image imagestab = new Image()){
            ArrayList<Image> image_list = imagestab.SearchImages(null, null, author, null, null, null);
            String images_json = Image.toJSONArray(image_list);
            return Response.ok(images_json).build();
        }
        catch (SQLException e) {
            return Response.status(500).entity(ApiError.errorJSON(e.getMessage(), 0)).build();
        }
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
        try(Image imagestab = new Image()){
            ArrayList<Image> image_list = imagestab.SearchImages(null, null, null, null, null, keywords);
            String images_json = Image.toJSONArray(image_list);
            return Response.ok(images_json).build();
        }
        catch (SQLException e) {
            return Response.status(500).entity(ApiError.errorJSON(e.getMessage(), 0)).build();
        }
    }

    @Path("getImage/{id}")
    @GET
    @Produces("image/*")
    public Response getImage(@PathParam("id") int id) {
        try (Image imagestab = new Image()){
            Image  im = imagestab.getImageByID(id);
            if (im == null)
                return Response.status(404).entity(ApiError.errorJSON("Image not found", 1)).type(MediaType.APPLICATION_JSON).build();

            String filename = im.getFilename();
            String stored_name = im.getStoredFileName();

            File f = new File(imagePath + File.separator + stored_name);
            if (!f.exists())
                return Response.status(404).entity(ApiError.errorJSON("Image not saved", 2)).type(MediaType.APPLICATION_JSON).build();
            String mt = new MimetypesFileTypeMap().getContentType(f);
            return Response.ok(f, mt).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+filename).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(ApiError.errorJSON(e.getMessage(), 0)).type(MediaType.APPLICATION_JSON).build();
        }
    }

    
}

