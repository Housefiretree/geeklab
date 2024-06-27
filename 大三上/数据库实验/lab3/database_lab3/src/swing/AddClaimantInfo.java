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

public class AddClaimantInfo {
    private JPanel AddClaimantInfoPanel;
    private JLabel AddInfoLabel;
    private JTextField NameField;
    private JComboBox GenderBox;
    private JTextField AgeField;
    private JTextField PhoneField;
    private JTextField CityField;
    private JTextField StreetField;
    private JTextField HouseField;
    private JButton ConfirmButton;
    private JLabel NameLabel;
    private JLabel GenderLabel;
    private JLabel AgeLabel;
    private JLabel PhoneLabel;
    private JLabel CityLabel;
    private JLabel StreetLabel;
    private JLabel HouseLabel;
    private JTextField EmailField;
    private JLabel EmailLabel;

    int wrong_message = 0;

    public AddClaimantInfo(JFrame frame, DB_controller db_ctrl,String user_id) {
        AddInfoLabel.setIcon(claimantInfoImage);


        GenderBox.addItem("请选择性别");
        GenderBox.addItem("男");
        GenderBox.addItem("女");
        GenderBox.addItem("其他");


        ConfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //先获取各种信息
                String name_enter = NameField.getText();
                String name="nullname";
                if (name_enter.length() > NAME_MAX_LENGTH) {
                    JOptionPane.showMessageDialog(null,
                            "姓名太长！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message = 1;
                } else if (name_enter.length() == 0) {
                    JOptionPane.showMessageDialog(null,
                            "姓名为空！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message = 1;
                }
                name = name_enter;


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
                if (age_enter.length() == 0) {
                    JOptionPane.showMessageDialog(null,
                            "年龄为空！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message = 1;
                }else if(Integer.parseInt(age_enter)<CLAIMANT_MIN_AGE){
                    JOptionPane.showMessageDialog(null,
                            "申请人年龄过小！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message = 1;
                }else if(Integer.parseInt(age_enter)>MAX_AGE){
                    JOptionPane.showMessageDialog(null,
                            "年龄过大！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message = 1;
                }
                int age = Integer.parseInt(age_enter);



                String phone_enter = PhoneField.getText();
                String phone_num = "11111111111";
                if (phone_enter.length()!=PHONE_LENGTH) {
                    JOptionPane.showMessageDialog(null,
                            "电话号码位数不对！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message = 1;
                }
                BigDecimal phone = new BigDecimal(phone_enter);


                String email_enter = EmailField.getText();
                String email = "nullemail";
                if (email_enter.length()>EMAIL_MAX_LENGTH) {
                    JOptionPane.showMessageDialog(null,
                            "电子邮箱地址太长！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message = 1;
                }else if(email_enter.length()>0){
                    email = email_enter;
                }



                String city_enter = CityField.getText();
                String city = "nullcity";
                if (city_enter.length()>CITY_MAX_LENGTH) {
                    JOptionPane.showMessageDialog(null,
                            "城市名太长！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message = 1;
                }else if(city_enter.length()>0){
                    city = city_enter;
                }


                String street_enter = StreetField.getText();
                String street = "nullstreet";
                if (street_enter.length()>STREET_MAX_LENGTH) {
                    JOptionPane.showMessageDialog(null,
                            "街道名太长！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message = 1;
                }else if(street_enter.length()>0){
                    street = street_enter;
                }


                String house_enter = HouseField.getText();
                String house = "nullstreet";
                if (house_enter.length()>HOUSE_MAX_LENGTH) {
                    JOptionPane.showMessageDialog(null,
                            "门牌号太长！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message = 1;
                }else if(house_enter.length()>0){
                    house = house_enter;
                }

                if (wrong_message == 0) {

                    //会报错，注意增加对空值的处理
                    int new_claimant_id = 0;
                    try {
                        new_claimant_id = db_ctrl.add_claimant(name,gender,age,user_id,
                                phone,email,city,street,house);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    JOptionPane.showMessageDialog(null,"申请人添加成功，申请人id:"+new_claimant_id+
                                    ",请重新添加申请",
                            "Message",JOptionPane.INFORMATION_MESSAGE);

                    //JOptionPane.showMessageDialog(null, "注册成功！您的账号为："+new_user_id);

                    //成功后回到刚才浏览的界面
                    cardLayout.show(cardPanel,CLAIMANT_PET_PANEL);
                    frame.setVisible(true);

                } else {
                    System.out.println("本次申请人信息填写失败");
                    wrong_message = 0;
                }


            }
        });
    }

    public JPanel getPanel() {
        return AddClaimantInfoPanel;
    }
}
