/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

/**
 *
 * @author jordane
 */
public class ParamManager {
    
    //variables de la kinect
    double kinectWindowWidth = 640;
    double kinectWindowHeight = 480;
    
    //taille de la fenêtre en normal et plein écran
    double windowSizeWidth = 800;
    double windowsizeHeight = 600;
    double windowSizeWidthFull = 1440;
    double windowsizeHeightFull = 900;
    
    // vitesse d'opacification et de resize
    double constantOpacity = 2;
    double constantSize = 2;
    
    // demultiplication du mouvement
    double constantMultMove = 1;
    
    //minimums et maximums taille et opacité
    double minimumSize = 50;
    double minimumOpacity = 0;
    double maximumSize = 300;
    double maximumOpacity = 300;
    
    //sens de modification
    int sizeDirection = +1;
    int opacityDirection = +1;
    
    //taille et opacité par défaut
    double defaultSize = 90;
    double defaultOpacity = 80;
    
    public ParamManager(){
        
    }
}
