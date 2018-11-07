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
public class ChatRoom implements Serializable{
    private String roomName,hostCode;
    private String nameUserCreateChatRoom;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getHostCode() {
        return hostCode;
    }

    public void setHostCode(String hostCode) {
        this.hostCode = hostCode;
    }

    public String getNameUserCreateChatRoom() {
        return nameUserCreateChatRoom;
    }

    public void setNameUserCreateChatRoom(String nameUserCreateChatRoom) {
        this.nameUserCreateChatRoom = nameUserCreateChatRoom;
    }

   

    public ChatRoom() {
    }

    public ChatRoom(String roomName, String hostCode, String idUserCreateChatRoom) {
        this.roomName = roomName;
        this.hostCode = hostCode;
        this.nameUserCreateChatRoom = idUserCreateChatRoom;
    }

    @Override
    public String toString() {
        return getRoomName() + " , " + getHostCode() + " , " + getNameUserCreateChatRoom();
    }
    
}
