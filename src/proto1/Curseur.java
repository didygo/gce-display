/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import java.awt.event.ActionListener;
import proto2.*;
import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    ImageView curseur,imgTimer;
    PathTransition transition;
    double ancienX = 0;
    double ancienY = 0;
    boolean play = false;
    Duration transitionTime =  Duration.millis(100);
    Group curseurGroup = new Group();
    Timer timer;

    public Curseur(Group g) {
        curseur = new ImageView(new Image("Images/curseurs/mainFermee.png"));
        imgTimer = new ImageView(new Image("Images/curseurs/"));
        curseur.setX(-50);
        curseur.setY(-50);
        curseurGroup.getChildren().add(curseur);
        g.getChildren().addAll(curseurGroup);
        

        
    }

    public void changeToTimer(){
        
    }

    public void changeToLibre() {
        curseur.setVisible(false);
    }

    public void changeToRepos() {
        curseur.setVisible(true);
        curseur.setImage(new Image("Images/curseurs/mainFermee.png"));

    }
    public void changeToHandClose(){
        curseur.setVisible(true);
        curseur.setImage(new Image("Images/curseurs/mainFermee.png"));
    }
    public void setVisible(boolean b){
        curseur.setVisible(b);
    }

    public void changeToDessine() {
        curseur.setVisible(true);
        curseur.setImage(new Image("Images/curseurs/mainOuverte.png"));
        
    }
    public void changeToFingerOn(){
        curseur.setVisible(true);
        curseur.setImage(new Image("Images/curseurs/fingerOn.png"));
    }

    public void changeToHandOpen(){
        curseur.setVisible(true);
        curseur.setImage(new Image("Images/curseurs/mainOuverte.png"));
    } 
    public void setPosition(double x, double y) {
            curseurGroup.setTranslateX(x);
            curseurGroup.setTranslateY(y);

    }

}
