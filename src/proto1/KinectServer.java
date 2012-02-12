package proto1;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author demalejo
 */
public class KinectServer  {

    private Ivy bus;
    private String busAdress;
    private Controller ctrl;
    private double windowHeight, windowWidth, kinectWindowSizeX, kinectWindowSizeY;
    private Date dateTemp;
    private ParamManager param;
    

    public KinectServer(Controller c, String adresse, double wX, double wY, ParamManager param) {
        this.param = param;
        this.ctrl = c;
        this.busAdress = adresse;
        this.windowWidth = wX;
        this.windowHeight = wY;
        this.kinectWindowSizeX = param.kinectWindowWidth;
        this.kinectWindowSizeY = param.kinectWindowHeight;
        dateTemp = new Date();



        this.bus = new Ivy("KinectServer", "KinectServer READY", null);
        try {
            this.bus.start(busAdress);

        } catch (IvyException ex) {
            Logger.getLogger(KinectServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        input();



    }
    
    public void changeWindowSize(double x, double y){
        windowWidth = x;
        windowHeight = y;
    }
    
    public void disconnect(){
        bus.stop();
    }

    public double ips() {
        
        double temp = (new Date().getTime() - dateTemp.getTime());
        dateTemp = new Date();
        return temp;
    }

    public void sendToSelf(boolean b) {
        this.bus.sendToSelf(b);
    }

    public void changeBusAdress(String st) {
        bus.stop();
        busAdress = st;
        try {
            bus.start(busAdress);
            input();
        } catch (IvyException ex) {
            Logger.getLogger(KinectServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void send(String st) {
        try {
            bus.sendMsg(st);
            //System.out.println("envoi de :" +st);
        } catch (IvyException ex) {
            Logger.getLogger(KinectServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void input() {
        try {
            bus.bindMsg("^KINECT_EVENT=(.*)", new IvyMessageListener() {

                @Override
                public void receive(IvyClient client, String[] args) {
                    //System.out.println("KINECT_EVENT=" + args[0]);
                    if (args[0].equals("KINECT_CONNECTED")) {
                        ctrl.kinectconnection(true);
                    }
                    if (args[0].equals("KINECT_DISCONNECTED")) {
                        ctrl.kinectconnection(false);
                    }

                    if (args[0].equals("USER_DETECTED")) {
                        ctrl.userDetected();
                    }
                    if (args[0].equals("USER_LOST")) {
                        ctrl.userLost();
                    }
                    if (args[0].equals("PUSH")) {
                        ctrl.pushHand();

                    }

                }
            });


        } catch (IvyException ex) {
            Logger.getLogger(KinectServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            bus.bindMsg("^KINECT_2HANDS_DISTANCE=(.*)", new IvyMessageListener() {

                @Override
                public void receive(IvyClient client, String[] args) {
                    //System.out.println("KINECT_2HANDS_POSITION=" + args[0]);

                    

                }
            });


        } catch (IvyException ex) {
            Logger.getLogger(KinectServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            bus.bindMsg("^KINECT_HAND_OPENED=(.*)", new IvyMessageListener() {

                @Override
                public void receive(IvyClient client, String[] args) {
                    //System.out.println("KINECT_HAND_OPENED=" + args[0]);
                    if (args[0].equals("true")) {
                        ctrl.handOpened();
                    } else {
                        ctrl.handClosed();
                    }

                }
            });


        } catch (IvyException ex) {
            Logger.getLogger(KinectServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            bus.bindMsg("^KINECT_POSITION X=(.*) Y=(.*) Z=(.*)", new IvyMessageListener() {

                @Override
                public void receive(IvyClient client, String[] args) {
                    //System.out.println("KINECT_POSITION X=" + args[0] + " Y=" + args[1]+ " Z=" + args[2]);

                    double x = (double) Integer.parseInt(args[0]);
                    double y = (double) Integer.parseInt(args[1]);
                    double z = (double) Integer.parseInt(args[2]);
                    
                    
                    
                    x = (x*windowWidth)/kinectWindowSizeX +(2*x/kinectWindowSizeX - 1)*param.windowBorderX;
                    y = (y*windowHeight)/kinectWindowSizeY + (2*y/kinectWindowSizeY - 1)*param.windowBorderY;
                    
                    
                    
                    if (x<50) x = 50;
                    if (x>windowWidth-50) x = windowWidth-50;
                    if (y<50) y =50;
                    if (y>windowHeight-50) y = windowHeight-50;
                    
                    
                    //System.out.println("x=" + x + "  y="+y);
                    ctrl.handMove(x,y,z);
                }
            });
        } catch (IvyException ex) {
            Logger.getLogger(KinectServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            bus.bindMsg("^KINECT_FINGER_ANGLE=(.*)", new IvyMessageListener() {

                @Override
                public void receive(IvyClient client, String[] args) {
                    //System.out.println("KINECT_FINGER_ANGLE=" + args[0]);
                    String s = "";
                    for (int i = 0; i < args[0].length(); i++) {
                        if (args[0].charAt(i) == ',') {
                            s = s + '.';
                        } else {
                            s = s + args[0].charAt(i);
                        }
                    }
                    //System.out.println(s);

                    
                    ctrl.fingerAngle(Math.toRadians(Double.parseDouble(s)));
                   
                }
            });


        } catch (IvyException ex) {
            Logger.getLogger(KinectServer.class.getName()).log(Level.SEVERE, null, ex);
        }



    }
}
