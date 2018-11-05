/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import model.MessageBeSaved;
import model.MessageContent;

/**
 *
 * @author Admin
 */
public class ChatRoomControl {
    
    /**
    multicastSend : luồng để gửi tin nhắn multicast
    MulticastReceive : luồng để nhận tin nhắn multicast
    listUser : danh sách user đang online
    * messageBeSaved : nhiệm vụ để lưu danh sách tin nhắn , phục vụ cho :
    *                   1, đọc danh sách tin nhắn lấy từ CSDL để hiển thị lên ChatView
    *                   2, lưu tin nhắn vừa chat để gửi lên server lưu vào CSDL.
    */
    private ChatRoomView view;
    private MulticastSend multicastSend;
    private MulticastReceive multicastReceive;
    private MessageContent messageContentSend;
    private long differenceBetweenEpochs = 2208988800L;
    private ArrayList<String> listUser;
    private MessageBeSaved messageBeSaved;
    public ChatRoomControl(String name, Date date) {
        System.out.println("chat room : " + date.getTime());
        messageBeSaved = new MessageBeSaved();
        listUser  = new ArrayList<>();
        String nickname = name;
        this.view = new ChatRoomView();
        this.view.setVisible(true);
        //set con trỏ chuột tại ô txtField
        this.view.getTxtSendContent().requestFocus(true);
        // set time  
        this.view.setLblTime(date);

        // set nick name for user
        this.view.setContentLblNickName(nickname);
        UpdateTime updateTime = new UpdateTime(date, this.view);
        updateTime.start();
        eventBtnSend();
        eventBtnExit();
        eventBtnDeleteMessage();
        eventTxtSendContent();
        // Lấy ra tin nhắn đã chat từ lần trước kia
        TakeDataMessageThread takeDataMessageThread = new TakeDataMessageThread( messageBeSaved,view);
        takeDataMessageThread.start();
        try {
            takeDataMessageThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        receive();
        /**
        * 1, thiết lập gói tin MessageContent để gửi tới server 
        * 2, ý nghĩa : thông báo với server là user đang online đấy nhé
        * 
         */
        messageContentSend = view.getMessageContent();
        messageContentSend.setTimeMessage(view.getLblTime().getText());
        messageContentSend.setStatusAlive(true);
        //checkThread : luồng để giao tiếp với server
        // cập nhật trạng thái online or offline của user
         CheckThread checkThread = new CheckThread(messageContentSend,view);
         checkThread.start();
        try {
            //  ý nghĩa của join() : trong khi luồng checkThread đang 
            //                          thực hiện thì không có bị thread khác xen vào
            checkThread.join(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //event of btnDeleteMessage
    private void eventBtnDeleteMessage(){
        this.view.addbtnDeleteMessageActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected = JOptionPane.showConfirmDialog(view, "bạn có muốn xóa tin nhắn ?", "thông báo", JOptionPane.OK_CANCEL_OPTION);
                if (selected == JOptionPane.YES_OPTION) {
                    deleteMessage();
                }
            }
        });
    }
    
    private void deleteMessage(){
        DefaultListModel defaultListModel = (DefaultListModel) this.view.getListChat().getModel();
        defaultListModel.removeElementAt(this.view.getListChat().getSelectedIndex());
        this.view.getListChat().setModel(defaultListModel);
    }
    
    // event of btnSend
    private void eventBtnSend() {
        this.view.addbtnSendActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                send();

            }
        });
        
    }
    // event of btnExit
    private void eventBtnExit() {
        this.view.addBtnExitActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected = JOptionPane.showConfirmDialog(view, "bạn có muốn kết thúc phiên nói chuyện ?", "thông báo", JOptionPane.OK_CANCEL_OPTION);
                if (selected == JOptionPane.YES_OPTION) {
                    exit();
                    System.exit(0);
                }
            }
        });
    }
    // event  : press key ENTER for txtSendContent;
    private void eventTxtSendContent() {
        this.view.addTxtSendContentKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {

            }

            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    send();
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {

            }
        });
    }

    // gửi gói tin chứa Object MessageContent đi
    public void send() {
        // thực hiện Thread : gửi tin nhắn đã chat lên server để lưu vào CSDL
        messageBeSaved.getListMessageBeSaved().add(view.getMessageContent());
        SendDataMessageThread sendDataMessageThread = new SendDataMessageThread(messageBeSaved);
        sendDataMessageThread.start();
        try {
            sendDataMessageThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        // 
        messageBeSaved.getListMessageBeSaved().clear();
        messageContentSend = view.getMessageContent();
        messageContentSend.setTimeMessage(view.getLblTime().getText());
        messageContentSend.setStatusAlive(true);
        // khởi tạo Thread Multicast send : gửi tin nhắn lên 
        multicastSend = new MulticastSend(messageContentSend);
        multicastSend.start();
        this.view.getTxtSendContent().setText("");
        this.view.getTxtSendContent().requestFocus(true);
        System.out.println(messageBeSaved.getListMessageBeSaved().size());
       
    }
    
    public void exit() {
        /*
            1, thiết lập gói tin messageCOntent để gửi tới server
            2, ý nghĩa : để thông báo là tôi offline nhé.
        */
        messageContentSend = view.getMessageContent();
        messageContentSend.setTimeMessage("");
        messageContentSend.setStatusAlive(false);
       CheckThread checkThread = new CheckThread(messageContentSend,view);
       checkThread.start();
        try {
            checkThread.join(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // nhận gói tin trả về multicast
    public void receive() {
        /*
        1, multicastReceive là Thread nhận gói tin gửi về.
        2, update txtArea trong ChatRoomView.
         */
        multicastReceive = new MulticastReceive(this.view);
        multicastReceive.start();

    }

}
