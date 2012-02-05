/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author jordane
 */
public class Launcher extends Application {

    private Controller c1;
    private Stage stage1;


    

    private void stopApplication() {
        try {
            
            c1.getStage().close();
            c1.stop();
            
        } catch (Exception ex) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
}
    }

    private void startApplication( boolean full) {
        c1 = new Controller(this,full);
        try {
            stage1 = new Stage(StageStyle.DECORATED);
            
            
            c1.start(stage1);
        } catch (Exception ex) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fullScreen(boolean b) {
if (c1.getStage() != null){
        if (b) {
            stopApplication();
            startApplication(b);
        }else{
            stopApplication();
            startApplication(b);
        }
}

    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage arg0) throws Exception {
        startApplication(false);
        

    }
}
