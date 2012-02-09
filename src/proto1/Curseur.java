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
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;
import javax.swing.Timer;

/**
 *
 * @author demalejo
 */
public class Curseur {

    private ImageView curseur,imgTimer;
    private PathTransition transition;
    private double ancienX = 0;
    private double ancienY = 0;
    private boolean play = false;
    private Duration transitionTime = Duration.millis(100);
    private Group loaderGroup = new Group();
    private Group curseurGroup = new Group();
    private Timer timer;
    private Circle backCircleGreen, backCircleRed;
    private Loading loading;
    private ParamManager param;

    public Curseur(Group g, ParamManager param) {
        loading = new Loading();
        this.param = param;
        backCircleGreen = new Circle(0, 0, 50, Color.web("#5dac26"));
        backCircleGreen.setScaleX(0.98);
        backCircleGreen.setScaleY(0.98);
        backCircleRed = new Circle(0, 0, 50, Color.web("#b70e22"));
        backCircleRed.setScaleX(0.98);
        backCircleRed.setScaleY(0.98);
        backCircleGreen.setBlendMode(BlendMode.HARD_LIGHT);
        backCircleRed.setBlendMode(BlendMode.HARD_LIGHT);
        curseur = new ImageView(new Image("Images/curseurs/mainOuverte_transparent.png"));
        imgTimer = new ImageView(new Image("Images/curseurs/transparent.png"));
        curseur.setX(-50);
        curseur.setY(-50);
        imgTimer.setX(-50);
        imgTimer.setY(-50);
        curseurGroup.getChildren().addAll(backCircleGreen, backCircleRed, curseur,imgTimer, loaderGroup);
        imgTimer.setVisible(false);
        g.getChildren().addAll(curseurGroup);



    }

    public void startTimer() {
        loading = new Loading();
        loading.playAnimation();
    }

    public void stopTimer() {
        loading.stopAnimation();
    }

    public void changeToLibre() {
        curseur.setVisible(false);
    }

    public void changeToHandClose() {
        curseur.setVisible(true);
        curseur.setImage(new Image("Images/curseurs/mainFermee_transparent.png"));
    }

    public void setVisible(boolean b) {
        curseur.setVisible(b);
    }

    public void changeToFingerOn() {
        curseur.setVisible(true);
        curseur.setImage(new Image("Images/curseurs/mainDoigt_transparent.png"));
    }

    public void changeToHandOpen() {
        curseur.setVisible(true);
        curseur.setImage(new Image("Images/curseurs/mainOuverte_transparent.png"));
    }

    public void setPosition(double x, double y, double z) {
        curseurGroup.setTranslateX(x);
        curseurGroup.setTranslateY(y);
        changeColorDepth(z);



    }

    private void changeColorDepth(double z) {
        if (z <= param.minDepthIHM + 50) {
            
            backCircleRed.setOpacity((param.minDepthIHM + 50 - z) / 50);
        } else if (z >= param.maxDepthIHM - 150) {
            backCircleRed.setOpacity((z - param.maxDepthIHM + 150) / 150);
        }else if ( z > param.minDepthIHM + 50 && z < param.maxDepthIHM - 150){
            backCircleRed.setOpacity(0);
        }
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
                    p -= Math.PI * 20 / (param.timerDuration);
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
                    p -= Math.PI * 20 / (param.timerDuration);
                    arc2.setX(rayon * Math.cos(p));
                    arc2.setY(-rayon * Math.sin(p));
                    if (p <= -3 * Math.PI / 2) {

                        curseur.setVisible(true);
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
            time2.setCycleCount((int) (Math.PI / 0.01));


        }

        public void playAnimation() {
            loaderGroup.setVisible(true);
            curseur.setVisible(false);
            imgTimer.setVisible(true);
            loaderGroup.getChildren().removeAll(loaderGroup.getChildren());
            loaderGroup.getChildren().addAll(p1);
            time1.play();
        }

        public void stopAnimation() {
            loaderGroup.setVisible(false);
            curseur.setVisible(true);
            imgTimer.setVisible(false);
            time1.stop();
            time2.stop();
            loaderGroup.getChildren().removeAll(loaderGroup.getChildren());
        }
    }
}
