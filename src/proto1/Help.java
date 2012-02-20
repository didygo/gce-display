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
    private double proximity = 50;
    private Rectangle background;
    private ImageView img;
    private Group helpGroup,totalGroup;
    private ArrayList<Option> array;
    private HandWave handWave;
    private Path pathReturn;
    private Path pathGo;
    private PathTransition transition;
    private boolean isIn = false;
    private double helpIconsPosition;
    private double offSetX = 100;
    private double offSetXEnd = 250;
    private double helpHeight = 110;
    private double yInit = 30;
    private double xInit;
    private double xBouton;
    private double dX;
    
    public enum Etats {
        HAND_CLOSE, HAND_OPEN, FINGER
    }

    public Help(double windowX, double windowY) {
        this.dX = windowX - offSetX - offSetXEnd;
        this.xInit = -windowX + offSetX;
        this.xBouton = windowX - offSetX/2;
        this.helpIconsPosition = offSetXEnd + 10;
        
        this.array = new ArrayList<Option>();
        this.helpGroup = new Group();
        this.totalGroup = new Group();
        this.background = new Rectangle(windowX, helpHeight, Color.web("#000000", 0.4));
        this.handWave = new HandWave(windowX/2, windowY/2);
        
        this.background.setArcHeight(20);
        this.background.setArcWidth(20);
        
        helpGroup.getChildren().addAll(background);
        totalGroup.getChildren().addAll(helpGroup,handWave.getWave());
        helpGroup.setVisible(true);
        totalGroup.setVisible(false);
        handWaveSetVisible(false);
        
        this.img = new ImageView(new Image("Images/help/Help.png"));

        //on place l'image en son centre'
        this.img.setX(-img.getImage().getWidth() / 2 + xBouton);
        this.img.setY(-img.getImage().getHeight() / 2 + helpHeight/2);

        helpGroup.getChildren().addAll(img);
        
        pathGo = new Path();
        pathGo.getElements().add(new MoveTo(windowX/2, helpHeight/2));
        pathGo.getElements().add(new LineTo(windowX/2 + dX, helpHeight/2));
        pathGo.setVisible(false);

        pathReturn = new Path();
        pathReturn.getElements().add(new MoveTo(windowX/2 + dX, helpHeight/2));
        pathReturn.getElements().add(new LineTo(windowX/2, helpHeight/2));
        pathReturn.setVisible(false);
        
        transition = new PathTransition();
        transition.setPath(pathGo);
        transition.setNode(helpGroup);
        transition.setDuration(Duration.millis(1000));
        
        helpGroup.setLayoutY(yInit);
        helpGroup.setLayoutX(xInit);
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
        this.img.setX(-img.getImage().getWidth() / 2 + xBouton);
        this.img.setY(-img.getImage().getHeight() / 2 + helpHeight/2);
        helpGroup.getChildren().addAll(img);
    }
    
    public boolean isIn() {
        return isIn;
    }
    
    public void addImg(String s, Etats e) {
        array.add(new Option(s, e));
    }
    
    public boolean isVisible(){
        return totalGroup.isVisible();
    }

    public Group getHelp() {
        return totalGroup;
    }

    public void allVisible(boolean b){
        totalGroup.setVisible(b);
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
        return (x > helpGroup.getLayoutX() + xBouton + (isIn ? dX : 0) - img.getImage().getWidth() / 2 - proximity
                && x < helpGroup.getLayoutX() + xBouton + (isIn ? dX : 0) + img.getImage().getWidth() / 2 + proximity
                && y < helpGroup.getLayoutY() + img.getImage().getHeight() / 2 + proximity + helpHeight/2
                && y > helpGroup.getLayoutY() - img.getImage().getHeight() / 2 - proximity + helpHeight/2);
    }

    private class Option {
        private ImageView img;
        private Etats etat;

        public Option(String s, Etats e) {
            img = new ImageView(new Image(s));
            this.etat = e;
            img.setX(helpIconsPosition);
            img.setY(-img.getImage().getHeight()/2 + helpHeight/2);
            helpIconsPosition += img.getImage().getWidth() + 10;
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
