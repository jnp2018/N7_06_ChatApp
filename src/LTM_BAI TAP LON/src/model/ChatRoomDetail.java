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
public class ChatRoomDetail implements Serializable{
    private int idUser,idRoom;
    private String nameUser;
    private boolean  status;
    public ChatRoomDetail() {
    }

    public ChatRoomDetail(int idUser, int idRoom, String nameUser, boolean status) {
        this.idUser = idUser;
        this.idRoom = idRoom;
        this.nameUser = nameUser;
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }
    
}
