/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 *
 * @author alumne
 */
public class ModelException extends RuntimeException {
    String message;
    public ModelException(String message){
        this.message = message;
    }
    
    @Override
    public String getMessage(){
        return message;
    }
    
    @Override
    public String toString(){
        return "Database Model error: " + message;
    }
}
