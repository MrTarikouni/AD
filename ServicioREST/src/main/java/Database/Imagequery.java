/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import data.image;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import javax.servlet.ServletException;

/**
 *
 * @author alumne
 */
public class Imagequery {
    
    static private Connection connection;
    
    static private void connectDB(){
        try{
        connection = null;
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        connection = DriverManager.getConnection("jdbc:derby://localhost:1527/pr2;user=pr2;password=pr2");
        
        }catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
    }
    
    static private void closeDB(){
        try{
            connection.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    static public boolean register(image M){
        connectDB();
        
        PreparedStatement ps;
        String query;
        boolean valid = true;
        try{
            
            query = "INSERT INTO IMAGE (TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            ps = connection.prepareStatement(query);           
            ps.setString(1, M.getTitle());
            ps.setString(2, M.getDescription());
            ps.setString(3, M.getKeywords());
            ps.setString(4, M.getAuthor());
            ps.setString(5, M.getCreator());
            ps.setString(6, M.getCapture_date());
            ps.setString(7, M.getStorage_date());
            ps.setString(8, M.getFilename());            
            ps.executeUpdate();
        
            closeDB();
        }
        catch (Exception e) {
            valid = false;
            System.err.println(e.getMessage());
        }
        return valid;
    }
    
    static public boolean delete(image M){
        connectDB();
        
        PreparedStatement ps;
        String query;
        boolean valid = true;
        try{
            
            query = "delete from image where id = ? ";
            
            ps = connection.prepareStatement(query);           
            ps.setInt(1, M.getId());           
            ps.executeUpdate();
        
            closeDB();
        }
        catch (Exception e) {
            valid = false;
            System.err.println(e.getMessage());
        }
        return valid;
    }
    
    static public boolean modify(image M){
        connectDB();
        
        PreparedStatement ps;
        String query;
        boolean valid = true;
        try{
            
            query = "update image "+ 
                    "set title = ?, description = ?, keywords = ?, author = ?, creator = ?, capture_date = ?, storage_date = ?, filename = ? "+
                    "where id = ?";
            
            ps = connection.prepareStatement(query);           
            ps.setString(1, M.getTitle());
            ps.setString(2, M.getDescription());
            ps.setString(3, M.getKeywords());
            ps.setString(4, M.getAuthor());
            ps.setString(5, M.getCreator());
            ps.setString(6, M.getCapture_date());
            ps.setString(7, M.getStorage_date());
            ps.setString(8, M.getFilename());
            ps.setInt(9, M.getId());
            ps.executeUpdate();
        
            closeDB();
        }
        catch (Exception e) {
            valid = false;
            System.err.println(e.getMessage());
        }
        return valid;
    }
    
    static public ResultSet search(image M){
        connectDB();
        
        PreparedStatement ps;
        ResultSet rs;
        String query;
        List<String> atr = new ArrayList<>();
        Map<String,String> hm = M.getAttributes();
        for (Map.Entry<String,String> e: hm.entrySet()){
            if (!".".equals(e.getValue())){
                if (e.getValue().length() != 0){
                    if (e.getValue() != null){
                        atr.add(e.getKey());
                    hm.put(e.getKey(),"%" + e.getValue() + "%"); 
                    
                    }
                }
                
            }
        }
        int condiciones = atr.size();
        if (condiciones == 0){
            ResultSet res;
            res = selectAll();
            return res;
        }
        else{
        query = "select * from image where ";
        for (int i =0; i < condiciones -1; ++i){
            query = query + atr.get(i) + " like ? and ";
        }
        query = query + atr.get(condiciones -1) +" like ?";
        out.println(query);
        try{
            
            ps = connection.prepareStatement(query); 
            for (int i = 0; i <= atr.size()-1; ++i){
                String id = atr.get(i);
                String value = hm.get(id);
                ps.setString(i+1, value);
            }
            rs = ps.executeQuery();
        
        }
        catch (Exception e) {
            rs = null;
            System.err.println(e.getMessage());
        }
        return rs;  
        }
    }
    
    static public ResultSet search2(String field, String value){
        connectDB();
        
        PreparedStatement ps;
        ResultSet rs;
        String query;
        String filter  = "%" + value + "%";
        try{
            
            query = "select * from image where "+ field + " like ? ";
            
            ps = connection.prepareStatement(query);           
            ps.setString(1, filter);
            rs = ps.executeQuery();
        
            
        }
        catch (Exception e) {
            rs = null;
            System.err.println(e.getMessage());
        }
        return rs;
    }
    
    static public ResultSet selectAll() {
        connectDB();
        
        PreparedStatement ps;
        ResultSet rs;
        String query;
        try{
            query = "select * from image";
            
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
        }catch (Exception e) {
            rs = null;
            System.err.println(e.getMessage());
        }
        
        return rs;
    }
    
    static public int nextId(){
        connectDB();
        
        PreparedStatement ps;
        ResultSet rs;
        String query;
        int ident = 0;
        try{
            query = "select MAX(id) as id from image";
            
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()){
                ident = rs.getInt("id") + 1;
            }
        }catch (Exception e) {
            rs = null;
            System.err.println(e.getMessage());
        }
        
        return ident;
    }
    
    static public image getImagefromID(int id) {
        connectDB();
        
        PreparedStatement ps;
        ResultSet rs;
        String query;
        image i = new image();
        try{
            query = "select * from image where id = ? ";
            ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
            
                i.setId(id);
                i.setTitle(rs.getString("TITLE"));
                i.setDescription(rs.getString("DESCRIPTION"));
                i.setKeywords(rs.getString("KEYWORDS"));
                i.setAuthor(rs.getString("AUTHOR"));
                i.setCreator(rs.getString("CREATOR"));
                i.setCapture_date(rs.getString("CAPTURE_DATE"));
                i.setStorage_date(rs.getString("STORAGE_DATE"));
                i.setFilename(rs.getString("FILENAME"));
            }
            
            closeDB();
            
        }catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return i;
    }
}
