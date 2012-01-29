/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;


/**
 *
 * @author demalejo
 */
public class CircleObject  {

    private Circle circ;
    private double size = 90;
    private double boxSize = 80;
    private BoxBlur box;
    
    //objets de l'illumination
    private Group illuminationGroup,totalGroup;
    private Circle illuminateCirc1;
    private Circle illuminateCirc2;
    private double illuminateRayonAdded=80;
    private RotateTransition transition;
    
    //variables pour le deplacement
    double X=0;
    double Y=0;
    
    //variables paramétrables
    double constantSize = 1;
    double constantOpacity = 0.4;

    public CircleObject(double i, double j, Group g) {
        circ = new javafx.scene.shape.Circle(0, 0, size, Color.BLACK);
        box = new BoxBlur(boxSize, boxSize, 10);
        circ.setEffect(box);
        totalGroup = new Group();
        totalGroup.getChildren().add(circ);
        
        initIllumination();
        
        totalGroup.getChildren().add(illuminationGroup);
        totalGroup.setLayoutX(i);
        totalGroup.setLayoutY(j);
        g.getChildren().add(totalGroup);
        

    }
    
    
    public double getX(){
        return totalGroup.getLayoutX();
    }
    
    public double getY(){
        return totalGroup.getLayoutY();
    }
   

    public boolean hitTestObject(double i, double j) {
        Circle c = new Circle(circ.getCenterX(), circ.getCenterY(), boxSize/2);
        return c.contains(i, j);
    }

    // effet de blur (flou) quand on repasse sur une tache
    public void increaseDensity() {

            boxSize ++;
            size +=3;
            circ.setRadius(size);
            box.setHeight(boxSize);
            box.setWidth(boxSize);
        
    }

    // diminuer le blur (flou)
    public void unDraw() {
        boxSize -= 2;
        box.setHeight(boxSize);
        box.setWidth(boxSize);
    }

    public Circle getCircle() {
        return circ;
    }
    
   // fonctions pour le prototype I 
    
    public void toIlluminate(){
        
        illuminateCirc1.setCenterX(0);
        illuminateCirc1.setCenterY(-illuminateRayonAdded);
        illuminateCirc2.setCenterX(0);
        illuminateCirc2.setCenterY(illuminateRayonAdded);
        illuminationGroup.setLayoutX(circ.getCenterX());
        illuminationGroup.setLayoutY(circ.getCenterY());
        illuminationGroup.setVisible(true);
        transition.playFrom(Duration.seconds(2));
        
    }
    
    public void toNormal(){
        transition.stop();
        illuminationGroup.setVisible(false);
        
    }
   
    public void changeOpacity(double x){
        if (box.getHeight()+ constantOpacity*x>=0 && box.getHeight()+ constantOpacity*x<=300){
         box.setHeight(box.getHeight() + constantOpacity*x);
        box.setWidth(box.getWidth() + constantOpacity*x);
        }
         
    }
    // valeur magique 2 àparamétrable
    public void changeSize(double x){
    circ.setRadius(circ.getRadius()+ constantSize*x);
    }
    
  
  
            
    public double proximity(double x, double y){
        return (Math.sqrt((x-totalGroup.getLayoutX())*(x-totalGroup.getLayoutX())+(y-totalGroup.getLayoutY())*(y-totalGroup.getLayoutY())));
    }
    
    private void initIllumination(){
        illuminationGroup = new Group();
        illuminateCirc1 = new Circle(5);
        illuminateCirc2 = new Circle(5);
        illuminateCirc1.setFill(Color.WHITE);
        illuminateCirc2.setFill(Color.WHITE);
        illuminationGroup.getChildren().addAll(illuminateCirc1,illuminateCirc2);
        illuminationGroup.setVisible(false);
        

        
        transition = new RotateTransition();
        
        transition.setNode(illuminationGroup);
        transition.setFromAngle(0);
        transition.setToAngle(3600);
        transition.setDuration(Duration.seconds(20));
        transition.setCycleCount(Timeline.INDEFINITE);
        
    }

    public void select(){
        illuminateCirc1.setFill(Color.BLUE);
        illuminateCirc2.setFill(Color.BLUE);
    }
    
    public void unSelect(){
        illuminateCirc1.setFill(Color.WHITE);
        illuminateCirc2.setFill(Color.WHITE);
    }
    public void translatePostion(double x, double y){
        //circ.setCenterX(circ.getCenterX()+x);
        //circ.setCenterY(circ.getCenterY()+y);
        //illuminationGroup.setLayoutX(x);
        //illuminationGroup.setLayoutY(y);
        totalGroup.setLayoutX(totalGroup.getLayoutX() + x);
        totalGroup.setLayoutY(totalGroup.getLayoutY() + y);
        
    }
    
    public void destroy(){
        transition.stop();
        toNormal();
        circ.setOpacity(0);
    }
}