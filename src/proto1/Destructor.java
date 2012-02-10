/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author jordane
 */
public class Destructor {

    private Group totalGroup;
    private ImageView img;
    private int nb_sun = 0;
    private double proximity = 50;

    public Destructor(double windowWidth, double windowHeight, ParamManager param) {
        this.totalGroup = new Group();
        this.img = new ImageView(new Image("Images/destructor/trash0.png"));
        //on place l'image en son centre'
        this.img.setX(-img.getImage().getWidth() / 2);
        this.img.setY(-img.getImage().getHeight() / 2);

        totalGroup.getChildren().add(img);
        
        totalGroup.setLayoutX(windowWidth - 60);
        totalGroup.setLayoutY(windowHeight - 180);
    }

    public void addSun() {
        if (nb_sun < 3) {
            nb_sun++;
            img.setImage(new Image("Images/destructor/trash" + nb_sun + ".png"));
        }

    }
    public void setVisible(boolean b){
        totalGroup.setVisible(b);
    }

    public Group getDestructor() {
        return totalGroup;
    }

    public boolean proximity(double x, double y) {
        return (x > totalGroup.getLayoutX() - img.getImage().getWidth() / 2 - proximity
                && x < totalGroup.getLayoutX() + img.getImage().getWidth() / 2 + proximity
                && y < totalGroup.getLayoutY() + img.getImage().getHeight() / 2 + proximity
                && y > totalGroup.getLayoutY() - img.getImage().getHeight() / 2 - proximity);
    }
}
