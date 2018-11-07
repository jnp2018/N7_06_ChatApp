/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homepage;

import chat.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class UpdateTime extends Thread {

    private Date date;
    private HomePageView view;

    public UpdateTime(Date date, HomePageView view) {
        this.date = date;
        this.view = view;
    }

    @Override
    public void run() {
        long msTime = date.getTime();
        while (true) {
            try {
                Thread.sleep(1);
                msTime = msTime + 1;
                Date currentTime = new Date(msTime);
                this.view.setLblTime(currentTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
