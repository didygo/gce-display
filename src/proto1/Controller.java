/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

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

/**
 *
 * @author demalejo
 */
public class Controller extends Application {
    //le gestionnaire des paramètres

    private ParamManager param;
    // le gestionnaire du logiciel
    private Launcher launcher;
    //variable pour définir si l'affichage est en fullscreen
    private boolean fullScreen;
    // variables de la scène graphique
    private Stage stage;
    private Scene scene;
    private Group root, cercles, menu;
    private ArrayList<CircleObject> circleObjectArray;
    private ImageView background;
    // variables sur le dimensionnemen
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
    //panier à sunshader
    CircleBasket basket;
    // varaible de sélection : -1 pour le panier , -2 pour rien , 0..infini pour les sunshader
    int illuminateIndex = -2;
    //variables pour le resize 
    double limitLeft, limitRight;
    double distance2handsKinect;
    double kinectPosXResize, kinectPosYResize;
    double segmentSizeResize = 10;
    //variables pour le change opacity
    double limitBack, limitFront;
    double distanceZkinect;
    double opacityGuard;
    double kinectPosXOpacity, kinectPosYOpacity;
    double segmentSizeOpacity = 2;
    //variable pour demultiplier le deplacement
    double multTranslation;
    //les TiltMenu
    TiltMenu tmenu;
    //le curseur
    Curseur curseur;
    //le menu d'aide
    Help help;
    //le pipe pour le size
    Pipe pipeSize;

    public Controller(Launcher l, boolean fullScreen) {
        this.param = new ParamManager();
        this.launcher = l;
        this.fullScreen = fullScreen;
        if (fullScreen) {
            this.windowSizeWidth = param.windowSizeWidthFull;
            this.windowSizeHeight = param.windowsizeHeightFull;
        } else {
            this.windowSizeWidth = param.windowSizeWidth;
            this.windowSizeHeight = param.windowsizeHeight;
        }



    }

    public enum States {

        SUPER_FREE, FREE, ON_SUN, SUN_SELECTED, CHANGE_SIZE, CHANGE_OPACITY, MENU, ON_CREATOR, ON_DESTRUCTOR
    }
    private States state;

    public enum HandState {

        CLOSE, OPEN, FINGER
    }
    private HandState handState;

    public Stage getStage() {
        return stage;
    }

    //////////////////////////////////////
    private void init(Stage primaryStage) {

        this.stage = primaryStage;

        /// init des variables
        this.distance2handsKinect = 100;
        this.limitLeft = 90;
        this.limitRight = 110;
        this.kinectPosXResize = 0;
        this.kinectPosYResize = 0;
        this.multTranslation = param.constantMultMove;

        // Gardes pour le resize et l'opacity
        this.distanceZkinect = 100;
        this.limitBack = 110;
        this.limitFront = 90;
        this.opacityGuard = 30;
        this.kinectPosXOpacity = 0;
        this.kinectPosYOpacity = 0;

        /// Initialisation de la scène graphique//
        this.kinectWindowSizeWidth = param.kinectWindowWidth;
        this.kinectWindowSizeHeight = param.kinectWindowHeight;
        this.root = new Group();
        this.scene = new Scene(root, windowSizeWidth, windowSizeHeight);


        this.background = new ImageView(new Image("Images/fonds/ciel4.jpg"));
        primaryStage.setScene(scene);
        this.root.getChildren().add(background);
        ////////////////////////////////////////

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent arg0) {
                kinectServer.send("IHM_EVENT=END_CONNECTION");
                System.exit(0);
            }
        });

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(final KeyEvent arg0) {

                if (arg0.getCode() == KeyCode.F) {
                    kinectServer.send("IHM_EVENT=END_CONNECTION");
                    kinectServer.disconnect();
                    if (getStage().isFullScreen()) {
                        launcher.fullScreen(false);
                    } else {
                        launcher.fullScreen(true);
                    }
                }
                if (arg0.getCode() == KeyCode.H) {
                    if (help.isVisible()) {
                        help.helpVisible(false);
                    } else {
                        help.helpVisible(true);
                    }
                }
            }
        });
        this.stage.setFullScreen(fullScreen);
        initComponents();
    }

    // initilaise le bus logiciel, et les composants graphiques
    public void initComponents() {
        /// 2) Initialisation du bus de communication inter logiciel ///
        this.adresseBus = "127.255.255.255:2010";
        this.kinectServer = new KinectServer(this, adresseBus, windowSizeWidth, windowSizeHeight);
        //////////////////////////////////////////////////////////////
        //gestionEvenementsSouris(scene);
        /// 3) Initialisation des interactions pour prototype I //
        this.state = States.FREE;
        this.basket = new CircleBasket(root, windowSizeWidth, windowSizeHeight, param);

        this.circleObjectArray = new ArrayList();
        this.cercles = new Group();
        this.menu = new Group();
        this.root.getChildren().addAll(cercles, menu);
        ////////////////////////////////////////////////////////

        this.connectionTool = new ConnectionTool(root, windowSizeWidth - 50, windowSizeHeight - 50, kinectServer);
        this.manConnectionTool = new ManConnectionTool(root, windowSizeWidth - 150, windowSizeHeight - 50, kinectServer);


        this.curseur = new Curseur(root);
        handToOpen();



        ImageView v = new ImageView(new Image("Images/curseurs/"));
        v.setX(200);
        v.setY(200);



        help = new Help(windowSizeWidth, windowSizeHeight);
        help.addImg("Images/help/doigtGris.png", Help.Etats.FINGER);
        help.addImg("Images/help/mainFermeeGris.png", Help.Etats.HAND_CLOSE);
        help.addImg("Images/help/mainOuverteGris.png", Help.Etats.HAND_OPEN);

        root.getChildren().addAll(v, help.getHelp());

        //primaryStage.setFullScreen(true);

        root.getChildren().add(new Config().getConfig());
    }

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
                switch (state) {
                    case SUPER_FREE:
                        break;
                    case FREE:
                        handToOpen();
                        break;
                    case ON_SUN:
                        handToOpen();
                        help.illuminateOptions(Help.Etats.HAND_CLOSE);
                        break;
                    case SUN_SELECTED:
                        state = States.ON_SUN;
                        onSunShader();
                        handToOpen();
                        help.illuminateOptions(Help.Etats.HAND_CLOSE);
                        break;
                    case MENU:
                        state = States.ON_SUN;
                        onSunShader();
                        handToOpen();
                        help.illuminateOptions(Help.Etats.HAND_CLOSE);
                        break;
                    case CHANGE_SIZE:
                        state = States.ON_SUN;
                        onSunShader();
                        handToOpen();
                        help.illuminateOptions(Help.Etats.HAND_CLOSE);
                        break;
                    case CHANGE_OPACITY:
                        state = States.ON_SUN;
                        onSunShader();
                        handToOpen();
                        help.illuminateOptions(Help.Etats.HAND_CLOSE);
                        break;
                    case ON_CREATOR:
                        handToOpen();
                        help.illuminateOptions(Help.Etats.HAND_CLOSE);
                        break;
                    case ON_DESTRUCTOR:
                        handToOpen();
                        break;

                }
            }
        });
    }

    public void handClosed() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                switch (state) {
                    case SUPER_FREE:
                        break;
                    case FREE:
                        handToClose();
                        break;
                    case ON_SUN:
                        handToClose();
                        help.illuminateOptions(Help.Etats.HAND_OPEN, Help.Etats.FINGER);
                        state = States.SUN_SELECTED;
                        selectSunShader();
                        break;
                    case SUN_SELECTED:
                        break;
                    case MENU:
                        handToClose();
                        help.illuminateOptions(Help.Etats.HAND_OPEN, Help.Etats.FINGER);
                        switch (tmenu.selected()) {
                            case OPACITY:
                                initOpacity();
                                state = States.CHANGE_OPACITY;
                                break;
                            case SIZE:
                                initSize();
                                state = States.CHANGE_SIZE;
                                break;
                            case CANCEL:
                                state = States.SUN_SELECTED;
                                break;
                        }
                        break;
                    case CHANGE_SIZE:
                        break;
                    case CHANGE_OPACITY:
                        break;
                    case ON_CREATOR:
                        handToClose();
                        help.illuminateOptions(Help.Etats.HAND_OPEN, Help.Etats.FINGER);
                        state = States.SUN_SELECTED;
                        createAndSelectSunShader();
                        break;
                    case ON_DESTRUCTOR:
                        handToClose();
                        break;

                }
            }
        });
    }

    public void handMove(final double x, final double y, final double z) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                switch (state) {
                    case SUPER_FREE:
                        break;
                    case FREE:
                        move(x, y, z);
                        curseur.setPosition(kinectPosX, kinectPosY);
                        if (illuminateIndex == -1) {
                            state = States.ON_CREATOR;
                            onCreator();
                        } else if (illuminateIndex >= 0) {
                            state = States.ON_SUN;
                            onSunShader();
                        }
                        break;
                    case ON_SUN:
                        int tempIndex = illuminateIndex;
                        move(x, y, z);
                        curseur.setPosition(kinectPosX, kinectPosY);
                        if (illuminateIndex != tempIndex) {
                            help.illuminateOptions();
                            state = States.FREE;
                            goToFree();
                        }
                        break;
                    case SUN_SELECTED:
                        double myX = kinectPosX;
                        double myY = kinectPosY;
                        move(x, y, z);
                        moveSunShader(kinectPosX - myX, kinectPosY - myY);
                        break;
                    case MENU:
                        move(x, y, z);
                        break;
                    case CHANGE_SIZE:
                        move(x, y, z);
                        changeSize(z);
                        break;
                    case CHANGE_OPACITY:
                        move(x, y, z);
                        changeOpacity(z);
                        break;
                    case ON_CREATOR:
                        move(x, y, z);
                        if (illuminateIndex != -1) {
                            state = States.FREE;
                            goToFree();
                        }
                        break;
                    case ON_DESTRUCTOR:
                        move(x, y, z);
                        if (illuminateIndex != -2) {
                            state = States.FREE;
                            goToFree();
                        }
                        break;

                }
            }
        });
    }

    public void pushHand() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                switch (state) {
                    case SUPER_FREE:
                        break;
                    case FREE:
                        break;
                    case ON_SUN:
                        break;
                    case SUN_SELECTED:
                        break;
                    case MENU:
                        break;
                    case CHANGE_SIZE:
                        break;
                    case CHANGE_OPACITY:
                        break;
                    case ON_CREATOR:
                        break;
                    case ON_DESTRUCTOR:
                        break;

                }
            }
        });
    }

    public void userDetected() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                switch (state) {
                    case SUPER_FREE:
                        state = States.FREE;
                        startUser();
                        break;
                    case FREE:
                        break;
                    case ON_SUN:
                        break;
                    case SUN_SELECTED:
                        break;
                    case MENU:
                        break;
                    case CHANGE_SIZE:
                        break;
                    case CHANGE_OPACITY:
                        break;
                    case ON_CREATOR:
                        break;
                    case ON_DESTRUCTOR:
                        break;

                }
            }
        });
    }

    public void userLost() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                switch (state) {
                    case SUPER_FREE:
                        break;
                    case FREE:
                        state = States.SUPER_FREE;
                        stopUser();
                        break;
                    case ON_SUN:
                        state = States.SUPER_FREE;
                        stopUser();
                        break;
                    case SUN_SELECTED:
                        state = States.SUPER_FREE;
                        stopUser();
                        break;
                    case MENU:
                        state = States.SUPER_FREE;
                        stopUser();
                        break;
                    case CHANGE_SIZE:
                        state = States.SUPER_FREE;
                        stopUser();
                        break;
                    case CHANGE_OPACITY:
                        state = States.SUPER_FREE;
                        stopUser();
                        break;
                    case ON_CREATOR:
                        state = States.SUPER_FREE;
                        stopUser();
                        break;
                    case ON_DESTRUCTOR:
                        state = States.SUPER_FREE;
                        stopUser();
                        break;

                }
            }
        });
    }

    public void fingerAngle(final double angle) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                switch (state) {
                    case SUPER_FREE:
                        break;
                    case FREE:
                        break;
                    case ON_SUN:
                        break;
                    case SUN_SELECTED:
                        state = States.MENU;
                        openMenu(angle);
                        break;
                    case MENU:
                        changeIndicator(angle);
                        break;
                    case CHANGE_SIZE:
                        break;
                    case CHANGE_OPACITY:
                        break;
                    case ON_CREATOR:
                        break;
                    case ON_DESTRUCTOR:
                        break;

                }
            }
        });
    }

    /////////////////////////////////////////////////////////
    /*
     * public void handClosed() {
     *
     * Platform.runLater(new Runnable() {
     *
     * @Override public void run() { switch (state) { case SUPER_FREE:
     *
     * break; case CHANGE_OPACITY:
     *
     * //interdit break; case CHANGE_SIZE: // interdit break;
     *
     * case FREE: switch (handState) { case CLOSE: break; case FINGER: break;
     * case OPEN: break; }
     *
     * help.illuminateOptions();
     *
     * if (illuminateIndex >= 0 && circleObjectArray.size() > 0) {
     *
     * circleObjectArray.get(illuminateIndex).select(); basket.makeItEmpty();
     * state = States.SUN_SELECTED; help.illuminateOptions(Help.Etats.HAND_OPEN,
     * Help.Etats.FINGER);
     *
     * } else if (illuminateIndex == -1) { circleObjectArray.add(new
     * CircleObject(kinectPosX, kinectPosY, cercles, param)); //attention au
     * changment de coordonnée
     *
     * illuminateIndex = circleObjectArray.size() - 1;
     * circleObjectArray.get(illuminateIndex).toIlluminate();
     * circleObjectArray.get(illuminateIndex).select(); basket.sunCaught();
     * state = States.SUN_SELECTED; help.illuminateOptions(Help.Etats.HAND_OPEN,
     * Help.Etats.FINGER);
     *
     * }
     * break;
     *
     * case SUN_SELECTED: // impossible
     *
     * break; case MENU: help.illuminateOptions(Help.Etats.FINGER,
     * Help.Etats.HAND_OPEN); switch (tmenu.selected()) {
     *
     * case OPACITY: distanceZkinect = kinectPosZ; limitBack = kinectPosZ +
     * segmentSizeOpacity / 2; limitFront = kinectPosZ - segmentSizeOpacity / 2;
     * circleObjectArray.get(illuminateIndex).displayPipeOpacity(true); state =
     * States.CHANGE_OPACITY; break; case SIZE: distance2handsKinect =
     * kinectPosZ; //valeur magique paramétrable limitLeft = kinectPosZ -
     * segmentSizeResize / 2; limitRight = kinectPosZ + segmentSizeResize / 2;
     * circleObjectArray.get(illuminateIndex).displayPipeSize(true); state =
     * States.CHANGE_SIZE; break; case CANCEL: state = States.SUN_SELECTED;
     * break; } menu.getChildren().removeAll(menu.getChildren()); break; } } });
     * }
     *
     * public void handOpened() { curseur.changeToHandOpen();
     * Platform.runLater(new Runnable() {
     *
     * @Override public void run() { switch (state) { case SUPER_FREE:
     *
     * break; case CHANGE_OPACITY: state = States.FREE;
     * help.illuminateOptions(Help.Etats.HAND_CLOSE);
     * circleObjectArray.get(illuminateIndex).unSelect();
     * circleObjectArray.get(illuminateIndex).displayPipeSize(false);
     * circleObjectArray.get(illuminateIndex).displayPipeOpacity(false); break;
     * case CHANGE_SIZE: help.illuminateOptions(Help.Etats.HAND_CLOSE); state =
     * States.FREE; circleObjectArray.get(illuminateIndex).unSelect();
     * circleObjectArray.get(illuminateIndex).displayPipeSize(false);
     * circleObjectArray.get(illuminateIndex).displayPipeOpacity(false); break;
     *
     * case FREE: // Interdit break;
     *
     * case SUN_SELECTED: circleObjectArray.get(illuminateIndex).unSelect();
     *
     * if (basket.proximity(circleObjectArray.get(illuminateIndex).getX(),
     * circleObjectArray.get(illuminateIndex).getY())) { basket.sunDroped();
     * cercles.getChildren().remove(illuminateIndex);
     * circleObjectArray.remove(illuminateIndex); illuminateIndex = -2;
     * help.illuminateOptions(Help.Etats.HAND_CLOSE);
     *
     * } else { help.illuminateOptions(Help.Etats.HAND_CLOSE);
     *
     * basket.makeItFull(); } state = States.FREE; break; case MENU:
     * help.illuminateOptions(Help.Etats.HAND_CLOSE); state = States.FREE;
     * circleObjectArray.get(illuminateIndex).unSelect();
     * menu.getChildren().removeAll(menu.getChildren()); break;
     *
     * }
     * }
     * });
     *
     *
     * }
     *
     * public void handMove(final double x, final double y, final double z) {
     * //System.out.println(etat); Platform.runLater(new Runnable() {
     *
     * @Override public void run() {
     *
     * switch (state) { case SUPER_FREE:
     *
     * break; case CHANGE_OPACITY: kinectPosX = x; kinectPosY = y; kinectPosZ =
     * z; //if (testGuardOpacity()) { // etat = Etats.SUN_SELECTED; //}
     * changeOpacity(z); //interdit break; case CHANGE_SIZE: kinectPosX = x;
     * kinectPosY = y; kinectPosZ = z; changeSize(z); break;
     *
     * case FREE: kinectPosX = x; kinectPosY = y; kinectPosZ = z; majFeedback(x,
     * y); //attention au passage des coordonnées 640*480
     *
     * break;
     *
     * case SUN_SELECTED: if
     * (basket.proximity(circleObjectArray.get(illuminateIndex).getX(),
     * circleObjectArray.get(illuminateIndex).getY())) { basket.handIn();
     *
     * } else { basket.handOut();
     *
     * }
     * //translateSunShader((x) * windowSizeX / kinectWindowSizeX, (y) *
     * windowSizeY / kinectWindowSizeY); // attention dimension
     * translateSunShader(x - kinectPosX, y - kinectPosY); kinectPosX = x;
     * kinectPosY = y; kinectPosZ = z; kinectPosXOpacity = x; kinectPosXResize =
     * x; kinectPosYOpacity = y; kinectPosYResize = y; break; case MENU:
     * kinectPosX = x; kinectPosY = y; kinectPosZ = z;
     *
     * break; } curseur.setPosition(kinectPosX, kinectPosY); } });
     *
     * }
     *
     * public void twoHandsdistance(final double distance) {
     * Platform.runLater(new Runnable() {
     *
     * @Override public void run() { switch (state) { case SUPER_FREE:
     *
     * break; case CHANGE_OPACITY: // Interdit break; case CHANGE_SIZE:
     * changeSize(distance); break;
     *
     * case FREE: // Interdit break;
     *
     * case SUN_SELECTED:
     *
     * distance2handsKinect = distance; //valeur magique paramétrable limitLeft
     * = distance - segmentSizeResize / 2; limitRight = distance +
     * segmentSizeResize / 2; state = States.CHANGE_SIZE; // Go etat CHANGE_SIZE
     * break; } } }); }
     *
     * public void pushHand() { Platform.runLater(new Runnable() {
     *
     * @Override public void run() { switch (state) { case SUPER_FREE: state =
     * States.FREE; basket.show();
     *
     * break; case CHANGE_OPACITY:
     *
     *
     * break; case CHANGE_SIZE: // Interdit break;
     *
     * case FREE: state = States.SUPER_FREE; basket.hide(); if (illuminateIndex
     * >= 0) { circleObjectArray.get(illuminateIndex).toNormal(); }
     *
     * illuminateIndex = -2; break;
     *
     * case SUN_SELECTED:
     *
     *
     * break; } } }); }
     *
     * public void userDetection(final boolean b) {
     *
     *
     * Platform.runLater(new Runnable() {
     *
     * @Override public void run() { help.handWaveSetVisible(!b); if (b) {
     * manConnectionTool.connected(); help.handWaveSetVisible(false);
     * majFeedback(kinectPosX, kinectPosY); state = States.FREE;
     * curseur.changeToHandOpen();
     *
     * } else { manConnectionTool.disconnected(); help.handWaveSetVisible(true);
     * help.illuminateOptions(); curseur.setVisible(false);
     * menu.getChildren().removeAll(menu.getChildren()); if (illuminateIndex >=
     * 0) { circleObjectArray.get(illuminateIndex).toNormal(); } illuminateIndex
     * = -2; state = States.SUPER_FREE; } switch (state) {
     *
     * case SUPER_FREE:
     *
     * break; case CHANGE_OPACITY:
     *
     *
     * break; case CHANGE_SIZE: // Interdit break;
     *
     * case FREE:
     *
     * break;
     *
     * case SUN_SELECTED:
     *
     *
     * break; case MENU: break;
     *
     * }
     * }
     * }); }
     *
     * void kinectconnection(final boolean b) { Platform.runLater(new Runnable()
     * {
     *
     * @Override public void run() { help.handWaveSetVisible(b); if (b) {
     * connectionTool.connected(); } else { connectionTool.disconnected(); } }
     * }); }
     *
     * public void fingerAngle(final double d) {
     *
     * curseur.changeToFingerOn();
     *
     * Platform.runLater(new Runnable() {
     *
     * @Override public void run() {
     *
     * switch (state) { case SUPER_FREE:
     *
     *
     * break; case CHANGE_OPACITY:
     *
     * tmenu = new TiltMenu(d + Math.PI / 4, Math.PI / 2, 200);
     *
     * tmenu.addItem(TiltMenu.Type.OPACITY, TiltMenu.Type.CANCEL,
     * TiltMenu.Type.SIZE); tmenu.setIndicator(d);
     * tmenu.setPosition(cercles.getChildren().get(illuminateIndex).getLayoutX(),
     * cercles.getChildren().get(illuminateIndex).getLayoutY());
     * tmenu.setVisible(true);
     * circleObjectArray.get(illuminateIndex).displayPipeSize(false);
     * circleObjectArray.get(illuminateIndex).displayPipeOpacity(false); state =
     * States.MENU; menu.getChildren().add(tmenu.getMenu()); break; case
     * CHANGE_SIZE: tmenu = new TiltMenu(d + Math.PI / 4, Math.PI / 2, 200);
     *
     * tmenu.addItem(TiltMenu.Type.OPACITY, TiltMenu.Type.CANCEL,
     * TiltMenu.Type.SIZE); tmenu.setIndicator(d);
     * tmenu.setPosition(cercles.getChildren().get(illuminateIndex).getLayoutX(),
     * cercles.getChildren().get(illuminateIndex).getLayoutY());
     * tmenu.setVisible(true);
     * circleObjectArray.get(illuminateIndex).displayPipeSize(false);
     * circleObjectArray.get(illuminateIndex).displayPipeOpacity(false); state =
     * States.MENU; menu.getChildren().add(tmenu.getMenu()); break;
     *
     * case FREE:
     *
     * break;
     *
     * case SUN_SELECTED: tmenu = new TiltMenu(d + Math.PI / 4, Math.PI / 2,
     * 200);
     *
     * tmenu.addItem(TiltMenu.Type.OPACITY, TiltMenu.Type.CANCEL,
     * TiltMenu.Type.SIZE); tmenu.setIndicator(d);
     * tmenu.setPosition(cercles.getChildren().get(illuminateIndex).getLayoutX(),
     * cercles.getChildren().get(illuminateIndex).getLayoutY());
     * tmenu.setVisible(true); state = States.MENU;
     * menu.getChildren().add(tmenu.getMenu());
     * help.illuminateOptions(Help.Etats.HAND_CLOSE, Help.Etats.HAND_OPEN);
     *
     *
     * break; case MENU: tmenu.setIndicator(d); break;
     *
     * }
     *
     *
     * }
     * });
     *
     * }
     */
    private void stopUser() {
        manConnectionTool.disconnected();
        help.handWaveSetVisible(true);
        help.illuminateOptions();
        curseur.setVisible(false);
    }

    private void startUser() {
        manConnectionTool.connected();
        help.handWaveSetVisible(false);
        curseur.setVisible(true);
    }

    private void goToFree() {
        for (CircleObject c : circleObjectArray) {
            c.toNormal();
        }
        creator.disappears();
        destructor.disappears();
    }

    private void handToOpen() {
        handState = HandState.OPEN;
        curseur.changeToHandOpen();
    }

    private void handToClose() {
        handState = HandState.CLOSE;
        curseur.changeToHandClose();
    }

    private void handToFinger() {
        handState = HandState.FINGER;
        curseur.changeToFingerOn();
    }

    // TODO faire les méthodes de tranistion
    private void onSunShader() {
        circleObjectArray.get(illuminateIndex).toIlluminate();
        switch (handState) {
            case CLOSE:
                help.illuminateOptions();
                break;
            case OPEN:
                help.illuminateOptions(Help.Etats.HAND_CLOSE);
                break;
            case FINGER:
                help.illuminateOptions();
                break;
        }
    }

    private void selectSunShader() {
        circleObjectArray.get(illuminateIndex).select();
    }

    private void createAndSelectSunShader() {
        circleObjectArray.add(new CircleObject(kinectPosX, kinectPosY, cercles, param));
        //attention au changment de coordonnée

        illuminateIndex = circleObjectArray.size() - 1;
        circleObjectArray.get(illuminateIndex).toIlluminate();
        circleObjectArray.get(illuminateIndex).select();
        creator.sunCaught();
        help.illuminateOptions(Help.Etats.HAND_OPEN, Help.Etats.FINGER);
    }
    // quand on a la main ouverte -1

    private void onCreator() {
        switch (handState) {
            case CLOSE:
                help.illuminateOptions();
                break;
            case OPEN:
                help.illuminateOptions(Help.Etats.HAND_CLOSE);
                break;
            case FINGER:
                help.illuminateOptions();
                break;
        }
        creator.appears();
    }
    // quand on est proche du destructeur -2

    private void onDestructor() {
        destructor.appears();
    }

    private void moveSunShader(double dx, double dy) {
        translateSunShader(dx, dy - kinectPosY);
    }

    private void initSize() {
        distance2handsKinect = kinectPosZ;
        limitLeft = kinectPosZ - segmentSizeResize / 2;
        limitRight = kinectPosZ + segmentSizeResize / 2;
        circleObjectArray.get(illuminateIndex).displayPipeSize(true);
    }

    private void changeSize(double z) {
        if (z < limitLeft) {
            circleObjectArray.get(illuminateIndex).changeSize(z - limitLeft);
            limitLeft = z;

            distance2handsKinect = limitLeft + segmentSizeResize / 2;
            limitRight = limitLeft + segmentSizeResize;

        } else if (z > limitRight) {
            circleObjectArray.get(illuminateIndex).changeSize(z - limitRight);
            limitRight = z;
            distance2handsKinect = limitRight - segmentSizeResize / 2;
            limitLeft = limitRight - segmentSizeResize;

        }
    }

    private void initOpacity() {
        distanceZkinect = kinectPosZ;
        limitBack = kinectPosZ + segmentSizeOpacity / 2;
        limitFront = kinectPosZ - segmentSizeOpacity / 2;
        circleObjectArray.get(illuminateIndex).displayPipeOpacity(true);
    }

    private void changeOpacity(double z) {
        if (z > limitBack) {
            circleObjectArray.get(illuminateIndex).changeOpacity(z - limitBack);
            limitBack = z;
            distanceZkinect += limitBack - segmentSizeOpacity / 2;
            limitFront = limitBack - segmentSizeOpacity;


        } else if (z < limitFront) {
            circleObjectArray.get(illuminateIndex).changeOpacity(z - limitFront);
            limitFront = z;
            distanceZkinect += limitFront + segmentSizeOpacity / 2;
            limitBack = limitFront + segmentSizeOpacity;

        }
    }

    private void changeIndicator(double angle) {
        tmenu.setIndicator(angle);
    }

    private void openMenu(double d) {
        tmenu = new TiltMenu(d + Math.PI / 4, Math.PI / 2, 200);
        tmenu.addItem(TiltMenu.Type.OPACITY, TiltMenu.Type.CANCEL, TiltMenu.Type.SIZE);
        tmenu.setIndicator(d);
        tmenu.setPosition(cercles.getChildren().get(illuminateIndex).getLayoutX(), cercles.getChildren().get(illuminateIndex).getLayoutY());
        tmenu.setVisible(true);
        menu.getChildren().add(tmenu.getMenu());
    }

    private void closeMenu() {
        tmenu.setVisible(false);
        menu.getChildren().removeAll(menu.getChildren());
    }

    private void move(double x, double y, double z) {
        kinectPosX = x;
        kinectPosY = y;
        kinectPosZ = z;
        majFeedback(x, y);
    }

    private void translateSunShader(double x, double y) {
        circleObjectArray.get(illuminateIndex).translatePostion(multTranslation * x, multTranslation * y);
    }

    private void majFeedback(double x, double y) {
        illuminateIndex = getNearest(x, y);
    }

    private int getNearest(double x, double y) {
        double tempDistance = 5000;
        double tempCompare;
        int tempIndex = -3;
        if (circleObjectArray.size() > 0) {
            for (int i = 0; i < circleObjectArray.size(); i++) {

                tempCompare = circleObjectArray.get(i).proximity(x, y);
                if (tempCompare < tempDistance && tempCompare < 100) {
                    tempDistance = tempCompare;
                    tempIndex = i;
                }
            }
        } else if (destructor.proximity(x, y)) {
            tempIndex = -2;
        } else if (creator.proximity(x, y)) {
            tempIndex = -1;
        }
        return tempIndex;
    }

    private boolean testGuardOpacity() {
        // teste si la main est en dehors de la sphère de centre (kinectPosXOpacity,kinectPosYOpacity) et de rayon r = opacityGuard
        return (Math.sqrt((kinectPosX - kinectPosXOpacity) * (kinectPosX - kinectPosXOpacity) + (kinectPosY - kinectPosYOpacity) * (kinectPosY - kinectPosYOpacity)) > opacityGuard);
    }

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


                kinectServer.send("KINECT_POSITION X=" + (int) (me.getX() * kinectWindowSizeWidth / windowSizeWidth) + " Y=" + (int) (me.getY() * kinectWindowSizeHeight / windowSizeHeight) + " Z=0");
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

                kinectServer.send("KINECT_POSITION X=" + (int) (me.getX() * kinectWindowSizeWidth / windowSizeWidth) + " Y=" + (int) (me.getY() * kinectWindowSizeHeight / windowSizeHeight) + " Z=0");
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