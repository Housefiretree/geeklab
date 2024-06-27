package swing;

import Main.DB_controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import static Main.ImageManager.addStaffInfoImage;
import static Main.Main.cardLayout;
import static Main.Main.cardPanel;
import static Main.StringManager.*;

public class AddStaffInfo {
    private JPanel AddStaffInfoPanel;
    private JLabel AddStaffInfoLabel;
    private JTextField NameField;
    private JTextField AgeField;
    private JButton ConfirmButton;
    private JLabel NameLabel;
    private JLabel GenderLabel;
    private JLabel AgeLabel;
    private JComboBox GenderBox;



    int wrong_message = 0;
public AddStaffInfo(JFrame frame, DB_controller db_ctrl,String new_user_id) {
    AddStaffInfoLabel.setIcon(addStaffInfoImage);


    GenderBox.addItem("请选择性别");
    GenderBox.addItem("男");
    GenderBox.addItem("女");
    GenderBox.addItem("其他");

    ConfirmButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = NameField.getText();
            String gender = (String) GenderBox.getSelectedItem();
            if (gender == "男") {
                gender = "male";
            } else if (gender == "女") {
                gender = "female";
            } else if (gender == "其他") {
                gender = "other";
            } else {
                gender = "nullgender";
            }
            String age_enter = AgeField.getText();

            //处理各种错误
            //1.针对name:长度超过30，或者为空
            if (name.length() > NAME_MAX_LENGTH) {
                JOptionPane.showMessageDialog(null,
                        "姓名太长！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                wrong_message = 1;
            } else if (name.length() == 0) {
                JOptionPane.showMessageDialog(null,
                        "姓名为空！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                wrong_message = 1;
            }

            //2.年龄
            if (age_enter.length() == 0) {
                JOptionPane.showMessageDialog(null,
                        "年龄为空！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                wrong_message = 1;
            }


            if (Integer.parseInt(age_enter) <= STAFF_MIN_AGE) {
                JOptionPane.showMessageDialog(null,
                        "管理员年龄过小！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                wrong_message = 1;
            } else if (Integer.parseInt(age_enter) > MAX_AGE) {
                JOptionPane.showMessageDialog(null,
                        "年龄过大！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                wrong_message = 1;
            }


            //3.性别：已经做了限制


            int age = Integer.parseInt(age_enter);

            if (wrong_message == 0) {

                //会报错，注意增加对空值的处理
                int new_staff_id = 0;
                try {
                    new_staff_id = db_ctrl.insert_staff(name,gender,age,new_user_id);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(null,"管理员注册成功，管理员id:"+new_staff_id,
                        "Message",JOptionPane.INFORMATION_MESSAGE);

                //JOptionPane.showMessageDialog(null, "注册成功！您的账号为："+new_user_id);

                //如果成功，跳转到开始界面
                cardLayout.show(cardPanel,"StartPanel");

            } else {
                System.out.println("本次管理员信息填写失败");
                wrong_message = 0;
            }


        }
    });

}
    public JPanel getPanel() {
        return AddStaffInfoPanel;
    }

}