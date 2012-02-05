/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author jordane
 */
public class Launcher extends Application {

    private Controller1 c1;
    private Stage stage;
    private double windowNormalSizeX = 800;
    private double windowNormalSizeY = 600;
    private double windowFullSizeX = 1440;
    private double windowFullSizeY = 900;

    public void init(Stage stage) {
        this.stage = stage;
    }

    private void stopAppli() {
        try {
            
            stage.close();
            c1.stop();
            
        } catch (Exception ex) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
}
    }

    private void startAppli(double x, double y, boolean full) {
        c1 = new Controller1(x, y,this,full);
        try {
            c1.start(stage);
        } catch (Exception ex) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fullScreen(boolean b) {
if (stage != null){
        if (b) {
            stopAppli();
            startAppli(windowFullSizeX, windowFullSizeY,b);
        }else{
            stopAppli();
            startAppli(windowNormalSizeX, windowNormalSizeY,b);
        }
}

    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage arg0) throws Exception {
        init(arg0);
        startAppli(windowNormalSizeX, windowNormalSizeY,false);
        

    }
}
