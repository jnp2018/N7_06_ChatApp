
package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.MessageContent;

/**
 *
 * checkThread : 1,sử dụng giao thức TCP để gửi và nhận gói tin
 *               2, nhiệm vụ : sau 1000ms thì gửi lên server để thông báo trạng thái online hoặc offline.
 */
public class CheckThread extends Thread {

    private ChatRoomView view;
    private MessageContent messageContent;
    private int portServer = 1997;
    private String host = "localhost";
    private Socket clientSocket;

    public CheckThread(MessageContent messageContent,ChatRoomView view) {
        this.messageContent = messageContent;
        this.view = view;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(2000);
                ObjectOutputStream objectOutputStream = null;
                ObjectInputStream objectInputStream = null;
                try {
                    clientSocket = new Socket(host, portServer);
                    objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    objectOutputStream.writeObject(messageContent);
                    objectOutputStream.flush();
                    objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                    MessageContent messageContentReceive = (MessageContent) objectInputStream.readObject();
                    ArrayList<String> lists = messageContentReceive.getListUser();
                    // refreshThread : cập nhật danh sach user đang online lên chatRoomVIew
                    new RefreshThread(lists,view).start();
                } catch (IOException ex) {
                    Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ChatRoomControl.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        if (clientSocket != null) {
                            clientSocket.close();
                        }
                        if (objectOutputStream != null) {
                            objectOutputStream.close();
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

}
