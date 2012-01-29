/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author demalejo
 */
public class KinectServer1 implements KinectServer {

    private Ivy bus;
    private String busAdress;
    private Controller1 ctrl;
    private double windowSizeY, windowSizeX, kinectWindowSizeX, kinectWindowSizeY;

    public KinectServer1(Controller1 c, String adresse, double wX, double wY) {
        this.ctrl = c;
        this.busAdress = adresse;
        this.windowSizeX = wX;
        this.windowSizeY = wY;
        this.kinectWindowSizeX = 640;
        this.kinectWindowSizeY = 480;


        this.bus = new Ivy("KinectServer", "KinectServer READY", null);
        try {
            this.bus.start(busAdress);

        } catch (IvyException ex) {
            Logger.getLogger(KinectServer1.class.getName()).log(Level.SEVERE, null, ex);
        }
        input();



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
            Logger.getLogger(KinectServer1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void send(String st) {
        try {
            bus.sendMsg(st);
            //System.out.println("envoi de :" +st);
        } catch (IvyException ex) {
            Logger.getLogger(KinectServer1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void input() {
        try {
            bus.bindMsg("^KINECT_EVENT=(.*)", new IvyMessageListener() {

                @Override
                public void receive(IvyClient client, String[] args) {
                    //System.out.println("KINECT_EVENT=" + args[0]);
                    if (args[0].equals("KINECT_CONNECTED")) {
                        ctrl.connectionTool.connected();
                    }
                    if (args[0].equals("KINECT_DISCONNECTED")) {
                        ctrl.connectionTool.disconnected();
                    }
                 
                    if (args[0].equals("USER_DETECTED")) {
                        ctrl.manConnectionTool.connected();
                    }
                    if (args[0].equals("USER_LOST")) {
                        ctrl.manConnectionTool.disconnected();
                    }
                    if (args[0].equals("PUSH")) {
                        ctrl.pushHand();
                        
                    }

                }
            });


        } catch (IvyException ex) {
            Logger.getLogger(KinectServer1.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            bus.bindMsg("^KINECT_2HANDS_DISTANCE=(.*)", new IvyMessageListener() {

                @Override
                public void receive(IvyClient client, String[] args) {
                    System.out.println("KINECT_2HANDS_POSITION=" + args[0]);

                    ctrl.eventKinect2Hands(Double.parseDouble(args[0]));

                }
            });


        } catch (IvyException ex) {
            Logger.getLogger(KinectServer1.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            bus.bindMsg("^KINECT_HAND_OPENED=(.*)", new IvyMessageListener() {

                @Override
                public void receive(IvyClient client, String[] args) {
                    //System.out.println("KINECT_HAND_OPENED=" + args[0]);
                    if (args[0].equals("true")) {
                        ctrl.handUnSelect();
                    } else {
                        ctrl.handSelect();
                    }

                }
            });


        } catch (IvyException ex) {
            Logger.getLogger(KinectServer1.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            bus.bindMsg("^KINECT_POSITION X=(.*) Y=(.*)", new IvyMessageListener() {

                @Override
                public void receive(IvyClient client, String[] args) {
                    //System.out.println("KINECT_POSITION X=" + args[0] + " Y=" + args[1]);

                    ctrl.eventKinectMove(((double) Integer.parseInt(args[0])) * windowSizeX / kinectWindowSizeX, ((double) Integer.parseInt(args[1])) * windowSizeY / kinectWindowSizeY);
                }
            });
        } catch (IvyException ex) {
            Logger.getLogger(KinectServer1.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            bus.bindMsg("^KINECT_HAND_DEPTH=(.*)", new IvyMessageListener() {

                @Override
                public void receive(IvyClient client, String[] args) {
                    System.out.println("KINECT_HAND_DEPTH=" + args[0]);

                    ctrl.eventHandDepth(Double.parseDouble(args[0]));
                }
            });


        } catch (IvyException ex) {
            Logger.getLogger(KinectServer1.class.getName()).log(Level.SEVERE, null, ex);
        }



    }
}
