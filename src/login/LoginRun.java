/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

/**
 *
 * @author Admin
 */
public class LoginRun {

    public static void main(String[] args) {
        LoginView view = new LoginView();
        LoginControl control = new LoginControl(view);
    }
}
