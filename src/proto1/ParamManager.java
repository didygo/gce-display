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
    double windowSizeWidth = 900;
    double windowSizeHeight = 700;
    double windowSizeWidthFull = 1440;
    double windowSizeHeightFull = 900;
    double windowBorderY = 100;
    double windowBorderX = 200;
    
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
    
    //paramètres pour les maxima et minima de profondeur (relatifs à l'affichage)
    double minDepthIHM = 500;
    double maxDepthIHM = 1000;
    public ParamManager(){
        
    }
}
