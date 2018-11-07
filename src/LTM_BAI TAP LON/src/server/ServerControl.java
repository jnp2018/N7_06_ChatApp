/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import dao.JDBCConnection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import model.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import login.LoginView;
import model.MessageBeSaved;
import model.MessageContent;

/**
 *
 * @author Admin
 */

public class ServerControl {
    private long  miliTime = System.currentTimeMillis();

    /*
    threadSocket : Thread  giao tiếp giữu client và server
    updateTimeServer : Thread cập nhật thời gian hiển thị của server
    messageContent :
    listUser : danh sách user đang online
     */
    private ServerView serverView;
    private ThreadSocket threadSocket;
    private UpdateTimeServer updateTimeServer;
    private ServerSocket serverSocket;
    private Socket socket;
    private int port = 1997;
    private MessageContent messageContent;
    private ArrayList<String> listUser;
    private MessageBeSaved messageBeSaved;
    public ServerControl(ServerView view) {
        messageBeSaved = new MessageBeSaved();
        messageContent = new MessageContent();
        listUser = new ArrayList<>();
        this.serverView = view;
        this.serverView.setVisible(true);
        openServer(this.port);
        // thêm sự kiện cho btnExit của ServerView
        this.serverView.addbtnExitActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });
        //cập nhật thời gian server,Để hiển thị lên serverView

        updateTimeServer = new UpdateTimeServer(this.serverView,  this);
        updateTimeServer.start();
        // server lắng nghe gói tin client gửi tới
        while (true) {
            listening();
        }

    }

    public long getMiliTime() {
        return miliTime;
    }

    public void setMiliTime(long miliTime) {
        this.miliTime = miliTime;
    }
 
    //
    public void openServer(int port) {
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException ex) {
            Logger.getLogger(ServerControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listening() {
        try {
            // luồng để xử lý khi có client gửi gói tin đến server 
            threadSocket = new ThreadSocket(serverSocket.accept(), this.serverView, listUser,messageBeSaved,this);
            threadSocket.start();

        } catch (IOException ex) {
            Logger.getLogger(ServerControl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            
        }

    }

}
