package swing;

import Main.DB_controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;

import static Main.ImageManager.*;
import static Main.Main.cardLayout;
import static Main.Main.cardPanel;
import static Main.StringManager.*;

public class RegisterWindow {
    private JPanel RegisterPanel;
    private JLabel RegisterLabel;


    private JPasswordField PSWField1;
    private JPasswordField PSWField2;
    private JCheckBox RoleCheckBox;
    private JButton ConfirmButton;
    private JLabel PSWLabel1;
    private JLabel PSWLabel2;




    int wrong_message = 0;


    public RegisterWindow(JFrame frame, DB_controller db_ctrl){

        RegisterLabel.setIcon(RegisterWelcomeImage);


        ConfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //特别注意需要满足各种约束！！！！！！！！！！

                //先获取各种信息

                String psw_1 = PSWField1.getText();
                String psw_2 = PSWField2.getText();
                boolean staff_or_not = RoleCheckBox.isSelected();





                //密码
                if(psw_1.length()<PSW_MIN_LENGTH){
                    JOptionPane.showMessageDialog(null,
                            "设置密码长度过小！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message=1;
                }else if(psw_1.length()>PSW_MAX_lENGTH){
                    JOptionPane.showMessageDialog(null,
                            "设置密码长度过大！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message=1;
                }

                if(psw_1.equals(psw_2)){

                }else{
                    JOptionPane.showMessageDialog(null,
                            "两次密码不同！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message=1;
                }

                //角色，已经做了限制

                //转换处理
                //int age = Integer.parseInt(age_enter);

                //BigDecimal phone = new BigDecimal(phone_num);

                BigDecimal role;
                if(staff_or_not==true){
                    role = USER_ROLE_STAFF;
                }else{
                    role = USER_ROLE_CLAIMANT;
                }

                try {
                    if(wrong_message==0){

                        //会报错，注意增加对空值的处理

                        String new_user_id = db_ctrl.add_user(psw_1,role);
                        JOptionPane.showMessageDialog(null,"注册成功！您的账号为："+new_user_id,
                                "Message",JOptionPane.INFORMATION_MESSAGE);
                        if(role.equals(USER_ROLE_STAFF)){
                            //进入管理员注册界面
                            AddStaffInfo add_staff = new AddStaffInfo(frame,db_ctrl,new_user_id);
                            cardPanel.add(add_staff.getPanel());
                            cardLayout.last(cardPanel);
                            frame.setVisible(true);
                        }else{
                            cardLayout.show(cardPanel,"StartPanel");
                        }

                        //JOptionPane.showMessageDialog(null, "注册成功！您的账号为："+new_user_id);

                        //如果成功，跳转到开始界面

                    }else{
                        System.out.println("本次注册失败");
                        wrong_message=0;
                    }


                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }


            }
        });


        }
        public JPanel getPanel() {
            return RegisterPanel;
        }
    }