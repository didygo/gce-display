/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import javafx.animation.PathTransition;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

/**
 *
 * @author jordane
 */
public class CircleBasket {

    ImageView basketImg;
    PathTransition transition;
    Path pathReturn;
    Path pathGo;
    
    //variables de position
    private double basketX,basketY;
  

    private enum Etats {

        OPEN_SUN, OPEN_EMPTY, CLOSE_FULL, CLOSE_EMPTY
    }
    private Etats etat = Etats.CLOSE_FULL;

    public CircleBasket(Group g, double x, double y) {
        basketX = 100;
        basketY = 200;
        basketImg = new ImageView(new Image("Images/tiroir/tiroirPlein.png"));
        basketImg.setX(x);
        basketImg.setY(200);
        

        pathGo = new Path();
        pathGo.getElements().add(new MoveTo(x, basketY));
        pathGo.getElements().add(new LineTo(x - basketX, basketY));
        pathGo.setVisible(false);

        pathReturn = new Path();
        pathReturn.getElements().add(new MoveTo(x - basketX, basketY));
        pathReturn.getElements().add(new LineTo(x, basketY));
        pathReturn.setVisible(false);




        transition = new PathTransition();
        transition.setPath(pathGo);
        transition.setNode(basketImg);
        transition.setDuration(Duration.millis(200));
        
        g.getChildren().addAll(basketImg, pathGo, pathReturn);
        closeWithSun();
    }
    
    
    // je metz un sushader dans le panier ouvert
    public void sunDroped(){
        basketImg.setImage(new Image("Images/tiroir/tiroirPlein.png"));
        etat = Etats.OPEN_SUN;
    }
    // je prends un Sunshader dans le panier ouvert
    public void sunCaught(){
            basketImg.setImage(new Image("Images/tiroir/tiroirVide.png"));
        etat = Etats.OPEN_EMPTY;
    }
     // le panier fermé devient fermé et vide
    public void makeItEmpty(){
        etat = Etats.CLOSE_EMPTY;
        basketImg.setImage(new Image("Images/tiroir/tiroirVide.png"));
    }
    
    
    // le panier fermé devient fermé et contient un Sunshader
    public void makeItFull(){
        etat = Etats.CLOSE_FULL;
        basketImg.setImage(new Image("Images/tiroir/tiroirPlein.png"));
    }
    public void handIn() {
        switch (etat) {
            case CLOSE_EMPTY:
                transition.stop();
                openWithoutSun();
                etat = Etats.OPEN_EMPTY;
                break;
            case CLOSE_FULL:
                transition.stop();
                openWithSun();
                etat= Etats.OPEN_SUN;
                break;
            case OPEN_EMPTY:
                break;
            case OPEN_SUN:
                break;

        }
    }

    public void handOut() {
        switch (etat) {
            case CLOSE_EMPTY:
                break;
            case CLOSE_FULL:
                break;
            case OPEN_EMPTY:
                transition.stop();
                closeWithoutSun();
                etat = Etats.CLOSE_EMPTY;
                break;
            case OPEN_SUN:
                transition.stop();
                closeWithSun();
                etat = Etats.CLOSE_FULL;
                break;

        }
    }
   
    

    private void openWithSun() {
        transition.stop();
        //changer l'image
        basketImg.setImage(new Image("Images/tiroir/tiroirPlein.png"));
        transition.setPath(pathGo);
        transition.play();
    }

    private void openWithoutSun() {
        transition.stop();
        //changer l'image
        basketImg.setImage(new Image("Images/tiroir/tiroirVide.png"));
        transition.setPath(pathGo);
        transition.play();
    }
    private void closeWithoutSun() {
        transition.stop();
        //changer l'image
        basketImg.setImage(new Image("Images/tiroir/tiroirVide.png"));
        transition.setPath(pathReturn);
        transition.play();
    }
    private void closeWithSun() {
        transition.stop();
        //changer l'image
        basketImg.setImage(new Image("Images/tiroir/tiroirPlein.png"));
        transition.setPath(pathReturn);
        transition.play();
    }

    

    public boolean proximity(double x, double y) {
       
       return (x>basketImg.getX()-basketImg.getImage().getWidth()/2-100  && y<basketImg.getY() + basketImg.getImage().getWidth()/2  && y>basketImg.getY() - basketImg.getImage().getWidth()/2 );
        
    }
    
    public void hide(){
        basketImg.setVisible(false);
    }
    public void show(){
        basketImg.setVisible(true);
    }
}
