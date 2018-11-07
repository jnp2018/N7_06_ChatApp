/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Room;

/**
 *
 * @author Admin
 */
public class isUserCreatChatRoom extends Thread {

    private int port = 1997;
    private String host = "localhost";
    private Socket socket;
    private ChatRoomView view;
    private Room room;

    public isUserCreatChatRoom(ChatRoomView view, Room room) {
        this.view = view;
        this.room = room;
    }

    @Override
    public void run() {
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            socket = new Socket(host, port);
            objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            objectOutputStream.writeObject(room);
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            boolean receive = (boolean) objectInputStream.readObject();
            if(receive){
                view.getBtnAddMember().setEnabled(true);
                view.getBtnRemoveMember().setEnabled(true);
            }

        } catch (IOException ex) {
            Logger.getLogger(SendDataMessageThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(isUserCreatChatRoom.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

}