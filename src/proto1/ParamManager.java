package proto1;

import java.awt.Toolkit;

/**
 *
 * @author jordane
 */
public class ParamManager {
    
    //variables de la kinect
    public double kinectWindowWidth = 640;
    public double kinectWindowHeight = 480;
    
    //taille de la fenêtre en normal et plein écran
    public double windowSizeWidth = 330*4;
    public double windowSizeHeight = 190*4;
    public double windowSizeWidthFull = 1920;//Toolkit.getDefaultToolkit().getScreenSize().width;
    public double windowSizeHeightFull = 1200;//Toolkit.getDefaultToolkit().getScreenSize().height;
    public double windowBorderY = 100;
    public double windowBorderX = 200;
    
    // vitesse d'opacification et de resize
    public double constantOpacity = 2;
    public double constantSize = 2;
    
    // demultiplication du mouvement
    public double constantMultMove = 1;
    
    //minimums et maximums taille et opacité
    public double minimumSize = 50;
    public double minimumOpacity = 0;
    public double maximumSize = 300;
    public double maximumOpacity = 300;
    
    //sens de modification
    public int sizeDirection = +1;
    public int opacityDirection = +1;
    
    //taille et opacité par défaut
    public double defaultSize = 90;
    public double defaultOpacity = 80;
    
    //timer destructeur en milisecondes
    public int timerDuration = 1000;
    
    //paramètres pour les maxima et minima de profondeur (relatifs à l'affichage)
    public double minDepthIHM = 500;
    public double maxDepthIHM = 1000;
    
    //paramètre kinect qui correspond au nombre de fois que l'algorithme vérifie ses calculs
    public int constantRobustness = 1;
    
    // adresse du bus de communication Ivy
    public String busAdress = "127.0.0.1:2010";
    public ParamManager(){
        
    }
    
    
}
