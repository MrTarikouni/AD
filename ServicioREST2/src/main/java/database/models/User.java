/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.models;

import database.DatabaseModel;
import database.ModelException;
import database.TableColumn;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;

/**
 *
 * @author alumne
 */
public class User extends DatabaseModel{    
    @TableColumn(dataType="VARCHAR (256)", primaryKey=true)
    private String username;
    @TableColumn(dataType="VARCHAR (256)", nullable=false)
    private String password;
    
    static class Meta{
        public static final String tablename = "USERS";
    }
    
    public User() throws SQLException {
        super();
        username = null;
        password = null;
    }
    
    private User(boolean connectionless) {
        super(connectionless);
        this.username = null;
        this.password = null;   
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    } 
    
    @Override
    public ArrayList<User> getAll() throws SQLException, ModelException {
        return getAll(User.class);
    }
    
    public User getUserByUsername(String username) throws SQLException, ModelException {
        ArrayList<User> users = getFiltered(User.class, "username = ?", new Object[] {username});
        if (users.isEmpty()) return null;
        return users.get(0);
    }
    
    public void addUser(String username, String password) throws SQLException, ModelException {
        checkForConnection();
        User new_user = new User(true);
        new_user.username = username;
        new_user.password = password;
        saveToDB(new_user);
    }
    
    public boolean checkPassword(String password) {
        // TODO hashear las contraseñas
        return this.password.equals(password);
    }
    
    @Override
    public String toString(){
        if (connection == null)
            return "Username: " + username + ", password: " + password;
       return "Connection-oriented instance";
    }
  
    
    public User getEmpty(){
        User instance = new User(true);
        return instance;
    }
    
    public void setUsername(String username) {
        checkForNoConnection();
        this.username = username;
    }
    
    public void setPassword(String password) {
        checkForNoConnection();
        this.password = password;
    }
}
