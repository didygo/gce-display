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
public class Controller1 extends Application {
    // Variables du système d'affichage

    Stage stage;
    Group root, cercles, menu;
    ArrayList<CircleObject> circleObjectArray;
    private double kinectPosX = 0;
    private double kinectPosY = 0;
    private double kinectPosZ = 0;
    private double windowSizeY, windowSizeX, kinectWindowSizeX, kinectWindowSizeY;
    private String adresseBus;
    private KinectServer1 kinectServer;
    ConnectionTool connectionTool;
    ManConnectionTool manConnectionTool;
    CircleBasket basket;
    int illuminateIndex = -2;
    // -1 pour le panier , -2 pour rien , 0..infini pour les sunshader
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
    //varaibles de test à enlever
    double distSize = 100;
    double distOpacity = 100;
    //variablr pour demultiplier le deplacement
    double multTranslation = 1;
    //les TiltMenu
    TiltMenu tmenu;
    //le curseur
    Curseur curseur;
    //le menu d'aide
    Help help;

    public enum Etats {

        FREE, SUN_SELECTED, CHANGE_SIZE, CHANGE_OPACITY, SUPER_FREE, MENU
    }
    private Etats etat;

    //////////////////////////////////////
    private void init(Stage primaryStage) {

        /// init des variables
        distance2handsKinect = 100;
        limitLeft = 90;
        limitRight = 110;
        kinectPosXResize = 0;
        kinectPosYResize = 0;

        distanceZkinect = 100;
        limitBack = 110;
        limitFront = 90;
        opacityGuard = 30;
        kinectPosXOpacity = 0;
        kinectPosYOpacity = 0;

        /// 1) Initialisation de la scène graphique//
        windowSizeX = 1440;
        windowSizeY = 900;
        kinectWindowSizeX = 640;
        kinectWindowSizeY = 480;
        root = new Group();
        Scene scene = new Scene(root, windowSizeX, windowSizeY);
        ImageView background = new ImageView(new Image("Images/fonds/ciel3.jpg"));
        //primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        root.getChildren().add(background);
        ////////////////////////////////////////
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent arg0) {
                kinectServer.send("IHM_EVENT=END_CONNECTION");
                System.exit(0);
            }
        });




        /// 2) Initialisation du bus de communication inter logiciel ///
        adresseBus = "192.168.1.255:2010";//"10.3.8.255:2010";
        kinectServer = new KinectServer1(this, adresseBus, windowSizeX, windowSizeY);
        //////////////////////////////////////////////////////////////
        gestionEvenementsSouris(scene);
        /// 3) Initialisation des interactions pour prototype I //
        etat = Etats.FREE;
        basket = new CircleBasket(root, windowSizeX + 40, windowSizeY);

        circleObjectArray = new ArrayList();
        cercles = new Group();
        menu = new Group();
        root.getChildren().addAll(cercles, menu);
        ////////////////////////////////////////////////////////

        connectionTool = new ConnectionTool(root, windowSizeX - 50, windowSizeY - 50, kinectServer);
        manConnectionTool = new ManConnectionTool(root, windowSizeX - 150, windowSizeY - 50, kinectServer);


        curseur = new Curseur(root);
        curseur.changeToLibre();



        ImageView v = new ImageView(new Image("Images/curseurs/"));
        v.setX(200);
        v.setY(200);
        


        help = new Help(windowSizeX,windowSizeY);
        help.addImg("Images/help/doigtGris.png", Help.Etats.FINGER);
        help.addImg("Images/help/mainFermeeGris.png", Help.Etats.HAND_CLOSE);
        help.addImg("Images/help/mainOuverteGris.png", Help.Etats.HAND_OPEN);
        root.getChildren().addAll(v,help.getHelp());
        
        //primaryStage.setFullScreen(true);
        
        
        

    }

    public void handSelect() {
        curseur.changeToHandClose();
        Platform.runLater(new Runnable() {
            
            @Override
            public void run() {
                switch (etat) {
                    case SUPER_FREE:

                        break;
                    case CHANGE_OPACITY:
                        //interdit
                        break;
                    case CHANGE_SIZE:
                        // interdit
                        break;

                    case FREE:
                        help.illuminateOptions();

                        if (illuminateIndex >= 0 && circleObjectArray.size() > 0) {

                            circleObjectArray.get(illuminateIndex).select();
                            basket.makeItEmpty();
                            etat = Etats.SUN_SELECTED;
                            help.illuminateOptions(Help.Etats.HAND_OPEN, Help.Etats.FINGER);

                        } else if (illuminateIndex == -1) {
                            circleObjectArray.add(new CircleObject(kinectPosX, kinectPosY, cercles));
                            //attention au changment de coordonnée

                            illuminateIndex = circleObjectArray.size() - 1;
                            circleObjectArray.get(illuminateIndex).toIlluminate();
                            circleObjectArray.get(illuminateIndex).select();
                            basket.sunCaught();
                            etat = Etats.SUN_SELECTED;
                            help.illuminateOptions(Help.Etats.HAND_OPEN, Help.Etats.FINGER);

                        }
                        break;

                    case SUN_SELECTED:
                        // impossible

                        break;
                    case MENU:
                        help.illuminateOptions(Help.Etats.FINGER, Help.Etats.HAND_OPEN);
                        switch (tmenu.selected()) {
                            case OPACITY:
                                distanceZkinect = kinectPosZ;
                                limitBack = kinectPosZ + segmentSizeOpacity / 2;
                                limitFront = kinectPosZ - segmentSizeOpacity / 2;
                                etat = Etats.CHANGE_OPACITY;
                                break;
                            case SIZE:
                                distance2handsKinect = kinectPosZ;
                                //valeur magique paramétrable
                                limitLeft = kinectPosZ - segmentSizeResize / 2;
                                limitRight = kinectPosZ + segmentSizeResize / 2;

                                etat = Etats.CHANGE_SIZE;
                                break;
                            case CANCEL:
                                etat = Etats.SUN_SELECTED;
                                break;
                        }
                        menu.getChildren().removeAll(menu.getChildren());
                        break;
                }
            }
        });
    }

    public void handUnSelect() {
        curseur.changeToHandOpen();
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                switch (etat) {
                    case SUPER_FREE:

                        break;
                    case CHANGE_OPACITY:
                        etat = Etats.FREE;
                        help.illuminateOptions(Help.Etats.HAND_CLOSE);
                        circleObjectArray.get(illuminateIndex).unSelect();
                        break;
                    case CHANGE_SIZE:
                        help.illuminateOptions(Help.Etats.HAND_CLOSE);
                        etat = Etats.FREE;
                        circleObjectArray.get(illuminateIndex).unSelect();
                        break;

                    case FREE:
                        // Interdit
                        break;

                    case SUN_SELECTED:
                        circleObjectArray.get(illuminateIndex).unSelect();

                        if (basket.proximity(circleObjectArray.get(illuminateIndex).getX(), circleObjectArray.get(illuminateIndex).getY())) {
                            basket.sunDroped();
                            cercles.getChildren().remove(illuminateIndex);
                            circleObjectArray.remove(illuminateIndex);
                            illuminateIndex = -2;
                            help.illuminateOptions(Help.Etats.HAND_CLOSE);

                        } else {
                            help.illuminateOptions(Help.Etats.HAND_CLOSE);

                            basket.makeItFull();
                        }
                        etat = Etats.FREE;
                        break;
                    case MENU:
                        help.illuminateOptions(Help.Etats.HAND_CLOSE);
                        etat = Etats.FREE;
                        circleObjectArray.get(illuminateIndex).unSelect();
                        menu.getChildren().removeAll(menu.getChildren());
                        break;

                }
            }
        });


    }

    public void eventKinectMove(final double x, final double y, final double z) {
        System.out.println(etat);
        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                switch (etat) {
                    case SUPER_FREE:

                        break;
                    case CHANGE_OPACITY:
                        kinectPosX = x;
                        kinectPosY = y;
                        kinectPosZ = z;
                        //if (testGuardOpacity()) {
                        //  etat = Etats.SUN_SELECTED;
                        //}
                        majOpacity(z);
                        //interdit
                        break;
                    case CHANGE_SIZE:
                        kinectPosX = x;
                        kinectPosY = y;
                        kinectPosZ = z;
                        majSize(z);
                        break;

                    case FREE:
                        kinectPosX = x;
                        kinectPosY = y;
                        kinectPosZ = z;
                        majFeedback(x, y);
                        //attention au passage des coordonnées 640*480

                        break;

                    case SUN_SELECTED:
                        if (basket.proximity(circleObjectArray.get(illuminateIndex).getX(), circleObjectArray.get(illuminateIndex).getY())) {
                            basket.handIn();

                        } else {
                            basket.handOut();

                        }
                        //translateSunShader((x) * windowSizeX / kinectWindowSizeX, (y) * windowSizeY / kinectWindowSizeY);
                        // attention dimension
                        translateSunShader(x - kinectPosX, y - kinectPosY);
                        kinectPosX = x;
                        kinectPosY = y;
                        kinectPosZ = z;
                        kinectPosXOpacity = x;
                        kinectPosXResize = x;
                        kinectPosYOpacity = y;
                        kinectPosYResize = y;
                        break;
                    case MENU:
                        kinectPosX = x;
                        kinectPosY = y;
                        kinectPosZ = z;

                        break;
                }
            }
        });
        curseur.setPosition(kinectPosX, kinectPosY);
    }

    public void eventKinect2Hands(final double distance) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                switch (etat) {
                    case SUPER_FREE:

                        break;
                    case CHANGE_OPACITY:
                        // Interdit
                        break;
                    case CHANGE_SIZE:
                        majSize(distance);
                        break;

                    case FREE:
                        // Interdit
                        break;

                    case SUN_SELECTED:

                        distance2handsKinect = distance;
                        //valeur magique paramétrable
                        limitLeft = distance - segmentSizeResize / 2;
                        limitRight = distance + segmentSizeResize / 2;
                        etat = Etats.CHANGE_SIZE;
                        // Go etat CHANGE_SIZE
                        break;
                }
            }
        });
    }

    public void eventHandDepth(final double depth) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                switch (etat) {
                    case SUPER_FREE:

                        break;
                    case CHANGE_OPACITY:
                        majOpacity(depth);

                        break;
                    case CHANGE_SIZE:
                        // Interdit
                        break;

                    case FREE:
                        // Interdit
                        break;

                    case SUN_SELECTED:

                        distanceZkinect = depth;
                        limitBack = depth + segmentSizeOpacity / 2;
                        limitFront = depth - segmentSizeOpacity / 2;
                        etat = Etats.CHANGE_OPACITY;
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
                    case SUPER_FREE:
                        etat = Etats.FREE;
                        basket.show();

                        break;
                    case CHANGE_OPACITY:


                        break;
                    case CHANGE_SIZE:
                        // Interdit
                        break;

                    case FREE:
                        etat = Etats.SUPER_FREE;
                        basket.hide();
                        if (illuminateIndex >= 0) {
                            circleObjectArray.get(illuminateIndex).toNormal();
                        }

                        illuminateIndex = -2;
                        break;

                    case SUN_SELECTED:


                        break;
                }
            }
        });
    }

    public void userDetection(final boolean b) {


        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                if (b) {
                    majFeedback(kinectPosX, kinectPosY);
                    etat = Etats.FREE;
                    curseur.setVisible(true);
                } else {
                    help.illuminateOptions();
                    curseur.setVisible(false);
                    menu.getChildren().removeAll(menu.getChildren());
                    if (illuminateIndex >= 0) {
                        circleObjectArray.get(illuminateIndex).toNormal();
                    }
                    illuminateIndex = -2;
                    etat = Etats.SUPER_FREE;
                }
                switch (etat) {

                    case SUPER_FREE:

                        break;
                    case CHANGE_OPACITY:


                        break;
                    case CHANGE_SIZE:
                        // Interdit
                        break;

                    case FREE:

                        break;

                    case SUN_SELECTED:


                        break;
                    case MENU:
                        break;

                }
            }
        });
    }

    public void eventFingerAngle(final double d) {

        curseur.changeToFingerOn();

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                switch (etat) {
                    case SUPER_FREE:


                        break;
                    case CHANGE_OPACITY:

                        tmenu = new TiltMenu(d + Math.PI / 4, Math.PI / 2, 200);

                        tmenu.addItem(TiltMenu.Type.OPACITY, TiltMenu.Type.CANCEL, TiltMenu.Type.SIZE);
                        tmenu.setIndicator(d);
                        tmenu.setPosition(cercles.getChildren().get(illuminateIndex).getLayoutX(), cercles.getChildren().get(illuminateIndex).getLayoutY());
                        tmenu.setVisible(true);
                        etat = Etats.MENU;
                        menu.getChildren().add(tmenu.getMenu());
                        break;
                    case CHANGE_SIZE:
                        tmenu = new TiltMenu(d + Math.PI / 4, Math.PI / 2, 200);

                        tmenu.addItem(TiltMenu.Type.OPACITY, TiltMenu.Type.CANCEL, TiltMenu.Type.SIZE);
                        tmenu.setIndicator(d);
                        tmenu.setPosition(cercles.getChildren().get(illuminateIndex).getLayoutX(), cercles.getChildren().get(illuminateIndex).getLayoutY());
                        tmenu.setVisible(true);
                        etat = Etats.MENU;
                        menu.getChildren().add(tmenu.getMenu());
                        break;

                    case FREE:

                        break;

                    case SUN_SELECTED:
                        tmenu = new TiltMenu(d + Math.PI / 4, Math.PI / 2, 200);

                        tmenu.addItem(TiltMenu.Type.OPACITY, TiltMenu.Type.CANCEL, TiltMenu.Type.SIZE);
                        tmenu.setIndicator(d);
                        tmenu.setPosition(cercles.getChildren().get(illuminateIndex).getLayoutX(), cercles.getChildren().get(illuminateIndex).getLayoutY());
                        tmenu.setVisible(true);
                        etat = Etats.MENU;
                        menu.getChildren().add(tmenu.getMenu());


                        break;
                    case MENU:
                        tmenu.setIndicator(d);
                        break;

                }


            }
        });

    }

    private void translateSunShader(double x, double y) {
        circleObjectArray.get(illuminateIndex).translatePostion(multTranslation * x, multTranslation * y);
    }

    private void majFeedback(double x, double y) {
        if (circleObjectArray.size() > 0) {
            int index = getNearest(x, y);
            if (index != illuminateIndex) {
                for (CircleObject c : circleObjectArray) {
                    c.toNormal();
                    help.illuminateOptions();
                }
                illuminateIndex = index;
                if (illuminateIndex == -1) {
                    help.illuminateOptions(Help.Etats.HAND_CLOSE);
                    basket.handIn();
                } else if (illuminateIndex >= 0) {
                    basket.handOut();
                    help.illuminateOptions(Help.Etats.HAND_CLOSE);
                    circleObjectArray.get(illuminateIndex).toIlluminate();
                }
            }
        } else {
            if (basket.proximity(x, y)) {
                basket.handIn();
                illuminateIndex = -1;
                help.illuminateOptions(Help.Etats.HAND_CLOSE);
            } else {
                illuminateIndex = -2;
                basket.handOut();
            }
        }
    }

    private int getNearest(double x, double y) {
        double tempDistance = 5000;
        double tempCompare;
        int tempIndex = -2;
        if (circleObjectArray.size() > 0) {
            for (int i = 0; i < circleObjectArray.size(); i++) {

                tempCompare = circleObjectArray.get(i).proximity(x, y);
                if (tempCompare < tempDistance && tempCompare < 100) {
                    tempDistance = tempCompare;
                    tempIndex = i;
                }
            }
        } else {
            //rien n'est selectionné
        }
        if (basket.proximity(x, y)) {
            tempIndex = -1;
        }

        return tempIndex;
    }

    private boolean testGuardOpacity() {
        // teste si la main est en dehors de la sphère de centre (kinectPosXOpacity,kinectPosYOpacity) et de rayon r = opacityGuard
        return (Math.sqrt((kinectPosX - kinectPosXOpacity) * (kinectPosX - kinectPosXOpacity) + (kinectPosY - kinectPosYOpacity) * (kinectPosY - kinectPosYOpacity)) > opacityGuard);
    }

    private void majSize(double distance) {


        if (distance < limitLeft) {
            circleObjectArray.get(illuminateIndex).changeSize(distance - limitLeft);
            limitLeft = distance;

            distance2handsKinect = limitLeft + segmentSizeResize / 2;
            limitRight = limitLeft + segmentSizeResize;

        } else if (distance > limitRight) {
            circleObjectArray.get(illuminateIndex).changeSize(distance - limitRight);
            limitRight = distance;
            distance2handsKinect = limitRight - segmentSizeResize / 2;
            limitLeft = limitRight - segmentSizeResize;

        }
    }

    private void majOpacity(double z) {
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


                kinectServer.send("KINECT_POSITION X=" + (int) (me.getX() * kinectWindowSizeX / windowSizeX) + " Y=" + (int) (me.getY() * kinectWindowSizeY / windowSizeY) + " Z=0");
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

                kinectServer.send("KINECT_POSITION X=" + (int) (me.getX() * kinectWindowSizeX / windowSizeX) + " Y=" + (int) (me.getY() * kinectWindowSizeY / windowSizeY) + " Z=0");
            }
        });


        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            

            @Override
            public void handle(KeyEvent arg0) {
                if (stage.isFullScreen() && arg0.getCode() == KeyCode.F){
                    stage.setFullScreen(false);
                }else{
                    stage.setFullScreen(true);
                }
                if (help.isVisible() && arg0.getCode() == KeyCode.H){
                    help.setVisible(false);
                }else{
                    help.setVisible(true);
                }
                
                
            }
        });






    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        init(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
