/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
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
    private ParamManager param;

    public Curseur(Group g, ParamManager param) {
        loading = new Loading();
        this.param = param;
        curseur = new ImageView(new Image("Images/curseurs/mainFermee.png"));
        imgTimer = new ImageView(new Image("Images/curseurs/neutre.png"));
        curseur.setX(-50);
        curseur.setY(-50);
        imgTimer.setX(-50);
        imgTimer.setY(-50);
        curseurGroup.getChildren().addAll(curseur,imgTimer,loaderGroup);
        imgTimer.setVisible(false);
        g.getChildren().addAll(curseurGroup);



    }
    
    

    public void startTimer() {
        loading = new Loading();
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
        private double p;
        private double rayon = 20;
        

        public Loading() {
            BoxBlur box = new BoxBlur(3, 3, 10);
            p = Math.PI / 2;
            p1 = new Path();
            p1.getElements().add(new MoveTo(0, -rayon));
            p2 = new Path();
            p2.getElements().add(new MoveTo(0, rayon));
            arc2 = new ArcTo();
            arc1 = new ArcTo();
            p1.getElements().add(arc1);
            p2.getElements().add(arc2);
            arc1.setX(rayon * Math.cos(p));
            arc1.setY(-rayon * Math.sin(p));
            arc1.setRadiusX(rayon);
            arc1.setRadiusY(rayon);
            arc2.setX(rayon * Math.cos(-p));
            arc2.setY(-rayon * Math.sin(-p));
            arc2.setRadiusX(rayon);
            arc2.setRadiusY(rayon);
            arc1.setSweepFlag(true);
            arc2.setSweepFlag(true);
            
            //style
            p1.setStrokeWidth(8);
            p1.setStroke(Color.WHITE);
            p1.setEffect(box);
            p2.setStrokeWidth(8);
            p2.setEffect(box);
            p2.setStroke(Color.WHITE);
            p1.setStrokeLineCap(StrokeLineCap.ROUND);
            p2.setStrokeLineCap(StrokeLineCap.ROUND);

            


            
            EventHandler<ActionEvent> firstArc = new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    p -= Math.PI*20/(param.timerDuration);
                    arc1.setX(rayon * Math.cos(p));
                    arc1.setY(-rayon * Math.sin(p));
                    if (p <= -Math.PI / 2) {
                        
                        
                        time1.stop();
                        loaderGroup.getChildren().addAll(p2);
                        time2.play();

                    }

                }
            };

            EventHandler<ActionEvent> secondArc = new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    p -= Math.PI*20/(param.timerDuration);
                    arc2.setX(rayon * Math.cos(p));
                    arc2.setY(-rayon * Math.sin(p));
                    if (p <= -3*Math.PI / 2) {
                        
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
