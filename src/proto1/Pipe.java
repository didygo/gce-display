/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author jordane
 */
public class Pipe {
    private ImageView pipe,ring;
    private Group totalGroup,temp;
    double scale;
    double dx,dy,dw,dh;
    double xinit, yinit, winit,hinit;
    double min,max,value;
    double coef = 0.785677;
    double widthRing;
    double heightRing ;
    double constant;
    
    
    public Pipe(double min, double max,double val,double constant){
        this.temp = new Group();
        this.constant =  constant;
        this.min = min;
        this.max = max;
        this.value = min;
        this.totalGroup = new Group();
        pipe = new ImageView(new Image("Images/pipe/pipe.png"));
        ring = new ImageView(new Image("Images/pipe/ring.png"));
        widthRing = ring.getImage().getWidth();
        heightRing = ring.getImage().getHeight();
        
        scale = 0.8;
        pipe.setX((scale-1)*pipe.getImage().getWidth()/2);
        pipe.setY((scale-1)*pipe.getImage().getHeight()/2);
        pipe.setScaleX(scale);
        pipe.setScaleY(scale);
        
        System.out.println(scale);
        dx = 286.6 * scale;
        dy = -225.45*scale;
        dw = -74.1*scale;
        dh = -68.2*scale;
        
        temp.setLayoutX(20*scale);
        temp.setLayoutY(232*scale);
        setWidth(ring.getImage().getWidth()*scale);
        setHeight(ring.getImage().getHeight()*scale);
        xinit = 20*scale;
        yinit = 232*scale;
        winit = widthRing;
        hinit = heightRing;
        temp.getChildren().add(ring);
        totalGroup.getChildren().addAll(pipe,temp);
        move((max-val)/constant);
    }
    
    public void move (double d){
        if (value +d*constant<=max && value + d*constant >=min){
            value += d*constant;
            temp.setLayoutX(xinit+((value-min)/(max-min))*dx);
            temp.setLayoutY(yinit+((value-min)/(max-min))*dy);
            
            setWidth((winit+((value-min)/(max-min))*dw));
            setHeight(hinit+((value-min)/(max-min))*dh);
        }
    }
    
    public void setVisible(boolean b){
        totalGroup.setVisible(b);
    }
    
    public Group getPipe(){
        return totalGroup;
    }
    
    private void setWidth(double newWidth) {
        ring.setX(ring.getX() + ((newWidth/widthRing)-1)*widthRing/2);
        ring.setScaleX(ring.getScaleX()*newWidth/widthRing);
        widthRing = newWidth;
    }
    
    private void setHeight(double h){
        ring.setY(ring.getY()+ ((h/heightRing)-1)*heightRing/2);
        ring.setScaleY(ring.getScaleY()*h/heightRing);
        heightRing = h;
    }
    
    
    
}
