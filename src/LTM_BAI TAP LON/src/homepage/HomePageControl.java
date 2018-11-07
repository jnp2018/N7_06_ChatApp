/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homepage;

import chat.ChatRoomControl;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import model.ChatRoom;
import model.ListRoom;
import model.MessageContent;
import model.Room;

/**
 *
 * @author Admin
 */
public class HomePageControl {

    private String name;

    private HomePageView view;
    private FormCreatRoom formCreatRoom = new FormCreatRoom();
    private MessageContent messageContentSend;
    private ListRoom listRoom;

    public ListRoom getListRoom() {
        return listRoom;
    }

    public void setListRoom(ListRoom listRoom) {
        this.listRoom = listRoom;
    }

    public HomePageControl(String name, Date date) {
        listRoom = new ListRoom();
        this.name = name;
        view = new HomePageView();

        view.setVisible(true);
        view.setLblName(name);
        view.setLblTime(date);
        // updateTime : cập nhạt thời gian

        UpdateTime updateTime = new UpdateTime(date, this.view);
        updateTime.start();
        messageContentSend = new MessageContent();
        messageContentSend.setNickName(name);
        messageContentSend.setMessage("");
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
        TakeListChatRoom takeListChatRoom = new TakeListChatRoom(this.view, this);
        takeListChatRoom.start();
        try {
            takeListChatRoom.join(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(HomePageControl.class.getName()).log(Level.SEVERE, null, ex);
        }

        ///////////////////
        eventbtnExit();
        eventbtnCreatRoom();
        ///////////////////
        eventbtnExitOfFormCreatRoom();
        eventbtnCreateRoomOfFormCreatRoom();
        ///////////////////
        eventbtnJoinGroup();
    }

    private void eventbtnJoinGroup() {
        view.addbtnJoinGroup(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int idRoom = 0;
                String hostCodeRoom = "";
                String nameRoom = "";
                if (view.getListRoom().getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(view, "bạn phải chọn 1 phòng chat trước !");
                } else {
                    nameRoom = view.getListRoom().getSelectedValue();
                    for (Room room : listRoom.getListRoom()) {
                        if (room.getRoomname().equals(nameRoom)) {
                            hostCodeRoom = room.getHostname();
                            idRoom = room.getId();
                            ChatRoomControl chatRoomControl = new ChatRoomControl(name, new Date(), room);
                            break;
                        }
                    }
                    System.out.println("Id room : " + idRoom);

                }
            }
        });

    }

    // sự kiện cho button createRoom của homepageview
    private void eventbtnCreatRoom() {
        this.view.addbtnCreatRoom(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                formCreatRoom.getLblNameCreateRoom().setText(name);
                formCreatRoom.setVisible(true);
            }
        });

    }

    // sự kiện cho button exit của FormCreatRoom
    private void eventbtnExitOfFormCreatRoom() {
        formCreatRoom.addbtnExit(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected = JOptionPane.showConfirmDialog(formCreatRoom, "bạn có muốn thoat ?", "thông báo", JOptionPane.OK_CANCEL_OPTION);
                if (selected == JOptionPane.YES_OPTION) {
                    formCreatRoom.setVisible(false);

                }
            }
        });
    }

    // sự kiện cho button CreateRoom của FormCreatRoom
    private void eventbtnCreateRoomOfFormCreatRoom() {
        formCreatRoom.addbtnCreateRoom(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addCreateRoom();

            }
        });
    }

    private void addCreateRoom() {
        ChatRoom chatRoom = formCreatRoom.getChatRoom();
        CreatRoom creatRoom = new CreatRoom(chatRoom, view);
        creatRoom.start();
        try {
            creatRoom.join(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(HomePageControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        formCreatRoom.dispose();
        TakeListChatRoom takeListChatRoom = new TakeListChatRoom(this.view, this);
        takeListChatRoom.start();
    }

    // sự kiện cho button exit của homepageview
    private void eventbtnExit() {
        this.view.addbtnExit(new ActionListener() {
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

    private void exit() {
        /*
            1, thiết lập gói tin messageCOntent để gửi tới server
            2, ý nghĩa : để thông báo là tôi offline nhé.
         */
        messageContentSend = new MessageContent();
        messageContentSend.setNickName(name);
        messageContentSend.setMessage("");
        messageContentSend.setTimeMessage(view.getLblTime().getText());
        messageContentSend.setStatusAlive(false);
        CheckThread checkThread = new CheckThread(messageContentSend, view);
        checkThread.start();
        try {
            checkThread.join(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
