/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author jordane
 */
public class Help {

    private Rectangle background;
    private Group totalGroup;
    private double position = 10;
    private ArrayList<Option> array;

    public enum Etats {

        HAND_CLOSE, HAND_OPEN, FINGER
    }

    public Help(double windowX, double windowY) {
        array = new ArrayList<>();
        this.totalGroup = new Group();
        this.background = new Rectangle(windowX, 100, Color.web("#000000", 0.4));
        totalGroup.getChildren().add(background);
    }

    public void addImg(String s, Etats e) {
        array.add(new Option(s, e));
    }

    public Group getHelp() {
        return totalGroup;
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
            totalGroup.getChildren().add(img);
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
}
