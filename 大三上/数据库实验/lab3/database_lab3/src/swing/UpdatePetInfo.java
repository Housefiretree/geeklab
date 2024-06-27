package swing;

import Main.DB_controller;
import Tables.PetInfo;
import Tables.TypeInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import static Main.ImageManager.dogImage;
import static Main.ImageManager.updatePetInfoImage;
import static Main.Main.cardLayout;
import static Main.Main.cardPanel;
import static Main.StringManager.*;

public class UpdatePetInfo {
    private JPanel UpdatePetInfoPanel;
    private JLabel UpdatePetInfoLabel;
    private JComboBox TypeBox;
    private JTextField NameField;
    private JComboBox GenderBox;
    private JTextField CharacterField;
    private JTextField ColorField;
    private JButton ConfirmButton;
    private JButton BackButton;
    private JLabel TypeLabel;
    private JLabel NameLabel;
    private JLabel GenderLabel;
    private JLabel BirthdayLabel;
    private JLabel CharacterLabel;
    private JLabel ColorLabel;
    private JTextField BirthdayField;


    int wrong_message = 0;

    /**预编译正则表达式，用于检查格式是否正确*/
    Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    public  UpdatePetInfo(JFrame frame, DB_controller db_ctrl,PetInfo origin,
                          JTable PetInfoStaffTable, JScrollPane StaffPetScrollPane) throws SQLException {


        UpdatePetInfoLabel.setIcon(updatePetInfoImage);
        
        ArrayList<TypeInfo> res_type = db_ctrl.browse_type_info();
        for(int i=0;i<res_type.size();i++){
            TypeBox.addItem(res_type.get(i).pet_type_name);
        }

        GenderBox.addItem("雄");
        GenderBox.addItem("雌");

        String pet_id = origin.pet_id;

        NameField.setText(origin.pet_name);
        BirthdayField.setText(origin.pet_birthday);
        CharacterField.setText(origin.pet_character);
        ColorField.setText(origin.pet_color);


        ConfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //这里比较粗糙，因为没有考虑其他品种。。。。。。。之后可能需要修改
                int pet_type_id = TypeBox.getSelectedIndex()+1;

                String pet_name = "nullname";
                String enter_name = NameField.getText();
                if(enter_name.length()>PET_NAME_MAX_LENGTH){
                    JOptionPane.showMessageDialog(null,
                            "昵称太长！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message=1;
                }else if(enter_name.length()==0){
                    pet_name = "nullname";
                }else{
                    pet_name = enter_name;
                }
                

                String pet_gender;
                if(((String)GenderBox.getSelectedItem()).equals("雄")){
                    pet_gender="male";
                }else{
                    pet_gender="female";
                }


                Date pet_birthday = new Date();
                String enter_birthday = BirthdayField.getText();
                if(p.matcher(enter_birthday).matches()){
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    sdf.applyPattern("yyyy-MM-dd");
                    try {
                        pet_birthday = sdf.parse(enter_birthday);
                    } catch (ParseException ex) {
                        throw new RuntimeException(ex);
                    }
                }else{
                    JOptionPane.showMessageDialog(null,
                            "生日格式错误！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message=1;
                }



                String pet_character="nullcharc";
                String enter_character = CharacterField.getText();
                if(enter_character.length()>CHARACTER_MAX_LENGTH){
                    JOptionPane.showMessageDialog(null,
                            "性格太长！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message=1;
                }else if(enter_character.length()==0){
                    pet_character="nullcharc";
                }else{
                    pet_character=enter_character;
                }



                String pet_color="nullcolor";
                String enter_Color = ColorField.getText();
                if(enter_Color.length()>COLOR_MAX_LENGTH){
                    JOptionPane.showMessageDialog(null,
                            "颜色太长！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                    wrong_message=1;
                }else if(enter_Color.length()==0){
                    pet_color="nullcolor";
                }else{
                    pet_color=enter_Color;
                }

                if(wrong_message==0){
                    try {
                        db_ctrl.update_pet_info(pet_id,pet_type_id,pet_name,pet_gender,pet_birthday,pet_character,pet_color);
                        //PetInfoTableStaff.update_table();\
                        setTableData(db_ctrl,PetInfoStaffTable,StaffPetScrollPane);
                        cardLayout.show(cardPanel,STAFF_PET_PANEL);
                        frame.setVisible(true);
                        //db_ctrl.DB_close();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }else{
                    System.out.println("本次更新宠物信息失败");
                    wrong_message=0;
                }

            }
        });
        BackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel,STAFF_PET_PANEL);
                frame.setVisible(true);
            }
        });
    }

    public void setTableData(DB_controller db_ctrl,JTable PetInfoStaffTable,
                             JScrollPane StaffPetScrollPane) throws SQLException {
        ((DefaultTableModel)PetInfoStaffTable.getModel()).getDataVector().clear();

        String[] columnName={"宠物id","品种编号","昵称","性别","生日","年龄","性格","颜色","状态","申请数"};
        ArrayList<PetInfo> res = db_ctrl.browse_pet_info_staff();
        String[][]tableData = new String[res.size()][10];
        for(int i = 0; i< res.size(); i++){
            tableData[i][0] = res.get(i).pet_id;
            tableData[i][1] = res.get(i).pet_type_id;
            tableData[i][2] = res.get(i).pet_name;
            tableData[i][3] = res.get(i).pet_gender;
            tableData[i][4] = res.get(i).pet_birthday;
            tableData[i][5] = res.get(i).pet_age;
            tableData[i][6] = res.get(i).pet_character;
            tableData[i][7] = res.get(i).pet_color;
            tableData[i][8] = res.get(i).pet_state;
            tableData[i][9] = res.get(i).pet_applys;
        }

        DefaultTableModel newView = new DefaultTableModel(tableData,columnName){
            @Override
            public boolean isCellEditable(int row,int col){
                return false;
            }
        };

        PetInfoStaffTable.setModel(newView);
        StaffPetScrollPane.setViewportView(PetInfoStaffTable);
    }

    public JPanel getPanel() {
        return UpdatePetInfoPanel;
    }
}
