/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homepage;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import model.ListRoom;
import model.Room;

/**
 *
 * Thread TakeListChatRoom : luồng để lấy ra danh sách chat room từ Server
 */
public class TakeListChatRoom extends Thread {

    private int port = 1997;
    private String host = "localhost";
    private Socket socket;
    private HomePageView view;
    private ListRoom listRoom;
    private HomePageControl control;
    public TakeListChatRoom(HomePageView view,HomePageControl control) {
        this.view = view;
        this.listRoom = new ListRoom();
        this.control = control;
    }

    @Override
    public void run() {
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try {

            socket = new Socket(this.host, this.port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            // gửi dữ liệu đi
            // nếu là Object String "listroom" thì lấy chatRoom
            objectOutputStream.writeObject("listroom");
            objectOutputStream.flush();
            // nhận dữ liệu về
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            listRoom = (ListRoom) objectInputStream.readObject();
            DefaultListModel<String> defaultListModel = new DefaultListModel<>();
            for (Room room : listRoom.getListRoom()) {
                String str = room.toString().trim();
                defaultListModel.addElement(str);
            }
            view.getListRoom().setModel(defaultListModel);
            control.setListRoom(listRoom);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
