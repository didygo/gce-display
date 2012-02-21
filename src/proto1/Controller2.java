package proto1;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.Timer;

/**
 *
 * @author demalejo
 */
public class Controller2 extends Application implements Control {
    //le gestionnaire des paramètres

    private ParamManager param;
    // le gestionnaire du logiciel
    private DashBoard dashboard;
    //variable pour définir si l'affichage est en fullscreen
    private boolean fullScreen;
    // variables de la scène graphique
    private Stage stage;
    private Scene scene;
    private Group root, cercles, menu;
    private ArrayList<CircleObject> circleObjectArray;
    private ImageView background;
    // variables sur le dimensionnement
    private double kinectPosX = 0;
    private double kinectPosY = 0;
    private double kinectPosZ = 0;
    private double windowSizeWidth = 800;
    private double windowSizeHeight = 600;
    private double kinectWindowSizeWidth, kinectWindowSizeHeight;
    // bus de communication
    private String adresseBus;
    private KinectServer kinectServer;
    ConnectionTool connectionTool;
    ManConnectionTool manConnectionTool;
    // varaible de sélection : -1 pour le panier , -2 pour rien , 0..infini pour les sunshader
    int illuminateIndex = -2;
    

    //le curseur
    Curseur curseur;
    //le menu d'aide
    Help help;


    public Controller2(DashBoard d, boolean fullScreen, ParamManager param) {
        this.param = param;
        this.dashboard = d;
        this.fullScreen = fullScreen;
        if (fullScreen) {
            this.windowSizeWidth = param.windowSizeWidthFull;
            this.windowSizeHeight = param.windowSizeHeightFull;
        } else {
            this.windowSizeWidth = param.windowSizeWidth;
            this.windowSizeHeight = param.windowSizeHeight;
        }
    }

    public enum Etats {

        LIBRE, DESSIN, REPOS
    }
    private Etats etat;

    public Stage getStage() {
        return stage;
    }

    //////////////////////////////////////
    private void init(Stage primaryStage) {
        this.stage = primaryStage;
        /// Initialisation de la scène graphique//
        this.kinectWindowSizeWidth = param.kinectWindowWidth;
        this.kinectWindowSizeHeight = param.kinectWindowHeight;
        this.root = new Group();
        this.scene = new Scene(root, windowSizeWidth, windowSizeHeight);
        primaryStage.setScene(scene);
        loadBackground();
        manageEvents();

        initComponentsProto();
        this.stage.setFullScreen(fullScreen);
    }

    // initilaise le bus logiciel, et les composants graphiques
    public void initComponentsProto() {




        /// 2) Initialisation du bus de communication inter logiciel ///
        this.adresseBus = "169.254.255.255:2010";
        this.kinectServer = new KinectServer(this, adresseBus, windowSizeWidth, windowSizeHeight, param);
        //////////////////////////////////////////////////////////////
        //gestionEvenementsSouris(scene);
        /// 3) Initialisation des interactions pour prototype I //
        this.etat = Etats.REPOS;

        this.circleObjectArray = new ArrayList();
        this.cercles = new Group();
        this.menu = new Group();
        this.root.getChildren().addAll(cercles, menu);
        ////////////////////////////////////////////////////////

        this.connectionTool = new ConnectionTool(root, windowSizeWidth - 50, windowSizeHeight - 50, kinectServer);
        this.manConnectionTool = new ManConnectionTool(root, windowSizeWidth - 150, windowSizeHeight - 50, kinectServer);


        help = new Help(windowSizeWidth, windowSizeHeight);
        help.allVisible(true);
        help.helpVisible(false);
        help.handWaveSetVisible(false);
                

        root.getChildren().addAll(help.getHelp());

        this.curseur = new Curseur(root, param);
        this.curseur.setVisible(false);

        stage.setFullScreen(fullScreen);
        
        
        //simuler connection kinect
        //kinectconnection(true);
        //userDetected();
    }

    private void loadBackground() {
        this.background = new ImageView(new Image("Images/fonds/ciel2.jpg"));

        this.root.getChildren().add(background);
        double dw = windowSizeWidth / background.getImage().getWidth();
        double dh = windowSizeHeight / background.getImage().getHeight();
        background.setScaleX(dw);
        background.setScaleY(dh);

        background.setX(background.getImage().getWidth() * (dw - 1) / 2);
        background.setY(background.getImage().getHeight() * (dh - 1) / 2);
    }

    private void manageEvents() {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent arg0) {
                kinectServer.send("IHM_EVENT=END_CONNECTION");
                dashboard.stopApplication();
            }
        });

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(final KeyEvent arg0) {
                if (arg0.getCode() == KeyCode.F) {
                    kinectServer.send("IHM_EVENT=END_CONNECTION");
                    kinectServer.disconnect();
                    if (getStage().isFullScreen()) {
                        dashboard.fullScreen(false);
                    } else {
                        dashboard.fullScreen(true);
                    }
                }
                if (arg0.getCode() == KeyCode.H) {
                    if (help.isVisible()) {
                        help.allVisible(false);
                    } else {
                        help.allVisible(true);
                    }
                }
            }
        });
    }

    /*
     * ----------------------------------------------------------------------
     */
    /*
     * ------------------------- State Machine Proto 2 ---------------------
     */
    public void kinectconnection(final boolean b) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                help.handWaveSetVisible(b);
                if (b) {
                    connectionTool.connected();
                } else {
                    connectionTool.disconnected();
                }
            }
        });
    }

    public void handOpened() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                switch (etat) {
                    case DESSIN:
                        etat = Etats.LIBRE;
                        handToOpen();
                        

                        break;
                    case LIBRE:


                        break;
                    case REPOS:

                        break;

                }
            }
        });
    }

    public void handClosed() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                switch (etat) {
                    case DESSIN:

                        break;
                    case LIBRE:
                        etat = Etats.DESSIN;
                        handToClose();

                        break;
                    case REPOS:

                        break;

                }
            }
        });
    }

    public void handMove(final double x, final double y, final double z) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                //System.out.println(state);
                kinectPosX = x;
                kinectPosY = y;
                kinectPosZ = z;
                switch (etat) {

                    case DESSIN:
                        superpositionTestPresenceKinect();
                        curseur.setPosition(x, y, z);
                        break;
                    case LIBRE:
                        curseur.setPosition(x, y, z);
                        break;
                    case REPOS:

                        break;

                }
            }
        });
    }

    public void pushHand() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                switch (etat) {
                    case DESSIN:
                        etat = Etats.REPOS;
                        break;
                    case LIBRE:
                        etat = Etats.REPOS;
                        break;
                    case REPOS:

                        break;

                }
            }
        });
    }

    public void userDetected() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                allErase();
                switch (etat) {
                    case DESSIN:

                        break;
                    case LIBRE:

                        break;
                    case REPOS:
                        etat = Etats.LIBRE;
                        startUser();
                        break;

                }
            }
        });
    }

    public void userLost() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                stopUser();
                switch (etat) {
                    case DESSIN:
                        etat = Etats.REPOS;
                        break;
                    case LIBRE:
                        etat = Etats.REPOS;
                        break;
                    case REPOS:

                        break;

                }
            }
        });
    }

    public void fingerAngle(final double angle) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                switch (etat) {
                    case DESSIN:
                        handToFinger();
                        break;
                    case LIBRE:
                        handToFinger();
                        break;
                    case REPOS:

                        break;

                }
            }
        });
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
            if (circleObjectArray.get(i).hitTestObject(kinectPosX, kinectPosY)) {
                testPresence = true;
                circleObjectArray.get(i).increaseDensity();
            }
        }
        if (!testPresence) {

            circleObjectArray.add(new CircleObject(kinectPosX, kinectPosY, cercles, param));


        }
    }

    private void stopUser() {

        help.handWaveSetVisible(true);
        manConnectionTool.disconnected();
        curseur.setVisible(false);

    }

    private void startUser() {
        help.handWaveSetVisible(false);
        manConnectionTool.connected();
        curseur.setVisible(true);
        curseur.changeToHandOpen();

    }

    private void handToOpen() {

        curseur.changeToHandOpen();
    }

    private void handToClose() {

        curseur.changeToHandClose();
    }

    private void handToFinger() {

        curseur.changeToFingerOn();
    }

    // quand on a la main ouverte -1
    private void gestionEvenementsSouris(Scene scene) {
        kinectServer.sendToSelf(true);
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                kinectServer.send("KINECT_HAND_OPENED=false");
            }
        });

        scene.setOnMouseMoved(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                // System.out.println("KINECT_POSITION SEND X=" + (int) (me.getX()*kinectWindowSizeX/windowSizeX) + " Y=" + (int) (me.getY()*kinectWindowSizeY/windowSizeY ));
                kinectServer.send("KINECT_POSITION X=" + (int) (me.getX() * kinectWindowSizeWidth / windowSizeWidth) + " Y=" + (int) (me.getY() * kinectWindowSizeHeight / windowSizeHeight) + " Z=" + (int) (me.getX() * (kinectWindowSizeWidth + 300) / windowSizeWidth));
            }
        });

        scene.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                kinectServer.send("KINECT_HAND_OPENED=true");
            }
        });

        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                kinectServer.send("KINECT_POSITION X=" + (int) (me.getX() * kinectWindowSizeWidth / windowSizeWidth) + " Y=" + (int) (me.getY() * kinectWindowSizeHeight / windowSizeHeight) + " Z=" + (int) (me.getX() * (kinectWindowSizeWidth + 300) / windowSizeWidth));
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        init(primaryStage);
        primaryStage.show();
    }
}
