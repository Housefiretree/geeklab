package swing;

import Main.DB_controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import static Main.ImageManager.*;
import static Main.Main.cardLayout;
import static Main.Main.cardPanel;
import static Main.StringManager.*;

public class StartWindow {
    private JButton LoginButton;
    private JButton RegisterButton;
    private JPanel StartPanel;
    private JButton ExitButton;
    private JLabel WelcomeLabel;
    private JLabel DogPicLabel;
    //private JLabel ClawLabel1;
    //private JLabel ClawLabel2;

    private static int first_time_login = 0;
    private static int first_time_register=0;


    public StartWindow(JFrame frame, DB_controller db_ctrl) {

        DogPicLabel.setIcon(sleepdogImage);
        WelcomeLabel.setIcon(clawImage);

        LoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //点击登录按钮，进入登录界面
                System.out.println("进入登录界面");
                LoginWindow login = new LoginWindow(frame,db_ctrl);
                if(first_time_login==0){
                    cardPanel.add(LOGIN_PANEL,login.getPanel());
                    first_time_login=1;
                }
                cardLayout.show(cardPanel,LOGIN_PANEL);
                frame.setVisible(true);
            }
        });
        RegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //点击注册按钮，进入注册界面
                System.out.println("进入注册界面");
                RegisterWindow register = new RegisterWindow(frame,db_ctrl);
                if(first_time_register==0){
                    cardPanel.add(REGISTER_PANEL,register.getPanel());
                    first_time_register=1;
                }
                cardLayout.show(cardPanel,REGISTER_PANEL);
                frame.setVisible(true);
            }
        });
        ExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //退出平台
                try {
                    db_ctrl.DB_close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);
            }
        });
    }
    public JPanel getPanel() {
        return StartPanel;
    }

}