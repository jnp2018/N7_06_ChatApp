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
import model.ChatRoomDetail;
import model.ListMessage;
import model.MessageBeSaved;
import model.MessageContent;
import model.Room;

/**
 *
 * @author Admin
 */
public class ChatRoomControl {

    /**
     * multicastSend : luồng để gửi tin nhắn multicast MulticastReceive : luồng
     * để nhận tin nhắn multicast listUser : danh sách user đang online
     * messageBeSaved : nhiệm vụ để lưu danh sách tin nhắn , phục vụ cho : 1,
     * đọc danh sách tin nhắn lấy từ CSDL để hiển thị lên ChatView 2, lưu tin
     * nhắn vừa chat để gửi lên server lưu vào CSDL.
     */
    private int indexTableSelect = 0;
    private ChatRoomView view;
    private MulticastSend multicastSend;
    private MulticastReceive multicastReceive;
    private MessageContent messageContentSend;
    private ListMessage listMessage;
    private String host;
    private int idRoom;
    private Room room;
    private String nickname;

    public ChatRoomControl(String name, Date date, Room room) {
        System.out.println("chat room : " + date.getTime());
        listMessage = new ListMessage();
        nickname = name;
        this.room = room;
        this.host = room.getHostname();
        this.idRoom = room.getId();
        this.view = new ChatRoomView(this.room);
        this.view.getLblNameRoom().setText(" tên phòng chat : " + room.getRoomname());
        this.view.setVisible(true);
        //set con trỏ chuột tại ô txtField
        this.view.getTxtSendContent().requestFocus(true);
        // set time  
        this.view.setLblTime(date);

        // set nick name for user
        this.view.setContentLblNickName(nickname);
        // updateTime : cập nhạt thời gian
        UpdateTime updateTime = new UpdateTime(date, this.view);
        updateTime.start();
        eventBtnSend();
        eventBtnExit();
        eventTxtSendContent();
        eventBtnDeleteMessage();
        eventBtnAddMember();
        eventBtnRemoveMember();
        this.room.setUserName(nickname);
        isUserCreatChatRoom uccr = new isUserCreatChatRoom(this.view,this.room);
        uccr.start();
        try {
            uccr.join(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Lấy ra tin nhắn đã chat từ lần trước kia
        TakeDataMessageThread takeDataMessageThread = new TakeDataMessageThread(listMessage, view, idRoom);
        takeDataMessageThread.start();
        try {
            takeDataMessageThread.join(100);
        } catch (Exception ex) {
            Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        // luồng nhận tin nhắn chat trong group
        receive();
        /**
         * 1, thiết lập gói tin MessageContent để gửi tới server 2, ý nghĩa :
         * thông báo với server là user đang online đấy nhé
         *
         */
        messageContentSend = view.getMessageContent();
        messageContentSend.setTimeMessage(view.getLblTime().getText());
        messageContentSend.setStatusAlive(true);
        //checkThread : luồng để giao tiếp với server
        // cập nhật trạng thái online or offline của user
        CheckThread checkThread = new CheckThread(messageContentSend, view);
        checkThread.start();
        try {
            //  ý nghĩa của join() : trong khi luồng checkThread đang 
            //                          thực hiện thì không có bị thread khác xen vào
            checkThread.join(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    

    private void eventBtnRemoveMember() {
        view.addbtnRemoveMember(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (view.getTableListUserOnline().getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(view, "ban phải chọn thành viên trước khi xoa");
                } else {
                    ChatRoomDetail c = view.getChatRoomDetail();
                    c.setStatus(false);
                    RemoveMemberThread removeMemberThread = new RemoveMemberThread(c);
                    removeMemberThread.start();
                    try {
                        removeMemberThread.join(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    private void eventBtnAddMember() {
        view.addbtnAddMember(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (view.getTableListUserOnline().getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(view, "ban phải chọn thành viên trước khi thêm");
                } else {
                    ChatRoomDetail c = view.getChatRoomDetail();
                    c.setStatus(true);
                    AddMemberThread addMemberThread = new AddMemberThread(c);
                    addMemberThread.start();
                    try {
                        addMemberThread.join(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    //event of btnDeleteMessage
    private void eventBtnDeleteMessage() {
        this.view.addbtnDeleteMessageActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (view.getListChat().getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(view, "bạn phải chọn tin nhắn trước khi xóa !");
                } else {
                    int selected = JOptionPane.showConfirmDialog(view, "bạn có muốn xóa tin nhắn ?", "thông báo", JOptionPane.OK_CANCEL_OPTION);
                    if (selected == JOptionPane.YES_OPTION) {

                        if (view.getListChat().getSelectedValue().contains("tôi :")) {
                            deleteMessage();

                        } else {
                            JOptionPane.showMessageDialog(view, "bạn chỉ được xóa tin nhắn của bạn !");
                        }
                    }

                }

            }
        });
    }

    private void deleteMessage() {

        String[] str = this.view.getListChat().getSelectedValue().split(":");

        String content = str[1];
        content = content.split("\n")[0];
        content = content + ":" + nickname + ":" + Integer.toString(room.getId());

//        content = content + ":";
//        content = content + nickname;
//        content = content + ":";
//        content = content + Integer.toString(room.getId());
        System.out.println("tin nhan : " + content);
        DeleteDataMessageThread deleteDataMessageThread = new DeleteDataMessageThread(content);
        deleteDataMessageThread.start();
        try {
            deleteDataMessageThread.join(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
        }
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

                    view.setVisible(false);
                    
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
        //thực hiện Thread : gửi tin nhắn đã chat lên server để lưu vào CSDL
        listMessage.getListMessage().add(view.getMessage());
        SendDataMessageThread sendDataMessageThread = new SendDataMessageThread(listMessage);
        sendDataMessageThread.start();
        try {
            sendDataMessageThread.join(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        // 
        listMessage.getListMessage().clear();
        messageContentSend = view.getMessageContent();
        messageContentSend.setTimeMessage(view.getLblTime().getText());
        messageContentSend.setStatusAlive(true);
        // khởi tạo Thread Multicast send : gửi tin nhắn lên group chat
        multicastSend = new MulticastSend(messageContentSend, host);
        multicastSend.start();
        this.view.getTxtSendContent().setText("");
        this.view.getTxtSendContent().requestFocus(true);

    }

    public void exit() {
        /*
            1, thiết lập gói tin messageCOntent để gửi tới server
            2, ý nghĩa : để thông báo là tôi offline nhé.
         */
        messageContentSend = view.getMessageContent();
        messageContentSend.setTimeMessage("");
        messageContentSend.setStatusAlive(false);
        CheckThread checkThread = new CheckThread(messageContentSend, view);
        checkThread.start();
        try {
            checkThread.join(100);
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
        multicastReceive = new MulticastReceive(this.view, host);
        multicastReceive.start();

    }

}
