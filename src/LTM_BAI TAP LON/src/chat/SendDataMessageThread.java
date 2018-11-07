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
import model.ListMessage;
import model.MessageBeSaved;

/**
 * SendDataMessageThread : gửi tin nhắn chat lên server để server lưu vào CSDL
 * 
 */
public class SendDataMessageThread extends Thread{

    private int port = 1997;
    private String host = "localhost";
    private ListMessage listMessage;
    private Socket socket;
    public SendDataMessageThread(ListMessage listMessage){
        this.listMessage = listMessage;
    }
    @Override
    public void run() {
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            socket = new Socket(host, port);
            objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            objectOutputStream.writeObject(listMessage);
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            String result = (String) objectInputStream.readObject();
            System.out.println(" sendDataMessageThread"  + result);
        } catch (IOException ex) {
            Logger.getLogger(SendDataMessageThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
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
