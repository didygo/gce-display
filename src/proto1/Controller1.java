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

    Group root, cercles,menu;
    ArrayList<CircleObject> circleObjectArray;
    private double kinectPosX = 0;
    private double kinectPosY = 0;
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
    double multTranslation = 1.4;
    //les TiltMenu
    TiltMenu tmenu;

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
        windowSizeX = 1024;
        windowSizeY = 768;
        kinectWindowSizeX = 640;
        kinectWindowSizeY = 480;
        root = new Group();
        Scene scene = new Scene(root, windowSizeX, windowSizeY);
        ImageView background = new ImageView(new Image("Images/fonds/ciel2.jpg"));
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
        adresseBus = "169.254.255.255:2010";//"10.3.8.255:2010";
        kinectServer = new KinectServer1(this, adresseBus, windowSizeX, windowSizeY);
        //////////////////////////////////////////////////////////////
        //gestionEvenementsSouris(scene);
        /// 3) Initialisation des interactions pour prototype I //
        etat = Etats.FREE;
        basket = new CircleBasket(root, windowSizeX + 40, windowSizeY);

        circleObjectArray = new ArrayList();
        cercles = new Group();
        menu = new Group();
        root.getChildren().addAll(cercles,menu);
        ////////////////////////////////////////////////////////

        connectionTool = new ConnectionTool(root, windowSizeX - 50, windowSizeY - 50, kinectServer);
        manConnectionTool = new ManConnectionTool(root, windowSizeX - 150, windowSizeY - 50, kinectServer);


        




    }

    public void handSelect() {
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

                        if (illuminateIndex >= 0 && circleObjectArray.size() > 0) {

                            circleObjectArray.get(illuminateIndex).select();
                            basket.makeItEmpty();
                            etat = Etats.SUN_SELECTED;

                        } else if (illuminateIndex == -1) {
                            circleObjectArray.add(new CircleObject(kinectPosX, kinectPosY, cercles));
                            //attention au changment de coordonnée

                            illuminateIndex = circleObjectArray.size() - 1;
                            circleObjectArray.get(illuminateIndex).toIlluminate();
                            circleObjectArray.get(illuminateIndex).select();
                            basket.sunCaught();
                            etat = Etats.SUN_SELECTED;

                        }
                        break;

                    case SUN_SELECTED:
                        // impossible

                        break;
                    case MENU:
                        switch(tmenu.selected()){
                            case OPACITY:
                                etat = Etats.CHANGE_OPACITY;
                                break;
                            case SIZE:
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
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                switch (etat) {
                    case SUPER_FREE:

                        break;
                    case CHANGE_OPACITY:
                        etat = Etats.FREE;
                        circleObjectArray.get(illuminateIndex).unSelect();
                        break;
                    case CHANGE_SIZE:
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

                        } else {

                            basket.makeItFull();
                        }
                        etat = Etats.FREE;
                        break;

                }
            }
        });
        //eraseInvisibleSun();

    }

    public void eventKinectMove(final double x, final double y) {
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
                        if (testGuardOpacity()) {
                            etat = Etats.SUN_SELECTED;
                        }
                        //interdit
                        break;
                    case CHANGE_SIZE:
                        kinectPosX = x;
                        kinectPosY = y;
                        //interdit
                        break;

                    case FREE:
                        kinectPosX = x;
                        kinectPosY = y;
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
                        kinectPosXOpacity = x;
                        kinectPosXResize = x;
                        kinectPosYOpacity = y;
                        kinectPosYResize = y;
                        break;
                    case MENU:
                        kinectPosX = x;
                        kinectPosY = y;
                        break;
                }
            }
        });
    }

    public void eventKinect2Hands(final double distance) {
        System.out.println(etat);
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
        System.out.println(etat);
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

    public void eventFingerAngle(final double d) {
       
            
            Platform.runLater(new Runnable() {

                @Override
                public void run() {

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
                            tmenu = new TiltMenu(d + Math.PI / 4, Math.PI / 2, 200);

                            tmenu.addItem(TiltMenu.Type.OPACITY, TiltMenu.Type.SIZE, TiltMenu.Type.CANCEL);
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
                }
                illuminateIndex = index;
                if (illuminateIndex == -1) {
                    basket.handIn();
                } else {
                    //ça va mal
                    basket.handOut();
                    circleObjectArray.get(illuminateIndex).toIlluminate();
                }
            }
        } else {
            if (basket.proximity(x, y)) {
                basket.handIn();
                illuminateIndex = -1;
            } else {
                illuminateIndex = -2;
                basket.handOut();
            }
        }
    }

    private int getNearest(double x, double y) {
        double tempDistance = 5000;
        double tempCompare;
        int tempIndex = -1;
        if (circleObjectArray.size() > 0) {
            for (int i = 0; i < circleObjectArray.size(); i++) {

                tempCompare = circleObjectArray.get(i).proximity(x, y);
                if (tempCompare < tempDistance) {
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

    private void eraseInvisibleSun() {
        for (int i = 0; i < circleObjectArray.size(); i++) {
            if (cercles.getChildren().get(i).getOpacity() == 0) {
                cercles.getChildren().remove(i);
                circleObjectArray.remove(i);
                i--;
            }
        }
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


                kinectServer.send("KINECT_POSITION X=" + (int) (me.getX() * kinectWindowSizeX / windowSizeX) + " Y=" + (int) (me.getY() * kinectWindowSizeY / windowSizeY));
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

                kinectServer.send("KINECT_POSITION X=" + (int) (me.getX() * kinectWindowSizeX / windowSizeX) + " Y=" + (int) (me.getY() * kinectWindowSizeY / windowSizeY));
            }
        });


        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent arg0) {

                if (arg0.getCode() == KeyCode.UP) {
                    distSize += 2;
                    kinectServer.send("KINECT_2HANDS_DISTANCE=" + distSize);

                } else if (arg0.getCode() == KeyCode.DOWN) {
                    distSize -= 2;
                    kinectServer.send("KINECT_2HANDS_DISTANCE=" + distSize);

                } else if (arg0.getCode() == KeyCode.LEFT) {
                    distOpacity -= 2;
                    kinectServer.send("KINECT_HAND_DEPTH=" + distOpacity);
                } else if (arg0.getCode() == KeyCode.RIGHT) {
                    distOpacity += 2;
                    kinectServer.send("KINECT_HAND_DEPTH=" + distOpacity);
                }
            }
        });






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
