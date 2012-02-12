package proto1;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.ArrayList;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author jordane
 */
public class DashBoard extends Application {
    
    private ImageView backImg,quitImg,reduceImg,selectImg;
    private Rectangle SelectionRect;
    private Scene scene;
    private Stage stage;
    private ParamManager param;
    private int selectedProto = -1;
    //elements de modification
    private Slider widthOffsetSlider,heightOffsetSlider;
    private TextField widthOffsetTextField, heightOffsetTextField;
    private TextField defaultSize,minSize,maxSize,sizeSpeed;
    private TextField defaultOpacity, minOpacity, maxOpacity, opacitySpeed;
    private ArrayList<Node> widgetArray;
    private MediaPlayer mediaPlayer1,mediaPlayer2;
    private MediaView mediaView1,mediaView2;
    private Rectangle rectSelect1, rectSelect2;
    
    //varaible pour le deplacelemetn de la fenetre
    private double mouseX,mouseY;
    

    //initialisation de la scène
    private void init(Stage primaryStage) {
        stage =primaryStage;
        // relatif à la scène graphique window
        Group root = new Group();
        widgetArray = new ArrayList<>();
        scene = new Scene(root, 800,600);
        stage.setTitle("Gesture Exploration");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        //les paramètres
        param= new ParamManager();
        // fond d'ecran
        quitImg = new ImageView(new Image("Images/fonds/quitImg.png"));
        quitImg.setX(780);
        quitImg.setY(4);
        reduceImg = new ImageView(new Image("Images/fonds/reduceImg.png"));
        reduceImg.setX(760);
        reduceImg.setY(4);
        backImg = new ImageView(new Image("Images/fonds/DashBoard.png"));
        selectImg = new ImageView(new Image("Images/fonds/seletOne.png"));
        selectImg.setLayoutX(343);
        selectImg.setLayoutY(425);
        
        // permettre le déplacement de la fenêtre 
        createMoveBar();
        createQuitReduce();
        
        // mettre sur la scène les éléments permettant la modification (slider, buttons ...)
        widthOffsetSlider = new Slider(0, 300, param.windowBorderX);
        widthOffsetSlider.setLayoutX(108);
        widthOffsetSlider.setLayoutY(216);
        widthOffsetSlider.setPrefWidth(180);
        widgetArray.add(widthOffsetSlider);
        
        heightOffsetSlider = new Slider(0, 200, param.windowBorderY);
        heightOffsetSlider.setLayoutX(108);
        heightOffsetSlider.setLayoutY(250);
        heightOffsetSlider.setPrefWidth(180);
        widgetArray.add(heightOffsetSlider);
        
        widthOffsetTextField = new TextField("" + (int)param.windowBorderX);
        widthOffsetTextField.setLayoutX(292);
        widthOffsetTextField.setLayoutY(213);
        widthOffsetTextField.setPrefWidth(40);
        widthOffsetTextField.setEditable(true);
        widgetArray.add(widthOffsetTextField);
        
        heightOffsetTextField = new TextField("" + (int)param.windowBorderY);
        heightOffsetTextField.setLayoutX(292);
        heightOffsetTextField.setLayoutY(247);
        heightOffsetTextField.setPrefWidth(40);
        heightOffsetTextField.setEditable(true);
        widgetArray.add(heightOffsetTextField);
        
        defaultSize = new TextField(""+ (int)param.defaultSize);
        defaultSize.setLayoutX(565);
        defaultSize.setLayoutY(46);
        defaultSize.setPrefWidth(50);
        defaultSize.setEditable(true);
        widgetArray.add(defaultSize);
        
        minSize = new TextField(""+ (int)param.minimumSize);
        minSize.setLayoutX(565);
        minSize.setLayoutY(90);
        minSize.setPrefWidth(50);
        minSize.setEditable(true);
        widgetArray.add(minSize);
        
        maxSize = new TextField(""+ (int)param.maximumSize);
        maxSize.setLayoutX(730);
        maxSize.setLayoutY(90);
        maxSize.setPrefWidth(50);
        maxSize.setEditable(true);
        widgetArray.add(maxSize);
        
        sizeSpeed = new TextField(""+ (int)param.constantSize);
        sizeSpeed.setLayoutX(730);
        sizeSpeed.setLayoutY(133);
        sizeSpeed.setPrefWidth(50);
        sizeSpeed.setEditable(true);
        widgetArray.add(sizeSpeed);
        
        
        
        defaultOpacity = new TextField(""+ (int)param.defaultOpacity);
        defaultOpacity.setLayoutX(575);
        defaultOpacity.setLayoutY(228);
        defaultOpacity.setPrefWidth(50);
        defaultOpacity.setEditable(true);
        widgetArray.add(defaultOpacity);
        
        minOpacity = new TextField(""+ (int)param.minimumOpacity);
        minOpacity.setLayoutX(575);
        minOpacity.setLayoutY(267);
        minOpacity.setPrefWidth(50);
        minOpacity.setEditable(true);
        widgetArray.add(minOpacity);
        
        
        maxOpacity = new TextField(""+ (int)param.maximumOpacity);
        maxOpacity.setLayoutX(745);
        maxOpacity.setLayoutY(265);
        maxOpacity.setPrefWidth(45);
        maxOpacity.setEditable(true);
        widgetArray.add(maxOpacity);
        
        opacitySpeed = new TextField(""+ (int)param.constantOpacity);
        opacitySpeed.setLayoutX(745);
        opacitySpeed.setLayoutY(303);
        opacitySpeed.setPrefWidth(45);
        opacitySpeed.setEditable(true);
        widgetArray.add(opacitySpeed);
        
        
        
        
        //ajout des vidéos
        Group mediaGroup1 = new Group();
        rectSelect1 = new Rectangle(330, 190, Color.RED);
        BoxBlur box = new BoxBlur(15, 15, 15);
        rectSelect1.setEffect(box);
        mediaPlayer1 = new MediaPlayer(new Media("http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv"));
        mediaView1 = new MediaView(mediaPlayer1);
        mediaView1.setPreserveRatio(false);
        //mediaView1.setLayoutX(10);
        //mediaView1.setLayoutY(400);
        mediaView1.setFitWidth(330);
        mediaView1.setFitHeight(190);
        mediaView1.setOpacity(0.4);
        mediaPlayer1.setMute(true);
        mediaPlayer1.setCycleCount(Timeline.INDEFINITE);
        mediaPlayer1.play();
        mediaGroup1.getChildren().addAll(rectSelect1,new Rectangle(330, 190, Color.BLACK),mediaView1);
        mediaGroup1.setLayoutX(10);
        mediaGroup1.setLayoutY(400);
        rectSelect1.setVisible(false);
        
        Group mediaGroup2 = new Group();
        rectSelect2 = new Rectangle(330, 190, Color.RED);
        rectSelect2.setEffect(box);
        mediaPlayer2 = new MediaPlayer(new Media("http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv"));
        mediaView2 = new MediaView(mediaPlayer2);
        mediaView2.setPreserveRatio(false);
        //mediaView2.setLayoutX(460);
        //mediaView2.setLayoutY(400);
        mediaView2.setFitWidth(330);
        mediaView2.setFitHeight(190);
        mediaView2.setOpacity(0.4);
        mediaPlayer2.setMute(true);
        mediaPlayer2.setCycleCount(Timeline.INDEFINITE);
        mediaPlayer2.play();
        mediaGroup2.getChildren().addAll(rectSelect2,new Rectangle(330, 190, Color.BLACK),mediaView2);
        mediaGroup2.setLayoutX(460);
        mediaGroup2.setLayoutY(400);
        rectSelect2.setVisible(false);
        
        // comportement des widgets
        widgetBehavior();
        
        // ajouter à l'affichage
        
        root.getChildren().addAll(backImg,SelectionRect,quitImg,reduceImg,widthOffsetSlider,heightOffsetSlider
                ,widthOffsetTextField,heightOffsetTextField,defaultSize,minSize,maxSize,sizeSpeed,
                defaultOpacity,minOpacity,maxOpacity,opacitySpeed,mediaGroup1,mediaGroup2,selectImg);
        
        
        

        

    }

    private void widgetBehavior() {
        //comportement des widgets
        widthOffsetSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    widthOffsetTextField.setText("" + (int)widthOffsetSlider.getValue());
                }
            });
        heightOffsetSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    heightOffsetTextField.setText("" + (int)heightOffsetSlider.getValue());
                }
            });
        widthOffsetTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent arg0) {
               if (arg0.getCode() == KeyCode.ENTER) {
                    widthOffsetSlider.adjustValue(Double.parseDouble(widthOffsetTextField.getText()));
                }
            }
        });
        heightOffsetTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent arg0) {
               if (arg0.getCode() == KeyCode.ENTER) {
                    heightOffsetSlider.adjustValue(Double.parseDouble(heightOffsetTextField.getText()));
                }
            }
        });
        defaultSize.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                
                // à compléter
            }
        });
        minSize.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                
                // à compléter
            }
        });
        maxSize.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                
                // à compléter
            }
        });
        sizeSpeed.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                
                // à compléter
            }
        });
        defaultOpacity.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                
                // à compléter
            }
        });
        minOpacity.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                
                // à compléter
            }
        });
        maxOpacity.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                
                // à compléter
            }
        });
        opacitySpeed.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                
                // à compléter
            }
        });
        mediaView1.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                mediaView1.setOpacity(1);
                mediaView2.setOpacity(0.4);
                rectSelect1.setVisible(true);
                rectSelect2.setVisible(false);
                selectedProto = 1;
                selectImg.setVisible(false);
            }
        });
        mediaView2.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                mediaView2.setOpacity(1);
                mediaView1.setOpacity(0.4);
                rectSelect2.setVisible(true);
                rectSelect1.setVisible(false);
                selectedProto = 2;
                selectImg.setVisible(false);
            }
        });
        
    }

    private void createQuitReduce() {
        quitImg.setPickOnBounds(true);
        quitImg.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                quitImg.setImage(new Image("Images/fonds/quitImg2.png"));
                scene.setCursor(Cursor.CLOSED_HAND);

            }
        });
        quitImg.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                quitImg.setImage(new Image("Images/fonds/quitImg.png"));
                scene.setCursor(Cursor.DEFAULT);
            }
        });
        quitImg.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                System.exit(0);
            }
        });
        reduceImg.setPickOnBounds(true);
        reduceImg.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                scene.setCursor(Cursor.CLOSED_HAND);
                reduceImg.setImage(new Image("Images/fonds/reduceImg2.png"));

            }
        });
        reduceImg.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                scene.setCursor(Cursor.DEFAULT);
                reduceImg.setImage(new Image("Images/fonds/reduceImg.png"));
            }
        });
        reduceImg.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                stage.setIconified(true);
            }
        });
    }

    private void createMoveBar() {
        SelectionRect = new Rectangle(800, 30);
        SelectionRect.setFill(Color.TRANSPARENT);
        SelectionRect.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                mouseX = arg0.getScreenX();
                mouseY = arg0.getScreenY();
            }
        });
        SelectionRect.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {

                scene.getWindow().setX(scene.getWindow().getX() + arg0.getScreenX() - mouseX);
                scene.getWindow().setY(scene.getWindow().getY() + arg0.getScreenY() - mouseY);
                mouseX = arg0.getScreenX();
                mouseY = arg0.getScreenY();
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();

    }
    public static void main(String[] args) { launch(args); }
}
