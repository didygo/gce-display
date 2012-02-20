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
public class Controller extends Application implements Control  {
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
    //panier à sunshader
    CircleBasket basket;
    private boolean isOnBasket = false;
    // panier qui détruit le sunshader`
    private Destructor destructor;
    private boolean isOnDestructor = false;
    private boolean isOnHelp = false;
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
    Timer timer;

    public Controller(DashBoard d, boolean fullScreen,ParamManager param) {
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

    
    public enum States {

        SUPER_FREE, FREE, ON_SUN, SUN_SELECTED, CHANGE_SIZE, CHANGE_OPACITY, MENU, ON_CREATOR, ON_DESTRUCTOR, ON_HELP
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
        
        // le timer utilisé par le prototype 1
        timer = new Timer(param.timerDuration, new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                tickTimer();
            }
        });
        
        /// 2) Initialisation du bus de communication inter logiciel ///
        this.adresseBus = "127.255.255.255:2010";
        this.kinectServer = new KinectServer(this, adresseBus, windowSizeWidth, windowSizeHeight, param);
        //////////////////////////////////////////////////////////////
        gestionEvenementsSouris(scene);
        /// 3) Initialisation des interactions pour prototype I //
        this.state = States.SUPER_FREE;
        this.basket = new CircleBasket(root, windowSizeWidth, windowSizeHeight, param);

        this.circleObjectArray = new ArrayList();
        this.cercles = new Group();
        this.menu = new Group();
        this.root.getChildren().addAll(cercles, menu);
        ////////////////////////////////////////////////////////

        this.connectionTool = new ConnectionTool(root, windowSizeWidth - 50, windowSizeHeight - 50, kinectServer);
        this.manConnectionTool = new ManConnectionTool(root, windowSizeWidth - 150, windowSizeHeight - 50, kinectServer);

        destructor = new Destructor(windowSizeWidth, windowSizeHeight, param);
        destructor.setVisible(false);
        this.root.getChildren().add(destructor.getDestructor());

        help = new Help(windowSizeWidth, windowSizeHeight);
        help.addImg("Images/help/doigtGris.png", Help.Etats.FINGER);
        help.addImg("Images/help/mainFermeeGris.png", Help.Etats.HAND_CLOSE);
        help.addImg("Images/help/mainOuverteGris.png", Help.Etats.HAND_OPEN);

        root.getChildren().addAll(help.getHelp());
        
        this.curseur = new Curseur(root, param);
        this.curseur.setVisible(false);
        this.handState = HandState.OPEN;
        
        //primaryStage.setFullScreen(true);

        root.getChildren().add(new Config().getConfig());
        
        help.allVisible(true);

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

    
    
    /* ------------------------- State Machine Proto 1 ---------------------*/

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

    public void tickTimer() {
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
                        destroy();
                        break;
                    case ON_HELP:
                        //help.helpVisible(!help.isVisible());
                        if (help.isIn()) help.helpOut();
                        else help.helpIn();
                        timer.stop();
                        curseur.stopTimer();
                        majFeedback(kinectPosX, kinectPosY);
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
                        closeMenu();
                        break;
                    case CHANGE_SIZE:
                        state = States.ON_SUN;
                        circleObjectArray.get(illuminateIndex).displayPipeSize(false);
                        onSunShader();
                        handToOpen();
                        help.illuminateOptions(Help.Etats.HAND_CLOSE);
                        break;
                    case CHANGE_OPACITY:
                        state = States.ON_SUN;
                        circleObjectArray.get(illuminateIndex).displayPipeOpacity(false);
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
                        destructor.setVisible(true);
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
                                destructor.setVisible(true);
                                break;
                        }
                        closeMenu();
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
                //System.out.println(state);
                switch (state) {
                    case SUPER_FREE:
                        break;
                    case FREE:
                        move(x, y, z);
                        curseur.setPosition(kinectPosX, kinectPosY, kinectPosZ);
                        if (illuminateIndex >= 0) {
                            state = States.ON_SUN;
                            onSunShader();
                        } else if (isOnBasket) {
                            state = States.ON_CREATOR;
                            onCreator();
                        } else if (isOnHelp) {
                            state = States.ON_HELP;
                            onHelp();
                        }
                        break;
                    case ON_SUN:
                        int tempIndex = illuminateIndex;
                        move(x, y, z);
                        curseur.setPosition(kinectPosX, kinectPosY, kinectPosZ);
                        if (illuminateIndex != tempIndex) {
                            help.illuminateOptions();
                            state = States.FREE;
                            goToFree();
                        }
                        break;
                    case SUN_SELECTED:
                        moveSunShader(x, y, z);
                        curseur.setPosition(kinectPosX, kinectPosY, kinectPosZ);
                        if (isOnDestructor) {
                            state = States.ON_DESTRUCTOR;
                            onDestructor();
                        }
                        break;
                    case MENU:
                        //move(x, y, z);
                        curseur.changeColorDepth(z);
                        break;
                    case CHANGE_SIZE:
                        //move(x, y, z);
                        curseur.changeColorDepth(z);
                        changeSize(z);
                        break;
                    case CHANGE_OPACITY:
                        //move(x, y, z);
                        curseur.changeColorDepth(z);
                        changeOpacity(z);
                        break;
                    case ON_CREATOR:
                        move(x, y, z);
                        curseur.setPosition(kinectPosX, kinectPosY, kinectPosZ);
                        if (!isOnBasket) {
                            state = States.FREE;
                            goToFree();
                            basket.handOut();
                            help.illuminateOptions();
                        }
                        break;
                    case ON_DESTRUCTOR:
                        if (illuminateIndex >= 0) {
                            moveSunShader(x, y, z);
                        } else {
                            move(x, y, z);
                        }
                        curseur.setPosition(kinectPosX, kinectPosY, kinectPosZ);
                        if (!isOnDestructor) {
                            timer.stop();
                            curseur.stopTimer();
                            if (illuminateIndex == -3) {
                                destructor.setVisible(false);
                                state = States.FREE;
                                goToFree();
                            } else {
                                state = States.SUN_SELECTED;
                            }
                        }
                        break;
                    case ON_HELP:
                        if (illuminateIndex >= 0) {
                            moveSunShader(x, y, z);
                        } else {
                            move(x, y, z);
                        }
                        curseur.setPosition(kinectPosX, kinectPosY, kinectPosZ);
                        if (!isOnHelp) {
                            timer.stop();
                            curseur.stopTimer();
                            if (illuminateIndex == -3) {
                                state = States.FREE;
                                goToFree();
                            } else {
                                state = States.ON_SUN;
                                onSunShader();
                            }
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
                        goToFree();
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
                        handToFinger();
                        break;
                    case ON_SUN:
                        handToFinger();
                        help.illuminateOptions(Help.Etats.HAND_CLOSE);
                        break;
                    case SUN_SELECTED:
                        state = States.MENU;
                        openMenu(angle);
                        handToFinger();
                        help.illuminateOptions(Help.Etats.HAND_CLOSE, Help.Etats.HAND_OPEN);
                        break;
                    case MENU:
                        changeIndicator(angle);
                        break;
                    case CHANGE_SIZE:
                        state = States.MENU;
                        circleObjectArray.get(illuminateIndex).displayPipeSize(false);
                        openMenu(angle);
                        handToFinger();
                        break;
                    case CHANGE_OPACITY:
                        state = States.MENU;
                        circleObjectArray.get(illuminateIndex).displayPipeOpacity(false);
                        openMenu(angle);
                        handToFinger();
                        break;
                    case ON_CREATOR:
                        break;
                    case ON_DESTRUCTOR:
                        break;

                }
            }
        });
    }
    
    
   

    private void stopUser() {
        for (CircleObject c : circleObjectArray) {
            c.unSelect();
            c.displayPipeOpacity(false);
            c.displayPipeSize(false);
            if (tmenu != null) {
                tmenu.setVisible(false);
                menu.getChildren().removeAll(menu.getChildren());
            }
            c.toNormal();
        }
        manConnectionTool.disconnected();
        help.handWaveSetVisible(true);
        help.helpVisible(false);
        help.illuminateOptions();
        curseur.setVisible(false);
        basket.hide();
    }

    private void startUser() {
        manConnectionTool.connected();
        help.handWaveSetVisible(false);
        help.helpVisible(true);
        curseur.setVisible(true);
        curseur.changeToHandOpen();
        basket.show();
    }

    private void goToFree() {
       // helpTool.setVisible(true);
        for (CircleObject c : circleObjectArray) {
            c.unSelect();
            c.toNormal();
        }
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
        destructor.setVisible(false);
        basket.makeItFull();
        circleObjectArray.get(illuminateIndex).unSelect();
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
        basket.makeItEmpty();
    }

    private void createAndSelectSunShader() {
        circleObjectArray.add(new CircleObject(kinectPosX, kinectPosY, cercles, param));
        //attention au changment de coordonnée

        illuminateIndex = circleObjectArray.size() - 1;
        circleObjectArray.get(illuminateIndex).toIlluminate();
        circleObjectArray.get(illuminateIndex).select();
        basket.sunCaught();
        basket.handOut();
        destructor.setVisible(true);

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
        basket.handIn();
    }
    // quand on est proche du destructeur -2

    private void destroy() {
        circleObjectArray.get(illuminateIndex).toNormal();
        cercles.getChildren().remove(illuminateIndex);
        circleObjectArray.remove(illuminateIndex);
        destructor.addSun();
        timer.stop();
        curseur.stopTimer();
        majFeedback(kinectPosX, kinectPosY);
    }

    private void onDestructor() {
        timer.start();
        curseur.startTimer();
    }

    private void onHelp() {
        timer.start();
        curseur.startTimer();
    }

    private void moveSunShader(double x, double y, double z) {
        double myX = x * 1;
        double myY = y * 1;
        translateSunShader(myX - kinectPosX, myY - kinectPosY);
        kinectPosX = myX;
        kinectPosY = myY;
        kinectPosZ = z;
        majFeedback(x, y);
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
        }
        isOnBasket = basket.proximity(x, y);
        isOnDestructor = destructor.proximity(x, y);
        isOnHelp = help.proximity(x, y);
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
