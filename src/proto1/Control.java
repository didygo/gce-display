/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import javafx.stage.Stage;

/**
 *
 * @author jordane
 */
public interface Control  {

    public void kinectconnection(boolean b);

    public void userDetected();

    public void userLost();

    public void pushHand();

    public void handOpened();

    public void handClosed();

    public void handMove(double x, double y, double z);

    public void fingerAngle(double toRadians);

    public Stage getStage();

    public void stop() throws Exception;

    public void start(Stage stage) throws Exception;
    
}
