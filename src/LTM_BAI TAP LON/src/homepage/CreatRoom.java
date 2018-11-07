/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homepage;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import model.ChatRoom;
import model.ListRoom;
import model.Room;

/**
 *
 * @author Admin
 */
public class CreatRoom extends Thread {

    private int port = 1997;
    private String host = "localhost";
    private Socket socket;
    private ChatRoom chatRoom;
    private HomePageView view;
    public CreatRoom(ChatRoom chatRoom,HomePageView view) {
        this.chatRoom = chatRoom;
        this.view = view;
    }

    @Override
    public void run() {
        ObjectOutputStream objectOutputStream = null;
        DataInputStream dataInputStream = null;
        try {

            socket = new Socket(this.host, this.port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            // gửi dữ liệu đi
            // để thêm mới 1 chatroom vào CSDL
            objectOutputStream.writeObject(chatRoom);
            objectOutputStream.flush();
            dataInputStream = new DataInputStream(this.socket.getInputStream());
            //nhận dữ liệu về
            int check = dataInputStream.readInt();
            if (check == 1) {
                JOptionPane.showMessageDialog(view, "thêm chat room thành công");
            } else if (check == 0) {
                JOptionPane.showMessageDialog(view, "thêm chat room thành công");

            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
