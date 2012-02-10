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
import javafx.scene.shape.*;
import javafx.util.Duration;

/**
 *
 * @author jordane
 */
public class Help {

    private Rectangle background;
    private ImageView img;
    private Group helpGroup,totalGroup;
    private double position = 10;
    private ArrayList<Option> array;
    private HandWave handWave;
    private Path pathReturn;
    private Path pathGo;
    private PathTransition transition;
    private boolean isIn = false;
    private double proximity = 50;
    private double offSetX = 100;
    private double windowX;
    
    public enum Etats {
        HAND_CLOSE, HAND_OPEN, FINGER
    }

    public Help(double windowX, double windowY) {
        this.windowX = windowX;
        array = new ArrayList<>();
        this.helpGroup = new Group();
        this.totalGroup = new Group();
        this.background = new Rectangle(windowX, 100, Color.web("#000000", 0.4));
        this.background.setX(offSetX-windowX);
        this.handWave = new HandWave(windowX/2, windowY/2);
        
        this.background.setArcHeight(20);
        this.background.setArcWidth(20);
        
        helpGroup.getChildren().addAll(background,handWave.getWave());
        totalGroup.getChildren().addAll(helpGroup);
        helpGroup.setVisible(false);
        handWaveSetVisible(false);
        
        position = -windowX+220;
        
        this.img = new ImageView(new Image("Images/help/Help.png"));

        //on place l'image en son centre'
        this.img.setX(-img.getImage().getWidth() / 2 + 60);
        this.img.setY(-img.getImage().getHeight() / 2 + 50);

        helpGroup.getChildren().addAll(img);
        
        pathGo = new Path();
        pathGo.getElements().add(new MoveTo(-windowX/2+offSetX, 50));
        pathGo.getElements().add(new LineTo(windowX/2-offSetX, 50));
        pathGo.setVisible(false);

        pathReturn = new Path();
        pathReturn.getElements().add(new MoveTo(windowX/2-offSetX, 50));
        pathReturn.getElements().add(new LineTo(-windowX/2+offSetX, 50));
        pathReturn.setVisible(false);
        
        transition = new PathTransition();
        transition.setPath(pathGo);
        transition.setNode(helpGroup);
        transition.setDuration(Duration.millis(1000));
        
        helpGroup.setLayoutY(50);
    }
    
    public void handWaveSetVisible(boolean b){
        handWave.activate(b);
    }
    
    public void helpIn() {
        transition.setPath(pathGo);
        transition.play();
        isIn = true;
        switchImage(isIn);
    }

    public void helpOut() {
        transition.setPath(pathReturn);
        transition.play();
        isIn = false;
        switchImage(isIn);
    }
    
    private void switchImage(boolean b) {
        helpGroup.getChildren().remove(img);
        if (b) {
            this.img = new ImageView(new Image("Images/help/flecheGauche.png"));
        } else {
            this.img = new ImageView(new Image("Images/help/Help.png"));
        }
        this.img.setX(-img.getImage().getWidth() / 2 + 60);
        this.img.setY(-img.getImage().getHeight() / 2 + 50);
        helpGroup.getChildren().addAll(img);
    }
    
    public boolean isIn() {
        return isIn;
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
    
    public boolean proximity(double x, double y) {
        return (x > helpGroup.getLayoutX() + (isIn ? windowX - offSetX : 0) - img.getImage().getWidth() / 2 - proximity
                && x < helpGroup.getLayoutX() + (isIn ? windowX - offSetX : 0) + img.getImage().getWidth() / 2 + proximity
                && y < helpGroup.getLayoutY() + img.getImage().getHeight() / 2 + proximity
                && y > helpGroup.getLayoutY() - img.getImage().getHeight() / 2 - proximity);
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
    
    private class HandWave {
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
