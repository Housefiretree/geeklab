package swing;

import Main.DB_controller;
import Tables.PetInfo;
import Tables.PetInfo_Claimant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import static Main.ImageManager.*;
import static Main.Main.cardLayout;
import static Main.Main.cardPanel;
import static Main.StringManager.*;

public class PetInfoTableClaimant {
    private JPanel PetInfoTableClaimantPanel;
    private JLabel PetInfoLabel;
    private JScrollPane PetClaimantScrollPane;
    private JTable PetClaimantTable;
    private JButton ApplyButton;
    private JButton BackButton;
public PetInfoTableClaimant(JFrame frame, DB_controller db_ctrl,String user_id) throws SQLException {

    setTableData(db_ctrl,PetClaimantTable,PetClaimantScrollPane);

    PetInfoLabel.setIcon(catImage);

    ApplyButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = PetClaimantTable.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(null,
                        "未选中宠物信息", "Warning", JOptionPane.WARNING_MESSAGE);
            }else{
                int result = JOptionPane.showConfirmDialog(ApplyButton,
                        "是否确定申请领养该宠物？");
                if (JOptionPane.YES_OPTION == result && row != -1) {
                    boolean check;
                    try {
                        check = db_ctrl.check_claimant_contact(user_id);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                    ArrayList<PetInfo_Claimant> resu;
                    //model.removeRow(row);
                    try {
                        resu = db_ctrl.browse_pet_info_claimant();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    PetInfo_Claimant pet = resu.get(row);
                    String petName = pet.pet_name;



                    if(check){
                        int claimant_id;
                        try {
                            claimant_id = db_ctrl.getClaimantID(user_id);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        try {
                            db_ctrl.add_apply(petName,claimant_id);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        JOptionPane.showMessageDialog(null,
                                "添加申请成功", "消息", JOptionPane.INFORMATION_MESSAGE);

                    }else{
                        JOptionPane.showMessageDialog(null,
                                "未添加联系方式，请添加联系方式", "Warning", JOptionPane.WARNING_MESSAGE);
                        //进入添加联系方式界面
                        System.out.println("进入添加联系人方式界面");
                        AddClaimantInfo add_claimant = null;
                        add_claimant = new AddClaimantInfo(frame,db_ctrl,user_id);
                        cardPanel.add(add_claimant.getPanel());
                        cardLayout.last(cardPanel);
                        frame.setVisible(true);
                    }

                    try {
                        setTableData(db_ctrl,PetClaimantTable,PetClaimantScrollPane);
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
            cardLayout.show(cardPanel,CLAIMANT_PANEL);
            frame.setVisible(true);
        }
    });
}


    public void setTableData(DB_controller db_ctrl, JTable PetClaimantTable,
                             JScrollPane PetClaimantScrollPane) throws SQLException {
        ((DefaultTableModel)PetClaimantTable.getModel()).getDataVector().clear();

        String[] columnName={"昵称","品种","性别","生日","性格","颜色"};
        ArrayList<PetInfo_Claimant> res = db_ctrl.browse_pet_info_claimant();
        String[][]tableData = new String[res.size()][6];
        for(int i = 0; i< res.size(); i++){
            tableData[i][0] = res.get(i).pet_name;
            tableData[i][1] = res.get(i).pet_type;
            tableData[i][2] = res.get(i).pet_gender;
            tableData[i][3] = res.get(i).pet_birthday;
            tableData[i][4] = res.get(i).pet_character;
            tableData[i][5] = res.get(i).pet_color;
        }

        DefaultTableModel newView = new DefaultTableModel(tableData,columnName){
            @Override
            public boolean isCellEditable(int row,int col){
                return false;
            }
        };

        PetClaimantTable.setModel(newView);
        PetClaimantScrollPane.setViewportView(PetClaimantTable);
    }

    public JPanel getPanel() {
        return PetInfoTableClaimantPanel;
    }

}
