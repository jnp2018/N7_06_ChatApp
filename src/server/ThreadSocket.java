/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import chat.CheckThread;
import dao.JDBCConnection;
import java.net.Socket;
import java.io.*;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import login.LoginView;
import model.MessageBeSaved;
import model.MessageContent;
import model.User;

/**
 *
 * @author Admin
 */
public class ThreadSocket extends Thread {

    /*
    listUser : danh sách user đang online
     */
    private long beginTime;
    private long exitTime;
    private Socket socket;
    private ServerView view;
    private ServerControl control;
    private ArrayList<String> listUser;
    private MessageContent messageContent;
    private MessageBeSaved listMessageBeSaved;

    public ThreadSocket(Socket s, ServerView view, ArrayList<String> listUser, MessageBeSaved listMessageContent,ServerControl serverControl) {
        this.listMessageBeSaved = listMessageContent;
        this.socket = s;
        this.view = view;
        this.listUser = listUser;
        this.control = serverControl;
    }

    public void run() {
        try {
            // tạo luồng để đọc dữ liệu từ client tới server
            // và trả về dữ liệu từ server về client
            DataOutputStream dataOutputStream = null;
            ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            ObjectOutputStream objectOutputStream = null;
            try {
                Object obj = objectInputStream.readObject();
                /*
                * khi giao diện Login thì obj là User
                * khi giao diện chatRoom thì obj là messageContent
                 */

                // 
                if (obj instanceof User) {
                    /*
                    bước 
                    1: client đóng gói object User và gửi cho server
                    2: server kiểm tra:
                        1: nếu user có trong CSDL và chưa đăng nhập thì trả về thời gian chính xác của server
                        2: nếu user có trong CSDL và đã đăng nhập thì trả về thời gian = Mon Jan 01 06:59:59 ICT 1900
                        3: nếu user không có trong CSDL thì trả về thời gian = Sun Jul 15 05:18:53 ICT 1900
                     */

                    dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
                    User user = (User) obj;
                    //  nếu user có trong CSDL và user chưa login
                    if (checkUser(user) && !checkUserLogged(user.getUsername())) {
                        // gửi về cho client : byte[] để lưu thời gian
                        dataOutputStream.writeLong(SyncTime());
                        dataOutputStream.flush();

                        // nếu user không có trong CSDL    
                    } else if (!checkUser(user)) {
                        // trả về byte[] fault, mà khi chuyển sang Date thì mặc định là : Sun Jul 15 05:18:53 ICT 1900
                        byte[] fault = new byte[4];
                        fault[0] = (byte) 1;
                        fault[1] = (byte) 1;
                        fault[2] = (byte) 1;
                        fault[3] = (byte) 1;
                        //outputStream.write(fault);
                    } // nếu đăng nhập vào tài khoản đang online
                    else {

                        // trả về null, mà khi chuyển sang Date thì mặc định là : Mon Jan 01 06:59:59 ICT 1900
                        //outputStream.write(null);
                    }
                }
                // 
                /*
                 để cập nhật danh sách user online or offline
                1: client cứ sau 1000ms sẽ gửi cho server gói tin : MessageContent
                2: server đọc và lấy thông tin nickName và statusAlive để kiểm tra
                // statusAlive = true là dang online
                // statusAlive = false là đang offline
                 */
                if (obj instanceof MessageContent) {
                    objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());

                    messageContent = (MessageContent) obj;
                    updateListUser(messageContent);
                    MessageContent content = new MessageContent();
                    content.setListUser(listUser);
                    objectOutputStream.writeObject(content);
                    objectOutputStream.flush();

                }
                if (obj.equals("123456789")) {
                    objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
                    listMessageBeSaved = checkedMessageContent();
                    objectOutputStream.writeObject(listMessageBeSaved);
                    objectOutputStream.flush();
                }
                if (obj instanceof MessageBeSaved) {
                    MessageBeSaved messageBeSaved = (MessageBeSaved) obj;
                    MessageContent content = messageBeSaved.getListMessageBeSaved().get(0);
                    sendedMessageContentToDatabase(content);
                    objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
                    objectOutputStream.writeObject(" đã gửi lên CSDL");
                    objectOutputStream.flush();
                }

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServerControl.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendedMessageContentToDatabase(MessageContent content) {
        System.out.println(content.getMessage());
        Connection connection = new JDBCConnection().getConnetion();
        String sql = " select * from messagecontent";
        int count = 0;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                count = count + 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        sql = " INSERT INTO messagecontent(nickname,messagecontent,id) VALUES (?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, content.getNickName());
            preparedStatement.setString(2, content.getMessage());
            preparedStatement.setInt(3, count);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public MessageBeSaved checkedMessageContent() {
        MessageBeSaved messageBeSaved = new MessageBeSaved();
        Connection connection = new JDBCConnection().getConnetion();
        String sql = "SELECT * FROM messagecontent";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String nickname = resultSet.getString("nickname");
                String messagecontent = resultSet.getString("messagecontent");
                MessageContent messageContent = new MessageContent(nickname, messagecontent);
                messageBeSaved.getListMessageBeSaved().add(messageContent);

            }
            System.out.println("vào checked MessageContent");
            return messageBeSaved;
        } catch (SQLException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public boolean checkUserLogged(String name) {
        for (String string : listUser) {
            if (string.equals(name)) {
                return true;
            }
        }
        return false;
    }

    // kiểm tra user online hay offline.
    // nếu online thì thêm vào danh sách online - listUser
    // nếu offline thì xóa khỏi danh sách online - listUser
    public void updateListUser(MessageContent messageContent) {
        if (!listUser.isEmpty()) {
            if (!messageContent.isStatusAlive()) {

                listUser.remove(messageContent.getNickName());
            } else {
                if (checkUserLogged(messageContent.getNickName())) {

                } else {
                    listUser.add(messageContent.getNickName());
                }
            }
        } else {
            listUser.add(messageContent.getNickName());
        }

    }

    // kiểm tra client có trong cơ sở dữ liệu
    public boolean checkUser(User user) {
        Connection conn = new JDBCConnection().getConnetion();
        String sql = "SELECT * FROM USER WHERE USERNAME = ? AND PASSWORD = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServerControl.class.getName()).log(Level.SEVERE, null, ex);

        }
        return false;
    }

    // lấy thời gian của server để gửi về cho client
    // đọc trong bài giảng lập trình mạng
    public long SyncTime() {


        try {
           long timeNow = control.getMiliTime();
            System.out.println(" time đồng bộ server : " + timeNow);
           return timeNow;
        } catch (Exception ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}
