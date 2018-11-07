package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import view.View;

/**
 * biến s : giây; biến m : phút; biến h : giờ; biến bools : kiểm tra luồng giây
 * có được phép thực hiện; biến boolm : kiểm tra luồng phút có được phép thực
 * hiện; biến boolh : kiểm tra luồng giờ có được phép thực hiện;
 *
 */
/**
 * khởi tạo lúc đầu : s = 0; m = 0 ; h = 0; bools = true; luồng giây được phép
 * chạy boolm = false; luồng phút không được phép chạy boolh = false; luồng giờ
 * không được phép chạy
 *
 */
public class ShareData {

    private int countShowTextArea = 1;
    private View view = null;
    private int ms = 0;
    private int s = 0;
    private int m = 0;
    private int h = 0;
    private boolean bools, boolm, boolh, boolms;

    public ShareData(boolean ms, boolean s, boolean m, boolean h, View view) {
        this.view = view;
        view.setVisible(true);
        view.getLblHour().setText(Integer.toString(this.h));
        view.getLblMinute().setText(Integer.toString(this.m));
        view.getLblSecond().setText(Integer.toString(this.s));
        this.bools = s;
        this.boolm = m;
        this.boolh = h;
        this.boolms = ms;
        flag();
    }

    public int getCountShowTextArea() {
        return countShowTextArea;
    }

    public void setCountShowTextArea(int countShowTextArea) {
        this.countShowTextArea = countShowTextArea;
    }

    public boolean isBoolms() {
        return boolms;
    }

    public void setBoolms(boolean boolms) {
        this.boolms = boolms;
    }

    
    public boolean isBools() {
        return bools;
    }

    public void setBools(boolean bools) {
        this.bools = bools;
    }

    public boolean isBoolm() {
        return boolm;
    }

    public void setBoolm(boolean boolm) {
        this.boolm = boolm;
    }

    public boolean isBoolh() {
        return boolh;
    }

    public void setBoolh(boolean boolh) {
        this.boolh = boolh;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getMs() {
        return ms;
    }

    public void setMs(int ms) {
        this.ms = ms;
    }

    private class FlagListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            String time = "";
            time += view.getLblHour().getText();
            time += " : ";
            time += view.getLblMinute().getText();
            time += " : ";
            time += view.getLblSecond().getText();
            view.getTxtAreaShow().append(countShowTextArea + " : " + time + "\n");
            countShowTextArea++;
        }

    }

    public synchronized void flag() {
        view.addFlagListener(new FlagListener());
    }

    public synchronized void pause() {
        boolms = false;
        bools = false;
        boolm = false;
        boolh = false;
        notifyAll();
    }

    public synchronized void play() {
        boolms = true;
        bools = false;
        boolm = false;
        boolh = false;
        notifyAll();
    }

    public synchronized void milisecond() {
        while (true) {
            if (!boolms) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ShareData.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    Thread.sleep(1000);
                    ms = ms + 1;
                    if (ms < 1) {
                        boolms = true;
                        bools = false;
                        boolm = false;
                        boolh = false;
                    } else {
                        ms = 0;
                        boolms = false;
                        bools = true;
                        boolm = false;
                        boolh = false;
                    }
                    notifyAll();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ShareData.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    // phương thức để chạy luồng giây
    public synchronized void second() {
        while (true) {
            // nếu bools = false : luồng giây không được phép chạy,

            if (!bools) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ShareData.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {

                s = s + 1;
                if (s <= 59) {
                    boolms = true;
                    bools = false;
                    boolm = false;
                    boolh = false;

                } else {
                    s = 0;
                    boolms = false;
                    bools = false;
                    boolm = true;
                    boolh = false;
                }

                view.getLblSecond().setText(Integer.toString(s));
                notifyAll();

            }

        }
    }
// phương thức để chạy luồng phút

    public synchronized void minute() {
        while (true) {
            // nếu boolm = false : luồng phút không được phép chạy
            if (!boolm) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ShareData.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                m = m + 1;
                if (m <= 59) {
                    boolms = true;
                    bools = false;
                    boolm = false;
                    boolh = false;
                } else {
                    m = 0;
                    boolms = false;
                    bools = false;
                    boolm = false;
                    boolh = true;
                }
                view.getLblMinute().setText(Integer.toString(m));
                notifyAll();
            }

        }
    }
//phương thức để chạy luồng giờ

    public synchronized void hour() {
        while (true) {
            // boolh = true : luồng giờ không được phép chạy
            if (!boolh) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ShareData.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                h = h + 1;
                if (h == 24) {
                    h = 0;
                    m = 0;
                    s = 0;
                    ms = 0;
                }
                boolms = true;
                bools = false;
                boolm = false;
                boolh = false;
                view.getLblHour().setText(Integer.toString(h));
                notifyAll();
            }

        }
    }
}
