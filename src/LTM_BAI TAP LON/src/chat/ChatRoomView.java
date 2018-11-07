/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.AncestorListener;
import model.ChatRoom;
import model.ChatRoomDetail;
import model.Message;
import model.MessageContent;
import model.Room;

/**
 *
 * @author Admin
 */
public class ChatRoomView extends javax.swing.JFrame {
   private String nickname;
   private ArrayList<String> listUser ;
   private MessageContent content;
   private Message message;
   private  Room room;
    /**
     * Creates new form ChatRoomView
     */
    
    public ChatRoomView(Room room){
        this.room = room;
        this.nickname = "";
        this.listUser = new ArrayList<>();
        content = new MessageContent();
        message = new Message();
        initComponents();
        
    }
    
   
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTemp = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnExit = new javax.swing.JButton();
        btnSend = new javax.swing.JButton();
        lblTime = new javax.swing.JLabel();
        lblNickName = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtSendContent = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        tableListUserOnline = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        listChat = new javax.swing.JList<>();
        btnDeleteMessage = new javax.swing.JButton();
        btnAddMember = new javax.swing.JButton();
        btnRemoveMember = new javax.swing.JButton();
        lblNameRoom = new javax.swing.JLabel();

        lblTemp.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 204));

        btnExit.setText("exit");

        btnSend.setIcon(new javax.swing.ImageIcon("E:\\LAP_TRINH_MANG\\SOURCE CODE\\LTM_BAI TAP LON\\images\\icon_send_messanger.png")); // NOI18N

        lblTime.setText("jLabel1");

        lblNickName.setBackground(new java.awt.Color(51, 255, 255));
        lblNickName.setText("Name :");

        jLabel2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 51, 51));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("PHÒNG CHAT");

        txtSendContent.setBackground(new java.awt.Color(153, 255, 153));
        txtSendContent.setToolTipText("nhập nội dung muốn gửi");
        txtSendContent.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jScrollPane4.setViewportView(txtSendContent);

        tableListUserOnline.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "DS online"
            }
        ));
        tableListUserOnline.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(tableListUserOnline);

        listChat.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(listChat);

        btnDeleteMessage.setText("xóa tin nhắn");

        btnAddMember.setText("thêm thành viên");
        btnAddMember.setEnabled(false);

        btnRemoveMember.setText("xóa thành viên");
        btnRemoveMember.setEnabled(false);

        lblNameRoom.setText("jLabel1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lblNickName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblNameRoom, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnSend)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnExit, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(btnDeleteMessage)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnRemoveMember, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddMember, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTime, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(lblNickName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblNameRoom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddMember)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoveMember)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addComponent(btnDeleteMessage))
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnSend)
                    .addComponent(btnExit))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    
    // set label nick name
    public void setContentLblNickName(String name){
        this.lblNickName.setText("Nick name : " + name);
        this.nickname = name;
    }
    //set nội dụng txtArea
    
    // event btnSend
    public void addbtnSendActionListener(ActionListener ac){
        btnSend.addActionListener(ac);
        
    }
    public void addTxtSendContentKeyListener(KeyListener ac){
        txtSendContent.addKeyListener(ac);
    }
    // event btnExit
    public void addBtnExitActionListener(ActionListener ac){
        btnExit.addActionListener(ac);
        
    }
    //event btnDeleteMessage
    public void addbtnDeleteMessageActionListener(ActionListener ac){
        btnDeleteMessage.addActionListener(ac);
        
    }
    
    //event btnAddMember
    public void addbtnAddMember(ActionListener ac){
        btnAddMember.addActionListener(ac);
        
    }
    
    //event btnRemoveMember
    public void addbtnRemoveMember(ActionListener ac){
        btnRemoveMember.addActionListener(ac);
        
    }
    
    
    //event tableListUserOnline
    
   // đóng gói Object MessageContent
    public MessageContent getMessageContent(){
        String nickName =this.nickname;
        String message = txtSendContent.getText();
        content.setNickName(nickName);
        content.setMessage(message);
        
        
        return content;
    }
    
    // đóng gói Object Message
    public Message getMessage(){
        String nickName =this.nickname;
        String message = txtSendContent.getText();
        this.message.setNameUser(nickName);
        this.message.setTextMessage(message);
        this.message.setIdChatRoom(room.getId());
        
        return this.message;
    }
    // tra  về đối tượng ChatRoomDetail để tiến hành thêm or xóa thành viên
    public ChatRoomDetail getChatRoomDetail(){
        ChatRoomDetail chatRoomDetail = new ChatRoomDetail();
        chatRoomDetail.setIdRoom(room.getId());
        chatRoomDetail.setNameUser(nickname);
        return chatRoomDetail;
    }
    
    // tra  về đối tượng ChatRoom để tiến hành thêm or xóa thành viên
    
    
    public void setLblTime(Date date ){
        String time =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        lblTime.setText(time);
    }
// trả về Object listChat

    public JList<String> getListChat() {
        return listChat;
    }
    

    public JTextField getTxtSendContent() {
        return txtSendContent;
    }

    public JLabel getLblNickName() {
        return lblNickName;
    }

    public JLabel getLblTime() {
        return lblTime;
    }

    public ArrayList<String> getListUser() {
        return listUser;
    }

    public JTable getTableListUserOnline() {
        return tableListUserOnline;
    }

    public JButton getBtnAddMember() {
        return btnAddMember;
    }

    public JButton getBtnRemoveMember() {
        return btnRemoveMember;
    }

    public JLabel getLblNameRoom() {
        return lblNameRoom;
    }
    
    

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddMember;
    private javax.swing.JButton btnDeleteMessage;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnRemoveMember;
    private javax.swing.JButton btnSend;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblNameRoom;
    private javax.swing.JLabel lblNickName;
    private javax.swing.JLabel lblTemp;
    private javax.swing.JLabel lblTime;
    private javax.swing.JList<String> listChat;
    private javax.swing.JTable tableListUserOnline;
    private javax.swing.JTextField txtSendContent;
    // End of variables declaration//GEN-END:variables
}
