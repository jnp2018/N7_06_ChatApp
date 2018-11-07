/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import chat.*;
import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import model.ListMessage;
import model.Message;
import model.MessageBeSaved;
import model.MessageContent;

/**
 * TakeDataMessageThread : lấy ra tin nhắn đã chat từ CSDL để có thể hiển thị
 * lên chatView
 *
 */
public class TakeDataMessageThread extends Thread {

    private int port = 1997;
    private String host = "localhost";
    private ListMessage listMessage;
    private Socket socket;
    private ChatRoomView view;
    private int idRoom;

    public TakeDataMessageThread(ListMessage listMessage, ChatRoomView view, int idRoom) {
        this.listMessage = listMessage;
        this.view = view;
        this.idRoom = idRoom;
    }

    @Override
    public void run() {
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        System.out.println("idRoom : " + idRoom);
        try {

            socket = new Socket(this.host, this.port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            // gửi dữ liệu đi
            // 
            objectOutputStream.writeObject(idRoom);
            objectOutputStream.flush();
            // nhận dữ liệu về
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            listMessage = (ListMessage) objectInputStream.readObject();
            DefaultListModel defaultListModel = new DefaultListModel();

            for (Message m : listMessage.getListMessage()) {
                String message = "";
                if (this.view.getLblNickName().getText().contains(m.getNameUser())) {
                    message = "tôi : " + m.getTextMessage() + "\n";
                    this.view.getListChat().setForeground(Color.BLACK);
                    defaultListModel.addElement(message);

                } else {
                    this.view.getListChat().setForeground(Color.BLUE);
                    message = "                                    \t \t" + m.getNameUser() + " : " + m.getTextMessage() + "\n";
                    defaultListModel.addElement(message);

                }
            }
            this.view.getListChat().setModel(defaultListModel);

        } catch (IOException ex) {
            Logger.getLogger(TakeDataMessageThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TakeDataMessageThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
