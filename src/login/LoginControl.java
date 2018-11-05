/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import chat.ChatRoomControl;
import chat.ChatRoomView;
import model.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class LoginControl {

    /*
    differenceBetweenEpochs : biến dùng cho đồng bộ thời gian server
     */
    private long beginTime;
    private long exitTime;
    private LoginView clientView;
    private int port = 1997;
    private String host = "localhost";
    private Socket clientSocket;

    public LoginControl(LoginView view) {
        this.clientView = view;
        this.clientView.setVisible(true);
        // thêm sự kiện cho button Login
        clientView.addLoginListener(new LoginListener());

    }

    class LoginListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            User user = clientView.getUser();
            ObjectOutputStream objectOutputStream = null;
            DataInputStream dataInputStream = null;
            try {
                clientSocket = new Socket(host, port);
                objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                dataInputStream = new DataInputStream(clientSocket.getInputStream());
                beginTime = System.currentTimeMillis();
                System.out.println(beginTime);
                // gửi dữ liệu Object User đến server
                objectOutputStream.writeObject(user);
                // nhận dữ liệu gửi từ server về
                Date date = SyncTime(dataInputStream);
                // Date date = new Date();
                String SyncTime = date.toString();

                if (SyncTime instanceof String) {
                    String receive = (String) SyncTime;
                    System.out.println(receive);
                    if (receive.contains("Mon Jan 01 06:59:59 ICT 1900")) {
                        clientView.showMessenge("tài khoản đã tồn tại");
                    } else if (receive.contains("Sun Jul 15 05:18:53 ICT 1900")) {
                        clientView.showMessenge("tài khoản hoặc mật khẩu không đúng");
                    } else {
                        // chuyển sang màn hình chatRoom
                        clientView.dispose();
                        new ChatRoomControl(user.getUsername(), date);

                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(LoginControl.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(clientView, "máy chủ chưa được bật, hoặc lỗi đường truyền", "lỗi", JOptionPane.OK_OPTION);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(clientView, " máy chủ đang gặp sự cố", "lỗi", JOptionPane.OK_OPTION);
            } finally {

                try {
                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                    if (objectOutputStream != null) {
                        objectOutputStream.close();
                    }

                } catch (IOException ex) {
                    Logger.getLogger(LoginControl.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }

    }

    // đọc gói tin nhân từ server để trả về thời gian
    private Date SyncTime(DataInputStream dataInputStream) {
        try {
            long timeNow = dataInputStream.readLong();
            System.out.println("time đồng bộ nhận từ server : " + timeNow);
            return new Date(timeNow);
        } catch (Exception ex) {
            Logger.getLogger(ChatRoomView.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            
        }
        return null;
    }
}
