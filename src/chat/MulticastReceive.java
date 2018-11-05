/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import model.MessageContent;

/**
 *
 * luồng để nhận gói tin multicast đọc bài giảng lâp trình mạng để rõ hơn
 */
public class MulticastReceive extends Thread {

    private ChatRoomView view;
    private String message = "";
    private String host = "234.5.6.8";
    private int port = 6666;
    private MulticastSocket socket;
    private byte[] receiveData = new byte[4096];
    private MessageContent messageContent;

    public MulticastReceive(ChatRoomView v) {
        view = v;
        try {

            socket = new MulticastSocket(this.port);
            InetAddress address = InetAddress.getByName(this.host);
            socket.joinGroup(address);
        } catch (IOException ex) {
            Logger.getLogger(MulticastReceive.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {
        DefaultListModel defaultListModel = new DefaultListModel();

        while (true) {

            DatagramPacket datagramPacketReceive = new DatagramPacket(receiveData, receiveData.length);
            try {
                socket.receive(datagramPacketReceive);
                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(receiveData);
                ObjectInputStream objectInputStream = new ObjectInputStream(arrayInputStream);
                messageContent = (MessageContent) objectInputStream.readObject();
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

            } catch (IOException ex) {
                Logger.getLogger(MulticastReceive.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MulticastReceive.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public MessageContent getContent() {
        return messageContent;
    }

}
