/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto2;

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
public class KinectServer  {

    private Ivy bus;
    private String busAdress;
    private Controller2 ctrl;

    public KinectServer(Controller2 c, String adresse) {
        this.ctrl = c;
        this.busAdress = adresse;
        this.bus = new Ivy("KinectReceiver", "KinectReceiver READY", null);
        try {
            this.bus.start(busAdress);
        } catch (IvyException ex) {
            Logger.getLogger(KinectServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        input();
        

    }
    
    public void sendToSelf(boolean b){
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
        } catch (IvyException ex) {
            Logger.getLogger(KinectServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void input() {
        try {
            bus.bindMsg("^KINECT_STATE=(.*)", new IvyMessageListener() {
                @Override
                public void receive(IvyClient client, String[] args) {
                    //System.out.println("KINECT_STATE=" + args[0]);
                    if (args[0].equals("LIBRE")) {
                        ctrl.changeState(Controller2.Etats.LIBRE);
                    }
                    if (args[0].equals("DESSIN")) {
                        ctrl.changeState(Controller2.Etats.DESSIN);
                    }
                    if (args[0].equals("REPOS")) {
                        ctrl.changeState(Controller2.Etats.REPOS);
                    }

                }
            });
            
            
        } catch (IvyException ex) {
            Logger.getLogger(KinectServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            bus.bindMsg("^KINECT_POSITION X=(.*) Y=(.*)", new IvyMessageListener() {
                @Override
                public void receive(IvyClient client, String[] args) {
                    //System.out.println("KINECT_POSITION X=" + args[0] + " Y=" + args[1]);
                    try {
                        ctrl.kinectMovePosition((double)Integer.parseInt(args[0]),(double)Integer.parseInt(args[1]));
                    } catch (Exception e) {
                       System.out.println(e.getMessage());
                    }
                   
                }
            });          
        } catch (IvyException ex) {
            Logger.getLogger(KinectServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
                    if (args[0].equals("CLEAN")) {
                       ctrl.clean();
                    }
                    
                }
            });
            
            
        } catch (IvyException ex) {
            Logger.getLogger(KinectServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
       
    }
}
