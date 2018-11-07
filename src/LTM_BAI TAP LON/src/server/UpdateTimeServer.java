/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.ServerControl;
/**
 *
 * @author Admin
 */
//bỏ qua
public class UpdateTimeServer extends Thread {

    private ServerView view;
    private ServerControl control;
    private Date date;
    private long miliTime;
    public UpdateTimeServer(ServerView view, ServerControl serverControl) {
        this.view = view;
        this.control = serverControl;
        this.miliTime = serverControl.getMiliTime();
    }

    @Override
    public void run() {
        view.updateLblTime(new Date(miliTime));
        while (true) {
            try {
                Thread.sleep(1);
                miliTime = miliTime + 1;
                control.setMiliTime(miliTime);
                view.updateLblTime(new Date(miliTime));
            } catch (InterruptedException ex) {
                Logger.getLogger(UpdateTimeServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
