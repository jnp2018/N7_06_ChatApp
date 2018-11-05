/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * MessageContent : nội dung tin nhắn khi client gửi cho nhau
 *  
 */
public class MessageContent implements Serializable{
    /*
    nickName : tên client
    message : nội dung của tin chat
    timeMessage : thời gian của tin chat
    statusAlive : trạng thái của user (online or offline)
    listUser : danh sách user online

    */
    private String nickName;
    private String message;
    private String timeMessage;
    private boolean statusAlive;
    private ArrayList<String> listUser = new ArrayList<>();

    public MessageContent(){
        this.nickName = "";
        this.message = "";
        this.timeMessage = "";
        statusAlive = false;
        listUser = new ArrayList<>();
    }

    public boolean isStatusAlive() {
        return statusAlive;
    }

    public void setStatusAlive(boolean statusAlive) {
        this.statusAlive = statusAlive;
    }

    public ArrayList<String> getListUser() {
        return listUser;
    }

    public void setListUser(ArrayList<String> listUser) {
        this.listUser = listUser;
    }

    public MessageContent(String nickName, String message) {
        this.nickName = nickName;
        this.message = message;
    }
    
    
    public MessageContent(String nickName, String message,String timeMessage) {
        this.nickName = nickName;
        this.message = message;
        this.timeMessage = timeMessage;
    }

    public String getTimeMessage() {
        return timeMessage;
    }

    public void setTimeMessage(String timeMessage) {
        this.timeMessage = timeMessage;
    }

    
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
