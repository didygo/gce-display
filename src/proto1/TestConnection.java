/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

/**
 *
 * @author demalejo
 */
public class TestConnection {
    PathTransition pt;
    int circleX = 0;
    int circleY =0;
    Circle circ;
    Path path;
    BoxBlur blur;
    
    
    public TestConnection(Group g, int x, int y ){
        circleX = x;
        circleY = y;
        circ = new Circle(circleX,circleY , 5);
        blur = new BoxBlur(10, 10, 10);
        circ.setEffect(blur);
        circ.setFill(Color.WHITE);
        createPathTransition();
        
        g.getChildren().addAll(circ,path);
        
    }
    
    public void createPathTransition(){
        path = new Path();
        path.getElements().add(new MoveTo(circleX,circleY));
        path.getElements().add(new CubicCurveTo(circleX, circleY, circleX+50, circleY, circleX+50, circleY+50));
        path.getElements().add(new CubicCurveTo(circleX+50, circleY+50, circleX+50, circleY+100, circleX, circleY+100));
        path.getElements().add(new CubicCurveTo(circleX, circleY+100, circleX-50, circleY+100, circleX-50, circleY+50));
        path.getElements().add(new CubicCurveTo(circleX-50, circleY+50, circleX-50, circleY, circleX, circleY));
        path.setVisible(false);
      
        
        pt = new PathTransition(Duration.seconds(2), circ);
        pt.setPath(path);
        pt.setNode(circ);
        pt.setCycleCount(Timeline.INDEFINITE);
    }
    
    
        

        

        
        
 
    public void play() {
        pt.play();
    }


    public void stop() {
        pt.stop();
    }


}
