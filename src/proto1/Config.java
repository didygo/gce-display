/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import javafx.scene.Group;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author jordane
 */
public class Config {
    private Group totalGroup;
    private Rectangle background;
    private double height = 500;
    private double weight = 300;
    
    
    public Config(){
        totalGroup = new Group();
        
        
    }
    private void components(){
        Slider s = new Slider(0, 400, 100);
    }
    
    
    private void initBackground(){
        background = new Rectangle(height, weight, Color.BLACK);
        background.setX(0);
        background.setY(0);
        background.setArcHeight(40);
        background.setArcWidth(40);
        background.setOpacity(0.5);
        totalGroup.getChildren().add(background);
    }
    
    
    public Group getConfig(){
     return totalGroup;   
    }
    
}
