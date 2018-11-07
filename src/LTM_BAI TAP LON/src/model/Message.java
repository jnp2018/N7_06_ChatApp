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
public class Message implements Serializable{
    private String textMessage,imageMessage,nameUser;
    private int idUser,idChatRoom;

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    
    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getImageMessage() {
        return imageMessage;
    }

    public void setImageMessage(String imageMessage) {
        this.imageMessage = imageMessage;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdChatRoom() {
        return idChatRoom;
    }

    public void setIdChatRoom(int idChatRoom) {
        this.idChatRoom = idChatRoom;
    }

    public Message() {
    }

    public Message(String textMessage, String imageMessage, int idUser, int idChatRoom) {
        this.textMessage = textMessage;
        this.imageMessage = imageMessage;
        this.idUser = idUser;
        this.idChatRoom = idChatRoom;
    }

    @Override
    public String toString() {
        return getIdUser() + " , " + getIdChatRoom() + " , " + getTextMessage();
    }
    
}
