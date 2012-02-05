/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import java.util.ArrayList;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author jordane
 */
public class Help {

    private Rectangle background;
    private Group helpGroup,totalGroup;
    private double position = 10;
    private ArrayList<Option> array;
    private HandWave handWave;

    public enum Etats {

        HAND_CLOSE, HAND_OPEN, FINGER
    }

    public Help(double windowX, double windowY) {
        array = new ArrayList<>();
        this.helpGroup = new Group();
        this.totalGroup = new Group();
        this.background = new Rectangle(windowX, 100, Color.web("#000000", 0.4));
        this.handWave = new HandWave(windowX/2, windowY/2);
        helpGroup.getChildren().add(background);
        totalGroup.getChildren().addAll(helpGroup,handWave.getWave());
        helpGroup.setVisible(false);
        handWaveSetVisible(false);
        
        
        
        
    }
    
    public void handWaveSetVisible(boolean b){
        
        handWave.activate(b);
    }

    public void addImg(String s, Etats e) {
        array.add(new Option(s, e));
    }
    public boolean isVisible(){
        return helpGroup.isVisible();
    }

    public Group getHelp() {
        return totalGroup;
    }

    
    public void helpVisible(boolean b){
        helpGroup.setVisible(b);
    }

    public void illuminateOptions(Etats... e) {
        for (Option o : array) {
            boolean temp = false;
            for (Etats etat : e) {
                if (o.getEtat().equals(etat)) {
                    temp = true;
                }
            }
            o.setVisible(temp);
        }
    }

    private class Option {

        private ImageView img;
        private Etats etat;

        public Option(String s, Etats e) {
            img = new ImageView(new Image(s));
            this.etat = e;
            img.setX(position);
            position += img.getImage().getWidth() + 10;
            helpGroup.getChildren().add(img);
        }

        public Etats getEtat() {
            return etat;
        }
        
        public void setVisible(boolean b){
            if (b){
                img.setOpacity(1);
            }else{
                img.setOpacity(0.2);
            }
        }
    }
    
    
    private class HandWave{
        private ImageView gif;
        private Rectangle background;
        private Group group;
        private Path path;
        private RotateTransition rot;
        private PathTransition tr;
        
        public HandWave(double x, double y){
            this.path= new Path();
            this.path.getElements().add(new MoveTo(x-90, y+20));
            this.path.getElements().add(new QuadCurveTo(x-15, y-80, x+60, y+20));
            this.path.setVisible(false);
            
            
            this.group = new Group();
            this.gif = new ImageView(new Image("Images/help/main.png"));
            this.gif.setScaleX(0.5);
            this.gif.setScaleY(0.5);
            this.background = new Rectangle(350, 250, Color.BLACK);
            this.background.setOpacity(0.4);
            this.background.setX(x-200);
            this.background.setY(y-125);
            this.background.setArcHeight(40);
            this.background.setArcWidth(40);
            this.background.setStroke(Color.DARKGREY);
            
            this.rot = new RotateTransition(Duration.seconds(1));
            this.rot.setNode(gif);
            this.rot.setFromAngle(-45);
            this.rot.setToAngle(45);
            this.rot.setAutoReverse(true);
            this.rot.setCycleCount(Timeline.INDEFINITE);
            
            this.tr = new PathTransition();
            this.tr.setPath(path);
            this.tr.setNode(gif);
            this.tr.setDuration(Duration.seconds(1));
            this.tr.setAutoReverse(true);
            this.tr.setCycleCount(Timeline.INDEFINITE);
            group.getChildren().addAll(background,gif,path);
        }
        
        public Group getWave(){
            return group;
        }
                
        
        public void activate(boolean b){
            if (b){
                group.setVisible(b);
                tr.play();
                rot.play();
            }else{
                group.setVisible(b);
                tr.stop();
                rot.stop();
            }
            
        }
        
    }
}
