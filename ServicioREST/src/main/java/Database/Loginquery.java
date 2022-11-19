/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;


import data.user;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author alumne
 */
public class Loginquery {
    
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
    
    static public boolean login(user U){
        connectDB();
        
        PreparedStatement ps;
        ResultSet rs;
        String query;
        boolean valid = false;
        try{
            query = "select * from usuarios where id_usuario = ? and password = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1,U.getUsername());
            ps.setString(2,U.getPassword());
            rs = ps.executeQuery();
            
            valid = rs.next();
            
            closeDB();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return valid;
    }
    
    static public boolean signup(user U){
        connectDB();
        
        PreparedStatement ps;
        ResultSet rs;
        String query;
        boolean valid = true;
        try{
            query = "select * from usuarios where id_usuario = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1,U.getUsername());
            rs = ps.executeQuery();
            
            if(rs.next()) return false;
            
            query = "insert into usuarios values(?,?)";
            ps = connection.prepareStatement(query);
            ps.setString(1,U.getUsername());
            ps.setString(2,U.getPassword());
            ps.executeUpdate();
        
            closeDB();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return valid;
    }
    
}
