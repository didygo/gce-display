/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto2;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import javax.swing.Timer;

/**
 *
 * @author demalejo
 */
public class ConnectionTool {

    PathTransition pt;
    double circleX = 0;
    double circleY = 0;
    Circle circ,circExt;
    Path path;
    BoxBlur blur;
    ImageView etatConnection;
    boolean connected = false;
    KinectServer bus;
    double amplitude;
    Group circG = new Group();
    Timer timer;

    public ConnectionTool(Group g, double x, double y, KinectServer server) {
        etatConnection = new ImageView(new Image("Images/connect/kinectOff.png"));
        bus = server;
        circleX = x;
        circleY = y - etatConnection.getImage().getHeight() / 2 - 5;
        amplitude = etatConnection.getImage().getHeight() + 10;
        DropShadow ds = new DropShadow();
        ds.setWidth(etatConnection.getImage().getHeight() / 2 + 20);
        ds.setHeight(etatConnection.getImage().getHeight() / 2 + 20);

        
        timer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bus.send("IHM_EVENT=ASK_CONNECTION");
            }
        });

        etatConnection.setX(x - etatConnection.getImage().getHeight() / 2);
        etatConnection.setY(y - etatConnection.getImage().getWidth() / 2);
        etatConnection.setEffect(ds);
        circ = new Circle(circleX, circleY, 3);
        circExt = new Circle(circleX, circleY, 5);
        circG.getChildren().addAll(circExt,circ);
        blur = new BoxBlur(4, 4, 5);
        circExt.setEffect(blur);
        circ.setFill(Color.WHITE);
        circ.setVisible(false);
        circExt.setVisible(false);
        circExt.setFill(Color.BLACK);
        createPathTransition();
        g.getChildren().addAll(circG, path, etatConnection);
        initConnection();

        etatConnection.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                if (connected) {
                    disconnect();
                } else {
                    initConnection();
                }
            }
        });


    }

    public void connected() {
        timer.stop();
        pt.stop();
        circ.setVisible(false);
        circExt.setVisible(false);
        etatConnection.setImage(new Image("Images/connect/kinectOn.png"));
        connected = true;

    }

    private void disconnect() {
    }

    public void disconnected(){
        connected = false;
        etatConnection.setOpacity(1);
        etatConnection.setImage(new Image("Images/connect/kinectOff.png"));
        initConnection();
        
        
    }
    private void createPathTransition() {
        path = new Path();
        path.getElements().add(new MoveTo(circleX, circleY));
        path.getElements().add(new CubicCurveTo(circleX, circleY, circleX + amplitude / 2, circleY, circleX + amplitude / 2, circleY + amplitude / 2));
        path.getElements().add(new CubicCurveTo(circleX + amplitude / 2, circleY + amplitude / 2, circleX + amplitude / 2, circleY + amplitude, circleX, circleY + amplitude));
        path.getElements().add(new CubicCurveTo(circleX, circleY + amplitude, circleX - amplitude / 2, circleY + amplitude, circleX - amplitude / 2, circleY + amplitude / 2));
        path.getElements().add(new CubicCurveTo(circleX - amplitude / 2, circleY + amplitude / 2, circleX - amplitude / 2, circleY, circleX, circleY));
        path.setVisible(false);


        pt = new PathTransition();
        pt.setDuration(Duration.seconds(2));
        pt.setPath(path);
        pt.setNode(circG);
        
        pt.setCycleCount(Timeline.INDEFINITE);

    }

    public void initConnection() {
        circ.setVisible(true);
        circExt.setVisible(true);
        etatConnection.setOpacity(0.4);
        pt.play();
        timer.start();;
        
        
    }
}
