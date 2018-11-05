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
public class MessageBeSaved implements Serializable{
    private ArrayList<MessageContent> listMessageBeSaved = new ArrayList<>();
    public MessageBeSaved(){}
    public MessageBeSaved(ArrayList<MessageContent> listMessageBeSaved){
        this.listMessageBeSaved = new ArrayList<>();
    }

    public ArrayList<MessageContent> getListMessageBeSaved() {
        return listMessageBeSaved;
    }

    public void setListMessageBeSaved(ArrayList<MessageContent> listMessageBeSaved) {
        this.listMessageBeSaved = listMessageBeSaved;
    }
    
}
