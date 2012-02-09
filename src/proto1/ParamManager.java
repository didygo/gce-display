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
    double windowSizeHeight = 600;
    double windowSizeWidthFull = 1920;
    double windowSizeHeightFull = 1200;
    double windowBorderY = 100;
    double windowBorderX = 100;
    
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
    
    //timer destructeur en milisecondes
    int timerDuration = 1000;
    public ParamManager(){
        
    }
}
