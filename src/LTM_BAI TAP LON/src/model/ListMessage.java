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
public class ListMessage implements Serializable{
    private ArrayList<Message> listMessage = new ArrayList<>();

    public ListMessage() {
    }
    public ListMessage(ArrayList<Message> listMessage){
        this.listMessage = listMessage;
    }
    public ArrayList<Message> getListMessage() {
        return listMessage;
    }

    public void setListMessage(ArrayList<Message> listMessage) {
        this.listMessage = listMessage;
    }
    
}
