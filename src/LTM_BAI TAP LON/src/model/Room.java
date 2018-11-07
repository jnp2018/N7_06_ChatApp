/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Admin
 */
public class Room implements  Serializable{
    private int id;
    private String roomname;
    private String hostname;
    private int userCreateChatRoom;
    private String userName = "";

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getUserCreateChatRoom() {
        return userCreateChatRoom;
    }

    public void setUserCreateChatRoom(int userCreateChatRoom) {
        this.userCreateChatRoom = userCreateChatRoom;
    }

    public Room() {
    }

    public Room(int id, String roomname, String hostname, int userCreateChatRoom, String userName) {
        this.id = id;
        this.roomname = roomname;
        this.hostname = hostname;
        this.userCreateChatRoom = userCreateChatRoom;
        this.userName = userName;
    }

    
   

    @Override
    public String toString() {
        return getRoomname();
    }
    
    
}
