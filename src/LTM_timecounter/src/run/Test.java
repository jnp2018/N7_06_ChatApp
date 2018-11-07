/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run;

import control.Hour;
import control.Milisecond;
import control.Minute;
import control.Pause;
import control.Play;
import control.Second;
import control.ShareData;
import java.awt.event.ActionEvent;
import view.View;

/**
 *
 * @author Trần Ngọc Quang
 */
public class Test {

    private ShareData shareData = null;
    private Thread threadSecond = null;
    private Thread threadMinute = null;
    private Thread threadHour = null;
    private Thread threadMilisecond = null;
    private Thread threadPlay = null;
    private Thread threadPause = null;

    public Test() {
        View view = new View();
        shareData = new ShareData(true, false, false, false, view);
        threadMilisecond = new Thread(new Milisecond(shareData));
        threadSecond = new Thread(new Second(shareData));
        threadMinute = new Thread(new Minute(shareData));
        threadHour = new Thread(new Hour(shareData));
        view.getBtnFlag().setEnabled(false);
        view.getBtnReset().setEnabled(false);
        view.getBtnPause().setEnabled(false);
        view.getBtnStart().setEnabled(true);
        view.getBtnPlay().setEnabled(false);

        view.addResetListener((ActionEvent ae) -> {
            try {
                if(!shareData.isBoolh()&& !shareData.isBoolm() && !shareData.isBools() && !shareData.isBoolms()){
                    shareData.setBoolh(false);
                    shareData.setBoolm(false);
                    shareData.setBools(false);
                    shareData.setBoolms(true);
                    
                }
                    
                threadMilisecond.stop();
                threadSecond.stop();
                threadMinute.stop();
                threadHour.stop();
            } catch (Exception e) {
            }
            shareData.setMs(0);
            shareData.setH(0);
            shareData.setM(0);
            shareData.setS(0);
            shareData.setCountShowTextArea(1);
            view.getLblHour().setText(Integer.toString(0));
            view.getLblMinute().setText(Integer.toString(0));
            view.getLblSecond().setText(Integer.toString(0));
            view.getTxtAreaShow().setText("");
            threadMilisecond = new Thread(new Milisecond(shareData));
            threadSecond = new Thread(new Second(shareData));
            threadMinute = new Thread(new Minute(shareData));
            threadHour = new Thread(new Hour(shareData));
            view.getBtnFlag().setEnabled(false);
            view.getBtnReset().setEnabled(false);
            view.getBtnPause().setEnabled(false);
            view.getBtnStart().setEnabled(true);
            view.getBtnPlay().setEnabled(false);
        });
        view.addStartListener((ActionEvent ae) -> {
            view.getBtnFlag().setEnabled(true);
            view.getBtnReset().setEnabled(true);
            view.getBtnPause().setEnabled(true);
            view.getBtnStart().setEnabled(false);
            view.getBtnPlay().setEnabled(false);
            try {
                threadMilisecond.start();
                threadSecond.start();
                threadMinute.start();
                threadHour.start();
            } catch (Exception e) {
                System.out.println(e.getMessage());

            }
        });

        view.addPauseListener((ActionEvent ae) -> {
            threadPause = new Thread(new Pause(shareData));
            threadPause.start();
            view.getBtnFlag().setEnabled(true);
            view.getBtnReset().setEnabled(true);
            view.getBtnPause().setEnabled(false);
            view.getBtnStart().setEnabled(false);
            view.getBtnPlay().setEnabled(true);
        });
        view.addPlayListener((ActionEvent ae) -> {
            threadPlay = new Thread(new Play(shareData));
            threadPlay.start();
            view.getBtnFlag().setEnabled(true);
            view.getBtnReset().setEnabled(true);
            view.getBtnPause().setEnabled(true);
            view.getBtnStart().setEnabled(false);
            view.getBtnPlay().setEnabled(false);
        });
    }

    public static void main(String[] args) {
        Test test = new Test();
    }
}
