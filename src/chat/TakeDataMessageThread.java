/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

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
import model.MessageBeSaved;
import model.MessageContent;

/**
 * TakeDataMessageThread : lấy ra tin nhắn đã chat từ CSDL để có thể hiển thị lên chatView
 *
 */
public class TakeDataMessageThread extends Thread {

    private int port = 1997;
    private String host = "localhost";
    private MessageBeSaved messageBeSaved;
    private Socket socket;
    private ChatRoomView view;

    public TakeDataMessageThread(MessageBeSaved messageBeSaved, ChatRoomView view) {
        this.messageBeSaved = messageBeSaved;
        this.view = view;

    }

    @Override
    public void run() {
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;

        try {

            socket = new Socket(this.host, this.port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            // gửi dữ liệu đi
            // nếu là Object String "123456789" thì để cập nhật danh sách tin nhắn đã chat
            objectOutputStream.writeObject("123456789");
            objectOutputStream.flush();
            // nhận dữ liệu về
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            messageBeSaved = (MessageBeSaved) objectInputStream.readObject();
            DefaultListModel defaultListModel = new DefaultListModel();

            for (MessageContent messageContent : messageBeSaved.getListMessageBeSaved()) {
                String message = "";
                if (this.view.getLblNickName().getText().contains(messageContent.getNickName())) {
                    message = "tôi : " + messageContent.getMessage() + "\n";
                    this.view.getListChat().setForeground(Color.BLACK);
                    this.view.getListChat().setModel(defaultListModel);

                    defaultListModel.addElement(message);
                    //this.view.getTxtAreaAllContent().append("Tôi : " + messageContent.getMessage() + "\n");

                } else {
                    this.view.getListChat().setForeground(Color.BLUE);
                    message = "\t \t" + messageContent.getNickName() + " : " + messageContent.getMessage() + "\n";
                    this.view.getListChat().setModel(defaultListModel);

                    defaultListModel.addElement(message);
                    
                    //this.view.getTxtAreaAllContent().append("\t \t" + messageContent.getNickName() + " : " + messageContent.getMessage() +"\n");
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(TakeDataMessageThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TakeDataMessageThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
