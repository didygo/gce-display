package proto1;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JFileChooser;

/**
 *
 * @author jordane
 */
public class DashBoard extends Application {

    private Control controller;
    private Stage stage;
    private ImageView backImg, quitImg, reduceImg, selectImg, kinectbg;
    private Rectangle SelectionRect;
    private Scene scene;
    private ParamManager param1, param2;
    private Group kinectbgGroup;
    private int selectedProto = -1;
    //elements de modification
    private Slider widthOffsetSlider, heightOffsetSlider;
    private TextField widthOffsetTextField, heightOffsetTextField;
    private TextField defaultSize, minSize, maxSize, sizeSpeed;
    private TextField defaultOpacity, minOpacity, maxOpacity, opacitySpeed;
    private TextField widthWindow, heightWindow;
    private Button loadButton, saveButton, defaultButton;
    private ArrayList<Node> widgetArray;
    private MediaPlayer mediaPlayer1, mediaPlayer2;
    private MediaView mediaView1, mediaView2;
    private Rectangle rectSelect1, rectSelect2;
    private Button validationButton;
    private CheckBox checkOpacity, checkSize;
    private String curPath = "";
    private String curFile = "";
    private Label fileLabel;
    //varaible pour le deplacelemetn de la fenetre
    private double mouseX, mouseY;

    //initialisation de la scène
    private void init(Stage primaryStage) {

        stage = primaryStage;



        // relatif à la scène graphique window
        Group root = new Group();
        kinectbgGroup = new Group();
        widgetArray = new ArrayList<Node>();
        scene = new Scene(root, 800, 600);
        stage.setTitle("Gesture Exploration - default_pref.txt");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        //les paramètres
        param1 = new ParamManager();
        param2 = new ParamManager();
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
        fileLabel = new Label();
        fileLabel.setLayoutX(19);
        fileLabel.setLayoutY(3);
        
        checkOpacity = new CheckBox();
        checkOpacity.setLayoutX(575);
        checkOpacity.setLayoutY(303);
        widgetArray.add(checkOpacity);

        checkSize = new CheckBox();
        checkSize.setLayoutX(565);
        checkSize.setLayoutY(134);
        widgetArray.add(checkSize);

        widthOffsetSlider = new Slider(0, 300, param1.windowBorderX);
        widthOffsetSlider.setLayoutX(108);
        widthOffsetSlider.setLayoutY(216);
        widthOffsetSlider.setPrefWidth(180);
        widgetArray.add(widthOffsetSlider);


        heightOffsetSlider = new Slider(0, 200, 0);
        heightOffsetSlider.setLayoutX(108);
        heightOffsetSlider.setLayoutY(250);
        heightOffsetSlider.setPrefWidth(180);
        widgetArray.add(heightOffsetSlider);

        widthOffsetTextField = new TextField();
        widthOffsetTextField.setLayoutX(292);
        widthOffsetTextField.setLayoutY(213);
        widthOffsetTextField.setPrefWidth(40);
        widthOffsetTextField.setEditable(true);
        widgetArray.add(widthOffsetTextField);




        heightOffsetTextField = new TextField();
        heightOffsetTextField.setLayoutX(292);
        heightOffsetTextField.setLayoutY(247);
        heightOffsetTextField.setPrefWidth(40);
        heightOffsetTextField.setEditable(true);
        widgetArray.add(heightOffsetTextField);

        defaultSize = new TextField();
        defaultSize.setLayoutX(565);
        defaultSize.setLayoutY(46);
        defaultSize.setPrefWidth(50);
        defaultSize.setEditable(true);
        widgetArray.add(defaultSize);

        minSize = new TextField();
        minSize.setLayoutX(565);
        minSize.setLayoutY(90);
        minSize.setPrefWidth(50);
        minSize.setEditable(true);
        widgetArray.add(minSize);

        maxSize = new TextField();
        maxSize.setLayoutX(730);
        maxSize.setLayoutY(90);
        maxSize.setPrefWidth(50);
        maxSize.setEditable(true);
        widgetArray.add(maxSize);

        sizeSpeed = new TextField();
        sizeSpeed.setLayoutX(730);
        sizeSpeed.setLayoutY(133);
        sizeSpeed.setPrefWidth(50);
        sizeSpeed.setEditable(true);
        widgetArray.add(sizeSpeed);



        defaultOpacity = new TextField();
        defaultOpacity.setLayoutX(575);
        defaultOpacity.setLayoutY(228);
        defaultOpacity.setPrefWidth(50);
        defaultOpacity.setEditable(true);
        widgetArray.add(defaultOpacity);

        minOpacity = new TextField();
        minOpacity.setLayoutX(575);
        minOpacity.setLayoutY(267);
        minOpacity.setPrefWidth(50);
        minOpacity.setEditable(true);
        widgetArray.add(minOpacity);


        maxOpacity = new TextField();
        maxOpacity.setLayoutX(745);
        maxOpacity.setLayoutY(265);
        maxOpacity.setPrefWidth(45);
        maxOpacity.setEditable(true);
        widgetArray.add(maxOpacity);

        opacitySpeed = new TextField();
        opacitySpeed.setLayoutX(745);
        opacitySpeed.setLayoutY(303);
        opacitySpeed.setPrefWidth(45);
        opacitySpeed.setEditable(true);
        widgetArray.add(opacitySpeed);

        widthWindow = new TextField();
        widthWindow.setLayoutX(105);
        widthWindow.setLayoutY(175);
        widthWindow.setPrefWidth(50);
        widthWindow.setEditable(true);

        widgetArray.add(widthWindow);

        heightWindow = new TextField();
        heightWindow.setLayoutX(245);
        heightWindow.setLayoutY(175);
        heightWindow.setPrefWidth(50);
        heightWindow.setEditable(true);
        widgetArray.add(heightWindow);

        validationButton = new Button("Start");
        validationButton.setLayoutX(380);
        validationButton.setLayoutY(500);
        validationButton.setVisible(false);

        kinectbg = new ImageView(new Image("Images/fonds/ciel3.jpg"));
        kinectbgGroup.getChildren().add(kinectbg);
        kinectbg.setLayoutX(-kinectbg.getImage().getWidth() / 2);
        kinectbg.setLayoutY(-kinectbg.getImage().getHeight() / 2);
        kinectbg.setScaleX(190 / kinectbg.getImage().getWidth());
        kinectbg.setScaleY(115 / kinectbg.getImage().getHeight());
        kinectbgGroup.setLayoutX(230);
        kinectbgGroup.setLayoutY(329);

        saveButton = new Button("Save");
        saveButton.setLayoutX(360);
        saveButton.setLayoutY(50);
        saveButton.setPrefWidth(80);
        saveButton.setDisable(true);

        loadButton = new Button("Load");
        loadButton.setLayoutX(360);
        loadButton.setLayoutY(80);
        loadButton.setPrefWidth(80);

        defaultButton = new Button("Default");
        defaultButton.setLayoutX(360);
        defaultButton.setLayoutY(5);
        defaultButton.setPrefWidth(80);




        //ajout des vidéos
        Group mediaGroup1 = new Group();
        rectSelect1 = new Rectangle(330, 190, Color.RED);
        BoxBlur box = new BoxBlur(15, 15, 15);
        rectSelect1.setEffect(box);
        File dir1 = new File(".");
        try {
            mediaPlayer1 = new MediaPlayer(new Media("file:" + dir1.getCanonicalPath() + "/videos/protos/proto1.flv"));
        } catch (IOException ex) {
            Logger.getLogger(DashBoard.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        mediaGroup1.getChildren().addAll(rectSelect1, new Rectangle(330, 190, Color.BLACK), mediaView1);
        mediaGroup1.setLayoutX(10);
        mediaGroup1.setLayoutY(400);
        rectSelect1.setVisible(false);

        Group mediaGroup2 = new Group();
        rectSelect2 = new Rectangle(330, 190, Color.RED);
        rectSelect2.setEffect(box);
        try {
            mediaPlayer2 = new MediaPlayer(new Media("file:" + dir1.getCanonicalPath() + "/videos/protos/proto2.flv"));
        } catch (IOException ex) {
            Logger.getLogger(DashBoard.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        mediaGroup2.getChildren().addAll(rectSelect2, new Rectangle(330, 190, Color.BLACK), mediaView2);
        mediaGroup2.setLayoutX(460);
        mediaGroup2.setLayoutY(400);
        rectSelect2.setVisible(false);

        // comportement des widgets
        widgetBehavior();

        // ajouter à l'affichage

        root.getChildren().addAll(backImg, SelectionRect, quitImg, reduceImg, widthOffsetSlider, heightOffsetSlider, widthOffsetTextField, heightOffsetTextField, defaultSize, minSize, maxSize, sizeSpeed,
                defaultOpacity, minOpacity, maxOpacity, opacitySpeed, mediaGroup1, mediaGroup2, selectImg,
                validationButton, widthWindow, heightWindow, kinectbgGroup, checkOpacity, checkSize, saveButton, loadButton, defaultButton);

        for (Node n : widgetArray) {
            n.setVisible(false);
        }
        autoload();

    }

    private void installParameters() {

        if (selectedProto == 1) {
            widthWindow.setText("" + (int) param1.windowSizeWidth);
            heightWindow.setText("" + (int) param1.windowSizeHeight);
            widthOffsetSlider.setValue(param1.windowBorderX);
            heightOffsetSlider.setValue(param1.windowBorderY);
            widthOffsetTextField.setText("" + (int) param1.windowBorderX);
            heightOffsetTextField.setText("" + (int) param1.windowBorderY);
            defaultSize.setText("" + (int) param1.defaultSize);
            minSize.setText(("" + (int) param1.minimumSize));
            maxSize.setText("" + (int) param1.maximumSize);
            sizeSpeed.setText("" + (int) param1.constantSize);
            defaultOpacity.setText("" + (int) param1.defaultOpacity);
            minOpacity.setText("" + (int) param1.minimumOpacity);
            maxOpacity.setText("" + (int) param1.maximumOpacity);
            opacitySpeed.setText("" + (int) param1.constantOpacity);
            if (param1.opacityDirection == 1) {
                checkOpacity.setSelected(false);
            }
            if (param1.opacityDirection == -1) {
                checkOpacity.setSelected(true);
            }
            if (param1.sizeDirection == 1) {
                checkSize.setSelected(false);
            }
            if (param1.sizeDirection == -1) {
                checkSize.setSelected(true);
            }
        } else if (selectedProto == 2) {
            widthWindow.setText("" + (int) param2.windowSizeWidth);
            heightWindow.setText("" + (int) param2.windowSizeHeight);
            widthOffsetSlider.setValue(param2.windowBorderX);
            heightOffsetSlider.setValue(param2.windowBorderY);
            widthOffsetTextField.setText("" + (int) param2.windowBorderX);
            heightOffsetTextField.setText("" + (int) param2.windowBorderY);
            defaultSize.setText("" + (int) param2.defaultSize);
            minSize.setText(("" + (int) param2.minimumSize));
            maxSize.setText("" + (int) param2.maximumSize);
            sizeSpeed.setText("" + (int) param2.constantSize);
            defaultOpacity.setText("" + (int) param2.defaultOpacity);
            minOpacity.setText("" + (int) param2.minimumOpacity);
            maxOpacity.setText("" + (int) param2.maximumOpacity);
            opacitySpeed.setText("" + (int) param2.constantOpacity);
            if (param2.opacityDirection == 1) {
                checkOpacity.setSelected(false);
            }
            if (param2.opacityDirection == -1) {
                checkOpacity.setSelected(true);
            }
            if (param2.sizeDirection == 1) {
                checkSize.setSelected(false);
            }
            if (param2.sizeDirection == -1) {
                checkSize.setSelected(true);
            }
        }
        for (Node n : widgetArray) {
            n.setVisible(true);
        }
    }

    private void widgetBehavior() {
        //comportement des widgets
        loadButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                File f;
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Charger ");
                chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                System.out.println(curPath);
                if (!curPath.equals("")) {
                    chooser.setInitialDirectory(new File(curPath));
                }
                f = chooser.showOpenDialog(stage.getOwner());
                loadConfiguration(f);
                if (f != null) {
                    autosave(f.getAbsolutePath(), f.getName());
                }

            }
        });

        saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                saveConfiguration();
            }
        });

        defaultButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                param1 = new ParamManager();
                param2 = new ParamManager();
                installParameters();
            }
        });


        checkSize.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                if (selectedProto == 1) {
                    if (arg2) {
                        param1.sizeDirection = -1;
                    } else {
                        param1.sizeDirection = 1;
                    }

                } else if (selectedProto == 2) {
                    if (arg2) {
                        param2.sizeDirection = -1;
                    } else {
                        param2.sizeDirection = 1;
                    }
                }
            }
        });
        checkOpacity.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                if (selectedProto == 1) {
                    if (arg2) {
                        param1.opacityDirection = -1;
                    } else {
                        param1.opacityDirection = 1;
                    }

                } else if (selectedProto == 2) {
                    if (arg2) {
                        param2.opacityDirection = -1;
                    } else {
                        param2.opacityDirection = 1;
                    }
                }
            }
        });
        mediaView1.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                scene.setCursor(Cursor.HAND);
            }
        });
        mediaView1.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                scene.setCursor(Cursor.DEFAULT);
            }
        });
        mediaView2.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                scene.setCursor(Cursor.HAND);
            }
        });
        mediaView2.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                scene.setCursor(Cursor.DEFAULT);
            }
        });

        widthWindow.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                try {
                    if (selectedProto == 1) {
                        param1.windowSizeWidth = Double.parseDouble(arg2);
                    } else if (selectedProto == 2) {
                        param2.windowSizeWidth = Double.parseDouble(arg2);
                    }
                    widthWindow.setStyle("-fx-background-color: #ffffff");
                } catch (Exception e) {
                    widthWindow.setStyle("-fx-background-color: #E80303");
                }

            }
        });




        heightWindow.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                try {
                    if (selectedProto == 1) {
                        param1.windowSizeHeight = Double.parseDouble(arg2);
                    } else if (selectedProto == 2) {
                        param2.windowSizeHeight = Double.parseDouble(arg2);
                    }
                    heightWindow.setStyle("-fx-background-color: #ffffff");
                } catch (Exception e) {
                    heightWindow.setStyle("-fx-background-color: #E80303");
                }
            }
        });
        validationButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                startApplication(false);
            }
        });
        widthOffsetSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                widthOffsetTextField.setText("" + (int) widthOffsetSlider.getValue());


            }
        });
        heightOffsetSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                heightOffsetTextField.setText("" + (int) heightOffsetSlider.getValue());
            }
        });

        widthOffsetTextField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                try {
                    if (selectedProto == 1) {
                        param1.windowBorderX = Double.parseDouble(widthOffsetTextField.getText());
                    } else if (selectedProto == 2) {
                        param2.windowBorderX = Double.parseDouble(widthOffsetTextField.getText());
                    }
                    kinectbg.setScaleX((190 - (2 * Double.parseDouble(widthOffsetTextField.getText()) * 190 / kinectbg.getImage().getWidth())) / kinectbg.getImage().getWidth());
                    widthOffsetTextField.setStyle("-fx-background-color: #ffffff");
                    widthOffsetSlider.adjustValue(Double.parseDouble(widthOffsetTextField.getText()));
                } catch (Exception e) {
                    widthOffsetTextField.setStyle("-fx-background-color: #E80303");
                }

            }
        });

     

        heightOffsetTextField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                try{
                if (selectedProto == 1) {
                    param1.windowBorderY = Double.parseDouble(arg2);
                } else if (selectedProto == 2) {
                    param2.windowBorderY = Double.parseDouble(arg2);
                }
                kinectbg.setScaleY((115 - (2 * Double.parseDouble(arg2) * 115 / kinectbg.getImage().getHeight())) / kinectbg.getImage().getHeight());
                heightOffsetSlider.adjustValue(Double.parseDouble(arg2));
                widthOffsetTextField.setStyle("-fx-background-color: #ffffff");
                   
                } catch (Exception e) {
                    widthOffsetTextField.setStyle("-fx-background-color: #E80303");
                }
            }
        });
        
        defaultSize.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {

                try {
                    if (selectedProto == 1) {
                        param1.defaultSize = Double.parseDouble(arg2);
                    } else if (selectedProto == 2) {
                        param2.defaultSize = Double.parseDouble(arg2);
                    }
                    defaultSize.setStyle("-fx-background-color: #ffffff");
                } catch (Exception e) {
                    defaultSize.setStyle("-fx-background-color: #E80303");
                }
            }
        });
        minSize.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                try {
                    if (selectedProto == 1) {
                        param1.minimumSize = Double.parseDouble(arg2);
                    } else if (selectedProto == 2) {
                        param2.minimumSize = Double.parseDouble(arg2);
                    }
                    minSize.setStyle("-fx-background-color: #ffffff");
                } catch (Exception e) {
                    minSize.setStyle("-fx-background-color: #E80303");
                }
            }
        });
        maxSize.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                try {
                    if (selectedProto == 1) {
                        param1.maximumSize = Double.parseDouble(arg2);
                    } else if (selectedProto == 2) {
                        param2.maximumSize = Double.parseDouble(arg2);
                    }
                    maxSize.setStyle("-fx-background-color: #ffffff");
                } catch (Exception e) {
                    maxSize.setStyle("-fx-background-color: #E80303");
                }
            }
        });
        sizeSpeed.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                try {
                    if (selectedProto == 1) {
                        param1.constantSize = Double.parseDouble(arg2);
                    } else if (selectedProto == 2) {
                        param2.constantSize = Double.parseDouble(arg2);
                    }
                    sizeSpeed.setStyle("-fx-background-color: #ffffff");
                } catch (Exception e) {
                    sizeSpeed.setStyle("-fx-background-color: #E80303");
                }
            }
        });
        defaultOpacity.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                try {
                    if (selectedProto == 1) {
                        param1.defaultOpacity = Double.parseDouble(arg2);
                    } else if (selectedProto == 2) {
                        param2.defaultOpacity = Double.parseDouble(arg2);
                    }
                    defaultOpacity.setStyle("-fx-background-color: #ffffff");
                } catch (Exception e) {
                    defaultOpacity.setStyle("-fx-background-color: #E80303");
                }
            }
        });
        minOpacity.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {

                try {
                    if (selectedProto == 1) {
                        param1.minimumOpacity = Double.parseDouble(arg2);
                    } else if (selectedProto == 2) {
                        param2.minimumOpacity = Double.parseDouble(arg2);
                    }
                    minOpacity.setStyle("-fx-background-color: #ffffff");
                } catch (Exception e) {
                    minOpacity.setStyle("-fx-background-color: #E80303");
                }
            }
        });
        maxOpacity.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                try {
                    if (selectedProto == 1) {
                        param1.maximumOpacity = Double.parseDouble(arg2);
                    } else if (selectedProto == 2) {
                        param2.maximumOpacity = Double.parseDouble(arg2);
                    }
                    maxOpacity.setStyle("-fx-background-color: #ffffff");
                } catch (Exception e) {
                    maxOpacity.setStyle("-fx-background-color: #E80303");
                }
            }
        });
        opacitySpeed.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                try {
                    if (selectedProto == 1) {
                        param1.constantOpacity = Double.parseDouble(arg2);
                    } else if (selectedProto == 2) {
                        param2.constantOpacity = Double.parseDouble(arg2);
                    }
                    opacitySpeed.setStyle("-fx-background-color: #ffffff");
                } catch (Exception e) {
                    opacitySpeed.setStyle("-fx-background-color: #E80303");
                }
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
                validationButton.setVisible(true);
                saveButton.setDisable(false);
                installParameters();
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
                validationButton.setVisible(true);
                saveButton.setDisable(false);
                installParameters();
            }
        });

    }

    private void createQuitReduce() {
        quitImg.setPickOnBounds(true);
        quitImg.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                quitImg.setImage(new Image("Images/fonds/quitImg2.png"));
                scene.setCursor(Cursor.HAND);

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

    public void loadConfiguration(File nomFichier) {
        if (nomFichier != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(nomFichier)));
                int numProto = Integer.parseInt(reader.readLine());

                if (numProto == 1) {
                    param1.windowSizeWidth = Double.parseDouble(reader.readLine());
                    param1.windowSizeHeight = Double.parseDouble(reader.readLine());
                    param1.windowBorderX = Double.parseDouble(reader.readLine());
                    param1.windowBorderY = Double.parseDouble(reader.readLine());
                    param1.defaultSize = Double.parseDouble(reader.readLine());
                    param1.minimumSize = Double.parseDouble(reader.readLine());
                    param1.maximumSize = Double.parseDouble(reader.readLine());
                    param1.sizeDirection = Integer.parseInt(reader.readLine());
                    param1.constantSize = Double.parseDouble(reader.readLine());
                    param1.defaultOpacity = Double.parseDouble(reader.readLine());
                    param1.minimumOpacity = Double.parseDouble(reader.readLine());
                    param1.maximumOpacity = Double.parseDouble(reader.readLine());
                    param1.opacityDirection = Integer.parseInt(reader.readLine());
                    param1.constantOpacity = Double.parseDouble(reader.readLine());

                    //mise à jour de l'ihm
                    mediaView1.setOpacity(1);
                    mediaView2.setOpacity(0.4);
                    rectSelect1.setVisible(true);
                    rectSelect2.setVisible(false);
                    selectedProto = 1;
                    selectImg.setVisible(false);
                    validationButton.setVisible(true);
                    installParameters();
                } else {
                    param2.windowSizeWidth = Double.parseDouble(reader.readLine());
                    param2.windowSizeHeight = Double.parseDouble(reader.readLine());
                    param2.windowBorderX = Double.parseDouble(reader.readLine());
                    param2.windowBorderY = Double.parseDouble(reader.readLine());
                    param2.defaultSize = Double.parseDouble(reader.readLine());
                    param2.minimumSize = Double.parseDouble(reader.readLine());
                    param2.maximumSize = Double.parseDouble(reader.readLine());
                    param2.sizeDirection = Integer.parseInt(reader.readLine());
                    param2.constantSize = Double.parseDouble(reader.readLine());
                    param2.defaultOpacity = Double.parseDouble(reader.readLine());
                    param2.minimumOpacity = Double.parseDouble(reader.readLine());
                    param2.maximumOpacity = Double.parseDouble(reader.readLine());
                    param2.opacityDirection = Integer.parseInt(reader.readLine());
                    param2.constantOpacity = Double.parseDouble(reader.readLine());

                    // mise à jour de l'ihm
                    mediaView2.setOpacity(1);
                    mediaView1.setOpacity(0.4);
                    rectSelect2.setVisible(true);
                    rectSelect1.setVisible(false);
                    selectedProto = 2;
                    selectImg.setVisible(false);
                    validationButton.setVisible(true);
                    installParameters();
                }


                reader.close();



            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void autosave(String path, String fileName) {
        File f;

        f = new File("configs/autosave.txt");

        if (f.exists()) {
            f.delete();
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(DashBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            f.createNewFile();
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter output = new BufferedWriter(fw);
            output.write(path + "\r\n");
            output.write(fileName + "\r\n");
            output.flush();
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(DashBoard.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private void autoload() {
        File f;

        f = new File("configs/autosave.txt");
        if (f.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream("configs/autosave.txt")));

                curPath = reader.readLine();
                curFile = curPath +"/"+reader.readLine();
                System.out.println(curFile);
                reader.close();
                reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(curFile)));
                int numProto = Integer.parseInt(reader.readLine());

                if (numProto == 1) {
                    param1.windowSizeWidth = Double.parseDouble(reader.readLine());
                    param1.windowSizeHeight = Double.parseDouble(reader.readLine());
                    param1.windowBorderX = Double.parseDouble(reader.readLine());
                    param1.windowBorderY = Double.parseDouble(reader.readLine());
                    param1.defaultSize = Double.parseDouble(reader.readLine());
                    param1.minimumSize = Double.parseDouble(reader.readLine());
                    param1.maximumSize = Double.parseDouble(reader.readLine());
                    param1.sizeDirection = Integer.parseInt(reader.readLine());
                    param1.constantSize = Double.parseDouble(reader.readLine());
                    param1.defaultOpacity = Double.parseDouble(reader.readLine());
                    param1.minimumOpacity = Double.parseDouble(reader.readLine());
                    param1.maximumOpacity = Double.parseDouble(reader.readLine());
                    param1.opacityDirection = Integer.parseInt(reader.readLine());
                    param1.constantOpacity = Double.parseDouble(reader.readLine());

                } else {
                    param2.windowSizeWidth = Double.parseDouble(reader.readLine());
                    param2.windowSizeHeight = Double.parseDouble(reader.readLine());
                    param2.windowBorderX = Double.parseDouble(reader.readLine());
                    param2.windowBorderY = Double.parseDouble(reader.readLine());
                    param2.defaultSize = Double.parseDouble(reader.readLine());
                    param2.minimumSize = Double.parseDouble(reader.readLine());
                    param2.maximumSize = Double.parseDouble(reader.readLine());
                    param2.sizeDirection = Integer.parseInt(reader.readLine());
                    param2.constantSize = Double.parseDouble(reader.readLine());
                    param2.defaultOpacity = Double.parseDouble(reader.readLine());
                    param2.minimumOpacity = Double.parseDouble(reader.readLine());
                    param2.maximumOpacity = Double.parseDouble(reader.readLine());
                    param2.opacityDirection = Integer.parseInt(reader.readLine());
                    param2.constantOpacity = Double.parseDouble(reader.readLine());
                }
                

            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void saveConfiguration() {
        File f;
        int choix = 0;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Sauvegarder ");
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        System.out.println(System.getProperty("user.dir"));
        if (!curPath.equals("")) {
            System.out.println(curPath);
            chooser.setInitialDirectory(new File(curPath));
        }
        f = chooser.showSaveDialog(stage.getOwner());
        if (f != null) {
            curPath = f.getAbsolutePath();
            if (f.exists()) {
                f.delete();
            }
            try {
                FileWriter fw = new FileWriter(f, true);
                BufferedWriter output = new BufferedWriter(fw);
                output.write(selectedProto + "\r\n");
                if (selectedProto == 1) {
                    output.write(param1.windowSizeWidth + "\r\n");
                    output.write(param1.windowSizeHeight + "\r\n");
                    output.write(param1.windowBorderX + "\r\n");
                    output.write(param1.windowBorderY + "\r\n");
                    output.write(param1.defaultSize + "\r\n");
                    output.write(param1.minimumSize + "\r\n");
                    output.write(param1.maximumSize + "\r\n");
                    output.write(param1.sizeDirection + "\r\n");
                    output.write(param1.constantSize + "\r\n");
                    output.write(param1.defaultOpacity + "\r\n");
                    output.write(param1.minimumOpacity + "\r\n");
                    output.write(param1.maximumOpacity + "\r\n");
                    output.write(param1.opacityDirection + "\r\n");
                    output.write(param1.constantOpacity + "\r\n");
                } else {
                    output.write(param2.windowSizeWidth + "\r\n");
                    output.write(param2.windowSizeHeight + "\r\n");
                    output.write(param2.windowBorderX + "\r\n");
                    output.write(param2.windowBorderY + "\r\n");
                    output.write(param2.defaultSize + "\r\n");
                    output.write(param2.minimumSize + "\r\n");
                    output.write(param2.maximumSize + "\r\n");
                    output.write(param2.sizeDirection + "\r\n");
                    output.write(param2.constantSize + "\r\n");
                    output.write(param2.defaultOpacity + "\r\n");
                    output.write(param2.minimumOpacity + "\r\n");
                    output.write(param2.maximumOpacity + "\r\n");
                    output.write(param2.opacityDirection + "\r\n");
                    output.write(param2.constantOpacity + "\r\n");
                }
                autosave(f.getParent(), f.getName());
                curPath = f.getParent();


                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }






    }

    public void stopApplication() {
        try {
            controller.getStage().close();
            controller.stop();



        } catch (Exception ex) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startApplication(boolean full) {


        try {

            if (selectedProto == 1) {
                controller = new Controller(this, full, param1);
            } else if (selectedProto == 2) {
                controller = new Proto2(this, full, param2);
            }

            stage = new Stage(StageStyle.DECORATED);
            controller.start(stage);

        } catch (Exception ex) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fullScreen(boolean b) {

        if (b) {
            stopApplication();
            startApplication(b);
        } else {
            stopApplication();
            startApplication(b);
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
