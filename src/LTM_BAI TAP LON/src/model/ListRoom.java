/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class ListRoom implements Serializable{
    private ArrayList<Room> listRoom = new ArrayList<>();

    public ArrayList<Room> getListRoom() {
        return listRoom;
    }

    public void setListRoom(ArrayList<Room> listRoom) {
        this.listRoom = listRoom;
    }

    public ListRoom(ArrayList<Room> listRoom) {
        this.listRoom = listRoom;
    }

    public ListRoom() {
    }
    
}
