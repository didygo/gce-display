/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import java.awt.event.ActionListener;
import javafx.animation.KeyFrame;
import proto2.*;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import javax.swing.Timer;

/**
 *
 * @author demalejo
 */
public class Curseur {

    ImageView curseur, imgTimer;
    PathTransition transition;
    double ancienX = 0;
    double ancienY = 0;
    boolean play = false;
    Duration transitionTime = Duration.millis(100);
    Group loaderGroup = new Group();
    Group curseurGroup = new Group();
    Timer timer;
    private Loading loading;

    public Curseur(Group g) {
        loading = new Loading();
        curseur = new ImageView(new Image("Images/curseurs/mainFermee.png"));
        imgTimer = new ImageView(new Image("Images/curseurs/"));
        curseur.setX(-50);
        curseur.setY(-50);
        curseurGroup.getChildren().addAll(curseur,imgTimer,loaderGroup);
        imgTimer.setVisible(false);
        g.getChildren().addAll(curseurGroup);



    }
    
    

    public void startTimer() {
        loading.playAnimation();
    }
    
    public void stopTimer(){
        loading.stopAnimation();
    }

    public void changeToLibre() {
        curseur.setVisible(false);
    }

    public void changeToRepos() {
        curseur.setVisible(true);
        curseur.setImage(new Image("Images/curseurs/mainFermee.png"));

    }

    public void changeToHandClose() {
        curseur.setVisible(true);
        curseur.setImage(new Image("Images/curseurs/mainFermee.png"));
    }

    public void setVisible(boolean b) {
        curseur.setVisible(b);
    }

    public void changeToDessine() {
        curseur.setVisible(true);
        curseur.setImage(new Image("Images/curseurs/mainOuverte.png"));

    }

    public void changeToFingerOn() {
        curseur.setVisible(true);
        curseur.setImage(new Image("Images/curseurs/fingerOn.png"));
    }

    public void changeToHandOpen() {
        curseur.setVisible(true);
        curseur.setImage(new Image("Images/curseurs/mainOuverte.png"));
    }

    public void setPosition(double x, double y) {
        curseurGroup.setTranslateX(x);
        curseurGroup.setTranslateY(y);

    }

    public class Loading {

        private ArcTo arc1, arc2;
        private Timeline time1, time2;
        private Path p1, p2;
        private double param;
        private double rayon = 20;
        

        public Loading() {
            param = Math.PI / 2;
            p1 = new Path(new MoveTo(0, 20));
            p2 = new Path(new MoveTo(0, -20));
            arc2 = new ArcTo();
            arc1 = new ArcTo();
            p1.getElements().add(arc1);
            p2.getElements().add(arc2);
            arc1.setX(rayon * Math.cos(param));
            arc1.setY(-rayon * Math.sin(param));
            arc2.setX(rayon * Math.cos(param));
            arc2.setY(-rayon * Math.sin(param));

            


            
            EventHandler<ActionEvent> firstArc = new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    param -= 0.01;
                    arc1.setX(100 + rayon * Math.cos(param));
                    arc1.setY(150 - rayon * Math.sin(param));
                    if (param <= -Math.PI / 2) {
                        
                        
                        time1.stop();
                        loaderGroup.getChildren().addAll(p2);
                        time2.play();

                    }

                }
            };

            EventHandler<ActionEvent> secondArc = new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    param -= 0.01;
                    arc2.setX(100 + rayon * Math.cos(param));
                    arc2.setY(150 - rayon * Math.sin(param));
                    if (param <= -3*Math.PI / 2) {
                        
                        imgTimer.setVisible(false);
                      
                        time2.stop();

                    }
                }
            };
            
            
            
            KeyFrame keyFrame1 = new KeyFrame(Duration.millis(10), firstArc);
            KeyFrame keyFrame2 = new KeyFrame(Duration.millis(10), secondArc);


            time1 = new Timeline(keyFrame1);
            time1.setCycleCount(Timeline.INDEFINITE);
            time2 = new Timeline(keyFrame2);
            time2.setCycleCount((int)(Math.PI/0.01));
            
            
        }
        
        
        public void playAnimation(){
            loaderGroup.setVisible(true);
            imgTimer.setVisible(true);
            loaderGroup.getChildren().removeAll(loaderGroup.getChildren());
            loaderGroup.getChildren().addAll(p1);
            time1.play();
        }
        
        public void stopAnimation(){
            loaderGroup.setVisible(false);
            imgTimer.setVisible(false);
            time1.stop();
            time2.stop();
            loaderGroup.getChildren().removeAll(loaderGroup.getChildren());
        }
    }
}
