/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;


import javafx.scene.Group;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Path;

/**
 *
 * @author demalejo
 */
public class ManConnectionTool {

    Path path;
    BoxBlur blur;
    ImageView etatConnection;
    boolean connected = false;
    KinectServer bus;

    public ManConnectionTool(Group g, double x, double y, KinectServer server) {
        etatConnection = new ImageView(new Image("Images/connect/manoff.png"));
        bus = server;
       
        DropShadow ds = new DropShadow();
        ds.setWidth(etatConnection.getImage().getHeight() / 2 + 20);
        ds.setHeight(etatConnection.getImage().getHeight() / 2 + 20);

        
        

        etatConnection.setX(x - etatConnection.getImage().getHeight() / 2);
        etatConnection.setY(y - etatConnection.getImage().getWidth() / 2);
        etatConnection.setEffect(ds);
        
        g.getChildren().addAll( etatConnection);

        


    }

    public void connected() {
        connected = true;

        etatConnection.setImage(new Image("Images/connect/manon.png"));


    }



    public void disconnected(){
        connected = false;

        etatConnection.setImage(new Image("Images/connect/manoff.png"));
        
    }
    

    
}
