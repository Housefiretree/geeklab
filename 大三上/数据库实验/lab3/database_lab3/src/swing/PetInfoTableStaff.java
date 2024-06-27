package swing;

import Main.DB_controller;
import Tables.PetInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import static Main.ImageManager.catImage;
import static Main.Main.cardLayout;
import static Main.Main.cardPanel;
import static Main.StringManager.*;

public class PetInfoTableStaff {
    private JPanel PetInfoTableStaffPanel;
    private JLabel PetInfoLabel;
    private JButton AddButton;
    private JButton BackButton;
    private JScrollPane StaffPetScrollPane;
    private JTable PetInfoStaffTable;
    private JButton UpdateButton;
    private JButton DeleteButton;



    /**
     * 还有一点问题，就是更新的时候可以显示之前的值吗？
     * */

    public PetInfoTableStaff(JFrame frame,DB_controller db_ctrl) throws SQLException {
        //想法：长按弹窗，选择删除或者修改，修改的话再弄出新的界面专门进行修改


        setTableData(db_ctrl,PetInfoStaffTable,StaffPetScrollPane);
        PetInfoLabel.setIcon(catImage);

        AddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //进入添加宠物信息的界面
                System.out.println("添加宠物信息");
                AddPetInfo add_pet = null;
                try {
                    add_pet = new AddPetInfo(frame,db_ctrl,PetInfoStaffTable,StaffPetScrollPane);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                cardPanel.add(add_pet.getPanel());
                cardLayout.last(cardPanel);
                frame.setVisible(true);

            }
        });
        UpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = PetInfoStaffTable.getSelectedRow();
                if(row==-1){
                    JOptionPane.showMessageDialog(null,
                            "未选中宠物信息", "Warning", JOptionPane.WARNING_MESSAGE);
                }else{
                    int result = JOptionPane.showConfirmDialog(UpdateButton,
                            "是否确定更新该宠物的信息？");
                    if (JOptionPane.YES_OPTION == result && row != -1) {
                        ArrayList<PetInfo> resu;
                        try {
                            resu = db_ctrl.browse_pet_info_staff();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        PetInfo origin = resu.get(row);
                        //System.out.println("更新宠物信息，id:"+update_pet_id);
                        //进入更新界面
                        UpdatePetInfo update_pet = null;
                        try {
                            update_pet = new UpdatePetInfo(frame,db_ctrl,origin,PetInfoStaffTable,StaffPetScrollPane);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        cardPanel.add(update_pet.getPanel());
                        cardLayout.last(cardPanel);
                        frame.setVisible(true);
                }
                //System.out.println(row);

                }
            }
        });

        //删除：已经ok
        DeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = PetInfoStaffTable.getSelectedRow();
                if(row==-1){
                    JOptionPane.showMessageDialog(null,
                            "未选中宠物信息", "Warning", JOptionPane.WARNING_MESSAGE);
                }else{
                    int result = JOptionPane.showConfirmDialog(DeleteButton,
                            "是否确定删除该宠物的信息？");
                    if (JOptionPane.YES_OPTION == result && row != -1) {
                        //不应该是这个tabledata，而是直接用row
                        //String delete_pet_id = tableData[row][0];
                        ArrayList<PetInfo> resu;
                        //model.removeRow(row);
                        try {
                            resu = db_ctrl.browse_pet_info_staff();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        PetInfo origin = resu.get(row);
                        String delete_pet_id = origin.pet_id;

                        boolean check;
                        try {
                            check = db_ctrl.check_pet_apply(delete_pet_id);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }

                        if(check){
                            JOptionPane.showMessageDialog(null,
                                    "该宠物有申请信息！不允许删除", "Warning", JOptionPane.WARNING_MESSAGE);
                        }else{
                            try {
                                db_ctrl.delete_pet_info(delete_pet_id);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                        //model.removeRow(row);

                        try {
                            setTableData(db_ctrl,PetInfoStaffTable,StaffPetScrollPane);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                }

                }
            }
        });
        BackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("返回上级界面");
                cardLayout.show(cardPanel,STAFF_PANEL);
                frame.setVisible(true);
            }
        });
    }

    public void setTableData(DB_controller db_ctrl, JTable PetInfoStaffTable,
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
        return PetInfoTableStaffPanel;
    }
}
