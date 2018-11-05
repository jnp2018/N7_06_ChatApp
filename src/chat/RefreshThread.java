/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.MessageContent;

/**
 *
 * 
 */
public class RefreshThread extends Thread{
    private ChatRoomView view;
    private ArrayList<String> listUser;
    private JTable table ;
    public RefreshThread(ArrayList<String> listUser,ChatRoomView view){
        this.view = view;
        this.listUser = listUser;
    }

    @Override
    public void run() {
        table = this.view.getTableListUserOnline();
        DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();
        defaultTableModel.setRowCount(0);
        String[] list = new String[1]; 
        for (String string : listUser) {
            list[0] = string;
            defaultTableModel.addRow(list);
        }
        table.setModel(defaultTableModel);
    }
    
    
}
