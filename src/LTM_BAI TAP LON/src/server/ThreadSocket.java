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
import model.ChatRoom;
import model.ChatRoomDetail;
import model.ListMessage;
import model.ListRoom;
import model.Message;
import model.MessageBeSaved;
import model.MessageContent;
import model.Room;
import model.User;

public class ThreadSocket extends Thread {

    /*
    listUser : danh sách user đang online
     */
    private Socket socket;
    private ServerView view;
    private ServerControl control;
    private ArrayList<String> listUser;
    private ListRoom listRoom;
    private MessageContent messageContent;
    private ListMessage listMessage;

    public ThreadSocket(Socket s, ServerView view, ArrayList<String> listUser, MessageBeSaved listMessageContent, ServerControl serverControl) {
        this.socket = s;
        this.view = view;
        this.listUser = listUser;
        this.control = serverControl;
        listRoom = new ListRoom();
        listMessage = new ListMessage();
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

                // kiểm tra thành viên có phải người tạo group 
                if (obj instanceof Room) {
                    Room room = (Room) obj;
                   boolean result =  isUserCreatRoom(room);
                   objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
                   objectOutputStream.writeObject(result);
                }

                //thêm or xóa thành viên vào nhóm chat để lưu vào csdl
                if (obj instanceof ChatRoomDetail) {
                    ChatRoomDetail chatRoomDetail = (ChatRoomDetail) obj;
                    if (chatRoomDetail.isStatus()) {
                        System.out.println("them thanh vien vao group");
                        addMemberToGroup(chatRoomDetail);
                    } else {
                        System.out.println("xoa thanh vien vao group");
                        remmoveMemberToGroup(chatRoomDetail);
                    }

                }

                // xoa 1 tin nhắn khỏi CSDL
                if (obj instanceof String) {
                    String content = (String) obj;
                    if (content.contains(":")) {
                        System.out.println(" xoa tin nhan");

                        deleteMessage(content);
                    }
                }

                // 
                // Thêm 1 chat room vào csdl
                if (obj instanceof ChatRoom) {
                    ChatRoom chatRoom = (ChatRoom) obj;
                    System.out.println(chatRoom);
                    dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
                    if (addChatRoomToDatabase(chatRoom)) {

                        dataOutputStream.writeInt(1);
                        dataOutputStream.flush();

                    } else {
                        dataOutputStream.writeInt(0);
                        dataOutputStream.flush();
                    }

                }

                /*
                * khi giao diện Login thì obj là User
                * khi giao diện chatRoom thì obj là messageContent
                 */
                if (obj instanceof User) {
                    /*
                    bước 
                    1: client đóng gói object User và gửi cho server
                    2: server kiểm tra:
                        1: nếu user có trong CSDL và chưa đăng nhập thì trả về thời gian chính xác của server
                        2: nếu user có trong CSDL và đã đăng nhập thì trả về thời gian =Mon Jan 12 20:46:40 ICT 1970
                        3: nếu user không có trong CSDL thì trả về thời gian = Thu Jan 01 07:00:00 ICT 1970
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
                        dataOutputStream.writeLong(0);
                        dataOutputStream.flush();
                    } // nếu đăng nhập vào tài khoản đang online
                    else {
                        dataOutputStream.writeLong(1000000000);
                        dataOutputStream.flush();

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
                // lấy tin nhắn lưu trong CSDL để hiển thị lên ChatRoom
                if (obj instanceof Integer) {
                    objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
                    int idRoom = (Integer) obj;
                    System.out.println("id Room nhận được là : " + idRoom);
                    listMessage = checkedMessageContent(idRoom);
                    objectOutputStream.writeObject(listMessage);
                    objectOutputStream.flush();
                }
                // lưu tin nhắn của 1 client vừa chat lên CSDL
                if (obj instanceof ListMessage) {
                    ListMessage listmes = (ListMessage) obj;
                    Message message = listmes.getListMessage().get(0);
                    sendedMessageContentToDatabase(message);
                    objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
                    objectOutputStream.writeObject(" đã gửi lên CSDL");
                    objectOutputStream.flush();
                }

                // lấy ra danh sách phòng từ cơ sở dữ liệu để gửi về cho client
                if (obj.equals("listroom")) {
                    objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
                    listRoom = takeListRoomFromDatabase();
                    objectOutputStream.writeObject(listRoom);
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

    private boolean isUserCreatRoom(Room room) {
        String nameUserCreatRoom = "";
        String userName = room.getUserName();
        int idUserCreatRoom = room.getUserCreateChatRoom();
        Connection connection = new JDBCConnection().getConnetion();
        String sql = "select USER_NAME from [USER] where ID = ?; ";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, idUserCreatRoom);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                nameUserCreatRoom = resultSet.getString("USER_NAME").trim();
            }
            if(nameUserCreatRoom.equals(userName)){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void remmoveMemberToGroup(ChatRoomDetail chatRoomDetail) {
        int idRoom = chatRoomDetail.getIdRoom();
        String nameUser = chatRoomDetail.getNameUser().trim();
        Connection connection = new JDBCConnection().getConnetion();
        String sql = "delete from CHAT_ROOM_DETAIL where ID_USER = (select ID from [USER] where USER_NAME = ?) and ID_CHAT_ROOM =?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nameUser);
            preparedStatement.setInt(2, idRoom);
            System.out.println(preparedStatement.executeUpdate());
        } catch (SQLException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addMemberToGroup(ChatRoomDetail chatRoomDetail) {
        int idRoom = chatRoomDetail.getIdRoom();
        String nameUser = chatRoomDetail.getNameUser().trim();
        Connection connection = new JDBCConnection().getConnetion();
        String sql = " insert into CHAT_ROOM_DETAIL(ID_USER,ID_CHAT_ROOM) values((select ID from [USER] where USER_NAME =?),?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nameUser);
            preparedStatement.setInt(2, idRoom);
            System.out.println(preparedStatement.executeUpdate());
        } catch (SQLException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deleteMessage(String content) {
        String[] str = content.split(":");
        String userName = str[1].trim();
        String textMessage = str[0].trim();
        int idRoom = Integer.parseInt(str[2]);
        System.out.println(userName);
        System.out.println(textMessage);
        System.out.println(idRoom);
        Connection connection = new JDBCConnection().getConnetion();
        String sql = "delete from MESSAGE where ID_USER = (select ID from [USER] where USER_NAME = ?) and TEXT_MESSAGE = ? and ID_CHAT_ROOM =?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, textMessage);
            preparedStatement.setInt(3, idRoom);

            System.out.println(preparedStatement.executeUpdate());
        } catch (SQLException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean addChatRoomToDatabase(ChatRoom chatRoom) {
        Connection connection = new JDBCConnection().getConnetion();

        String userCreatChatRoom = "";
        int idUserCreatChatRoom = 0;
        String sql = "SELECT * FROM [USER] WHERE USER_NAME = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, chatRoom.getNameUserCreateChatRoom());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                idUserCreatChatRoom = resultSet.getInt("ID");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("id của user tạo chat room :" + idUserCreatChatRoom);
        sql = " INSERT INTO CHAT_ROOM(ROOM_NAME,HOST_CODE,ID_USER_CREATE_CHAT_ROOM) VALUES(?,?,?)";
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, chatRoom.getRoomName());
            preparedStatement.setString(2, chatRoom.getHostCode());
            preparedStatement.setInt(3, idUserCreatChatRoom);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void sendedMessageContentToDatabase(Message message) {
        // System.out.println(mes.getMessage());
        Connection connection = new JDBCConnection().getConnetion();
        String sql = " insert into MESSAGE (ID_USER,TEXT_MESSAGE,IMAGE_MESSAGE,"
                + "ID_CHAT_ROOM) values ((select ID from [USER] where USER_NAME = ?) ,?,null,? );";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, message.getNameUser());
            preparedStatement.setString(2, message.getTextMessage());
            preparedStatement.setInt(3, message.getIdChatRoom());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ListRoom takeListRoomFromDatabase() {
        ListRoom list = new ListRoom();
        Connection connection = new JDBCConnection().getConnetion();
        String sql = "SELECT * FROM CHAT_ROOM";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int idRoom = resultSet.getInt("ID");
                String roomName = resultSet.getString("ROOM_NAME");
                String hostCode = resultSet.getString("HOST_CODE");
                int idUserCreateChatRoom = resultSet.getInt("ID_USER_CREATE_CHAT_ROOM");
                Room room = new Room();
                room.setId(idRoom);
                room.setRoomname(roomName);
                room.setHostname(hostCode);
                room.setUserCreateChatRoom(idUserCreateChatRoom);
                System.out.println(" dữ liệu room : " + room.toString());
                list.getListRoom().add(room);

            }
            System.out.println("vào checked MessageContent");
            return list;
        } catch (SQLException ex) {
            System.out.println("lỗi ở takeListRoomFromDatabase");
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    // lấy tất cả tin nhắn đã lưu trong CSDL để hiển thị lên ChatRoom
    public ListMessage checkedMessageContent(int idRoom) {
        ListMessage listMessage = new ListMessage();
        Connection connection = new JDBCConnection().getConnetion();
        String sql = " select USER_NAME,TEXT_MESSAGE from [USER] as u inner join  MESSAGE as m on u.ID = m.ID_USER where m.ID_CHAT_ROOM = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, idRoom);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("USER_NAME");
                String massage = resultSet.getString("TEXT_MESSAGE");
                Message message = new Message();
                message.setNameUser(name);
                message.setTextMessage(massage);
                listMessage.getListMessage().add(message);
            }
            return listMessage;
//            while (resultSet.next()) {
//                String nickname = resultSet.getString("nickname");
//                String messagecontent = resultSet.getString("messagecontent");
//                MessageContent messageContent = new MessageContent(nickname, messagecontent);
//                messageBeSaved.getListMessageBeSaved().add(messageContent);
//
//            }
//            System.out.println("vào checked MessageContent");
//            return messageBeSaved;
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
        String sql = "SELECT * FROM [USER] WHERE USER_NAME = ? AND PASS_WORD = ?";
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
    // 
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
