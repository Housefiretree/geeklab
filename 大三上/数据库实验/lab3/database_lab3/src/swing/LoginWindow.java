package swing;

import Main.DB_controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import static Main.ImageManager.*;
import static Main.Main.*;
import static Main.StringManager.*;

public class LoginWindow {
    private JPanel LoginPanel;
    private JTextField IDtextField;
    private JPasswordField PSWField;
    private JCheckBox staffCheckbox;
    private JButton ConfirmButton;
    private JButton BackButton;
    private JLabel IDLabel;
    private JLabel PSWLabel;
    private JLabel LoginLabel;

    private int check_flag=0;


    int wrong_message = 0;



    public LoginWindow(JFrame frame, DB_controller db_ctrl) {

        LoginLabel.setIcon(LoginWelcomeImage);


        ConfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ID = IDtextField.getText();
                String PSW = PSWField.getText();
                boolean staff_or_not = staffCheckbox.isSelected();
                System.out.println("输入账号：" + ID);
                System.out.println("输入密码：" + PSW);
                if(staff_or_not==true){
                    System.out.println("管理员登录");
                }

                try {
                    int flag = db_ctrl.check_user(ID,PSW,staff_or_not);
                    if(flag==LOGIN_SUCCESS){
                        System.out.println("进入相应界面");
                        int staff_id = db_ctrl.getStaffID(ID);
                        //这时根据是否为管理员进入不同界面
                        check_flag=1;

                        if(staff_or_not==true){
                            //是管理员，进入管理员界面
                            StaffWindow staff_window = new StaffWindow(frame,db_ctrl,staff_id);
                            cardPanel.add(STAFF_PANEL,staff_window.getPanel());
                            cardLayout.show(cardPanel,STAFF_PANEL);
                            frame.setVisible(true);
                        }else{
                            //否则进入认领人界面
                            ClaimantWindow claimant_window = new ClaimantWindow(frame,db_ctrl,ID);
                            cardPanel.add(CLAIMANT_PANEL,claimant_window.getPanel());
                            cardLayout.show(cardPanel,CLAIMANT_PANEL);
                            frame.setVisible(true);

                        }


                    }else if(flag==ID_OR_ROLE_ERROR){
                        //提示错误信息
                        System.out.println("出错");
                        JOptionPane.showMessageDialog(null,
                                "账号或角色错误！请重新进行登录", "Warning", JOptionPane.WARNING_MESSAGE);
                    }else{
                        System.out.println("出错");
                        JOptionPane.showMessageDialog(null,
                                "密码错误！请重新进行登录", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        BackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel,START_PANEL);
            }
        });
    }

    public JPanel getPanel() {
        return LoginPanel;
    }
}
