
package proto2;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import proto1.DashBoard;


/**
 *
 * @author demalejo
 */
public class Controller2 extends Application {
    // Variables du système d'affichage

    Group root, cercles;
    ArrayList<CircleObject> circleObjectArray;
    private double kinectPosX = 0;
    private double kinectPosY = 0;
    private Curseur cur;
    private double windowSizeHeight, windowSizeWidth;
    private String adresseBus;
    KinectServer kinectServer;
    ConnectionTool connectionTool;
    ManConnectionTool manConnectionTool;
    private DashBoard dashboard;
    private boolean fullScreen;
    private ParamManager2 param;

    public Controller2(DashBoard d, boolean fullScreen,ParamManager2 param) {
        this.dashboard = d;
        this.fullScreen = fullScreen;
        this.param = param;
        
    }
    
    

    public enum Etats {

        LIBRE, DESSIN, REPOS
    }

    public enum EvenementsKinect {

        CLEAN, KINECT_READY
    }
    private Etats etat;

    //////////////////////////////////////
    private void init(Stage primaryStage) {

        /// 1) Initialisation de la scène graphique//
        if (fullScreen) {
            this.windowSizeWidth = param.windowSizeWidthFull;
            this.windowSizeHeight = param.windowSizeHeightFull;
        } else {
            this.windowSizeWidth = param.windowSizeWidth;
            this.windowSizeHeight = param.windowSizeHeight;
        }
        root = new Group();
        Scene scene = new Scene(root, windowSizeWidth, windowSizeHeight);
        ImageView background = new ImageView(new Image("Images/fonds/ciel.png"));
        //primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        root.getChildren().add(background);

        ////////////////////////////////////////

        /// 2) Initialisation du bus de communication inter logiciel ///
        adresseBus = "169.254.255.255:2010";//"10.3.8.255:2010";
        //////////////////////////////////////////////////////////////

        /// 3) Initialisation des interactions pour prototype II //
        etat = Etats.REPOS;
        circleObjectArray = new ArrayList();
        cercles = new Group();
        root.getChildren().add(cercles);
        activerKinectProto2();
        ////////////////////////////////////////////////////////

        connectionTool = new ConnectionTool(root, windowSizeWidth - 50, windowSizeHeight - 50, kinectServer);
        manConnectionTool = new ManConnectionTool(root, windowSizeWidth - 150, windowSizeHeight - 50, kinectServer);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent arg0) {
                kinectServer.send("IHM_EVENT=END_CONNECTION");
                System.exit(0);
            }
        });
        
        primaryStage.setFullScreen(fullScreen);

    }

    private void activerKinectProto2() {
        activateKinectCursor(); // à faire avant l'activation du receiver
        kinectServer = new KinectServer(this, adresseBus);
        //gestionEvenementsSouris();
    }

    public void changeState(Etats e) {
        etat = e;
        switch (e) {
            case DESSIN:
                if (cur != null) {
                    cur.changeToDessine();
                }
                break;
            case LIBRE:
                if (cur != null) {
                    cur.changeToLibre();
                }
                break;
            case REPOS:
                if (cur != null) {
                    cur.changeToRepos();
                }
                break;


        }

    }

    public void kinectMovePosition(final double x, final double y) {
       Platform.runLater(new Runnable() {

            @Override
            public void run() {
        switch (etat) {
            case DESSIN:

                kinectPosX = x;
                kinectPosY = y;
                repositionnerCurseurKinect();
                superpositionTestPresenceKinect();

                break;
            case LIBRE:

                break;
            case REPOS:
                kinectPosX = x;
                kinectPosY = y;
                repositionnerCurseurKinect();
                break;
}

        }
       });
    }

    public void clean() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
        switch (etat) {
            case DESSIN:


                break;
            case LIBRE:
                allErase();
                break;
            case REPOS:
                allErase();
                break;


        }
            }});
    }

    public void activateKinectCursor() {
        cur = new Curseur(root);
        cur.changeToLibre();
    }

    private void gestionEvenementsSouris() {
        kinectServer.sendToSelf(true);
        root.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                switch (etat) {
                    case DESSIN:


                        break;
                    case LIBRE:
                        if (me.getButton() == MouseButton.MIDDLE) {
                            kinectServer.send("KINECT_STATE=REPOS");
                            kinectServer.send("KINECT_EVENT=CLEAN");
                        }
                        break;
                    case REPOS:
                        if (me.getButton() == MouseButton.PRIMARY) {
                            kinectServer.send("KINECT_STATE=DESSIN");
                        }
                        if (me.getButton() == MouseButton.MIDDLE) {
                            kinectServer.send("KINECT_STATE=LIBRE");
                        }
                        break;


                }

            }
        });

        root.setOnMouseMoved(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                switch (etat) {
                    case DESSIN:

                        break;
                    case LIBRE:
                        kinectServer.send("KINECT_POSITION X=" + (int)((me.getX() * 640 / windowSizeWidth)) + " Y=" + (int)((me.getY() * 480 / windowSizeHeight)));
                        break;
                    case REPOS:
                        kinectServer.send("KINECT_POSITION X=" + (int)((me.getX() * 640 / windowSizeWidth)) + " Y=" + (int)((me.getY() * 480 / windowSizeHeight)));
                        break;


                }
            }
        });

        root.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                switch (etat) {
                    case DESSIN:
                        if (me.getButton() == MouseButton.PRIMARY) {
                            kinectServer.send("KINECT_STATE=REPOS");
                        }
                        break;
                    case LIBRE:

                        break;
                    case REPOS:

                        break;


                }

            }
        });

        root.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                switch (etat) {
                    case DESSIN:
                        kinectServer.send("KINECT_POSITION X=" + (int)((me.getX() * 640 / windowSizeWidth)) + " Y=" + (int)((me.getY() * 480 / windowSizeHeight)));

                        break;
                    case LIBRE:
                        kinectServer.send("KINECT_POSITION X=" + (int)((me.getX() * 640 / windowSizeWidth)) + " Y=" + (int)((me.getY() * 480 / windowSizeHeight)));
                        break;
                    case REPOS:
                        kinectServer.send("KINECT_POSITION X=" + (int)((me.getX() * 640 / windowSizeWidth)) + " Y=" + (int)((me.getY() * 480 / windowSizeHeight)));
                        break;


                }

            }
        });
    }

    private void repositionnerCurseurKinect() {
        cur.setPosition(kinectPosX * windowSizeWidth / (double) 640, kinectPosY * windowSizeHeight / (double) 480);
    }

    private void allErase() {
        for (int i = 0; i < circleObjectArray.size(); i++) {

            cercles.getChildren().remove(i);
            circleObjectArray.remove(i);
            i--;


        }
    }

    private void superpositionTestPresenceKinect() {
        boolean testPresence = false;
        for (int i = 0; i < circleObjectArray.size(); i++) {
            if (circleObjectArray.get(i).hitTestObject(kinectPosX * windowSizeWidth / (double) 640, kinectPosY * windowSizeHeight / (double) 480)) {
                testPresence = true;

                circleObjectArray.get(i).increaseDensity();
            }
        }
        if (!testPresence) {

            circleObjectArray.add(new CircleObject(kinectPosX * windowSizeWidth / (double) 640, kinectPosY * windowSizeHeight / (double) 480, cercles));


        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
