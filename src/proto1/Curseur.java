/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

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

/**
 *
 * @author demalejo
 */
public class Curseur {

    ImageView curseur;
    PathTransition transition;
    Path path;
    Group dede = new Group();
    double ancienX = 0;
    double ancienY = 0;
    boolean play = false;
    Duration transitionTime =  Duration.millis(50);
    Group curseurGroup = new Group();

    public Curseur(Group g) {
        curseur = new ImageView(new Image("Images/curseurs/mainFermee.png"));
        curseur.setX(-50);
        curseur.setY(-50);
        path = new Path();
        path.setVisible(true);
        transition = new PathTransition();
        transition.setNode(curseurGroup);
        transition.setDuration(transitionTime);
        transition.setPath(path);
        curseurGroup.getChildren().add(curseur);
        g.getChildren().addAll(curseurGroup, path);
        
        
       

        
    }

    ;

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
    public void setPositionKinect(double x, double y) {

            ancienX = curseurGroup.getTranslateX();
            ancienY = curseurGroup.getTranslateY();
            path.getElements().removeAll(path.getElements());
            path.getElements().add(new MoveTo(ancienX, ancienY));
            path.getElements().add(new LineTo(x, y));
            ancienX = x;
            ancienY = y;
            transition.play();
      

    }
}
