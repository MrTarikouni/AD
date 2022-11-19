/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.HashMap;

/**
 *
 * @author alumne
 */
public class CjtImages {
    private HashMap<Integer, image> images = new HashMap<>();
    
    public CjtImages(){
        
    }

    public HashMap<Integer, image> getImages() {
        return images;
    }

    public void setImages(HashMap<Integer, image> images) {
        this.images = images;
    }
    
    public image getImage(int id){
        if (images.containsKey(id)) return images.get(id);
        else return null;
    }
    
    public void setImage(int id, image i){
        images.put(id, i);
    }
    
}
