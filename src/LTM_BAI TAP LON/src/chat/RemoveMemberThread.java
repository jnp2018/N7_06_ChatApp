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
import model.ChatRoomDetail;

/**
 *
 * @author Admin
 */
public class RemoveMemberThread extends Thread{

    private int port = 1997;
    private String host = "localhost";
    private Socket socket;
    private ChatRoomDetail chatRoomDetail;
    public RemoveMemberThread(ChatRoomDetail chatRoomDetail) {
        this.chatRoomDetail = chatRoomDetail;
    }

    @Override
    public void run() {
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            socket = new Socket(host, port);
            objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            objectOutputStream.writeObject(chatRoomDetail);
            objectOutputStream.flush();

        } catch (IOException ex) {
            Logger.getLogger(SendDataMessageThread.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            try {
                if ( socket != null)
                socket.close();
                if ( objectOutputStream != null)
                    objectOutputStream.close();
                if(objectInputStream != null)
                    objectInputStream.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            
        }
        
    }
    
}
