package swing;

import Main.DB_controller;
import Tables.ApplyInfo;
import Tables.ApplyInfo_Claimant;
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

public class ApplyInfoTable {
    private JPanel ApplyInfoTabelPanel;
    private JLabel ApplyInfoLabel;
    private JScrollPane ApplyInfoScrollPane;
    private JTable ApplyTable;
    private JButton AdoptButton;
    private JButton BackButton;
public ApplyInfoTable(JFrame frame, DB_controller db_ctrl,int claimant_id) throws SQLException {

    setTableData(db_ctrl,ApplyTable,ApplyInfoScrollPane,claimant_id);

    ApplyInfoLabel.setIcon(dogImage);

    AdoptButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = ApplyTable.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(null,
                        "未选中申请信息", "Warning", JOptionPane.WARNING_MESSAGE);
            }else{
                int result = JOptionPane.showConfirmDialog(AdoptButton,
                        "是否确定办理领养该宠物？");
                if (JOptionPane.YES_OPTION == result && row != -1) {
                    ArrayList<ApplyInfo_Claimant> resu;
                    //model.removeRow(row);
                    try {
                        resu = db_ctrl.browse_apply_info_claimant(claimant_id);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    ApplyInfo_Claimant apply = resu.get(row);
                    String apply_id = apply.apply_id;

                    boolean flag;
                    try {
                        flag = db_ctrl.check_apply_adopt(apply_id);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                    if(flag){
                        JOptionPane.showMessageDialog(null,
                                "该宠物已经办理领养", "重复领养", JOptionPane.WARNING_MESSAGE);
                    }else{
                        int state=0;
                        try {
                            state = db_ctrl.add_adopt(apply_id);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }


                        if(state==1){
                            JOptionPane.showMessageDialog(null,
                                    "办理领养成功，请到“我的领养”处查看领养信息", "领养成功", JOptionPane.INFORMATION_MESSAGE);
                        }else if(state==2){
                            JOptionPane.showMessageDialog(null,
                                    "正在等待审核中...", "办理领养失败", JOptionPane.WARNING_MESSAGE);
                        }else{
                            JOptionPane.showMessageDialog(null,
                                    "该申请已被驳回", "办理领养失败", JOptionPane.WARNING_MESSAGE);
                        }


                        try {
                            setTableData(db_ctrl,ApplyTable,ApplyInfoScrollPane,claimant_id);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }

                    }

                }
            }

        }
    });
    BackButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            cardLayout.show(cardPanel,CLAIMANT_PANEL);
            frame.setVisible(true);
        }
    });
}
    public void setTableData(DB_controller db_ctrl, JTable ApplyTable,
                             JScrollPane ApplyInfoScrollPane,int claimant_id) throws SQLException {
        ((DefaultTableModel)ApplyTable.getModel()).getDataVector().clear();

        String[] columnName={"申请id","宠物昵称","宠物品种","申请时间","审核管理员","申请状态"};
        ArrayList<ApplyInfo_Claimant> res = db_ctrl.browse_apply_info_claimant(claimant_id);

        String[][]tableData = new String[res.size()][6];
        for(int i = 0; i< res.size(); i++){
            tableData[i][0] = res.get(i).apply_id;
            tableData[i][1] = res.get(i).pet_name;
            tableData[i][2] = res.get(i).pet_type;
            tableData[i][3] = res.get(i).apply_time;
            tableData[i][4] = res.get(i).staff_name;
            tableData[i][5] = res.get(i).apply_state;
        }

        DefaultTableModel newView = new DefaultTableModel(tableData,columnName){
            @Override
            public boolean isCellEditable(int row,int col){
                return false;
            }
        };

        ApplyTable.setModel(newView);
        ApplyInfoScrollPane.setViewportView(ApplyTable);

    }

    public JPanel getPanel() {
        return ApplyInfoTabelPanel;
    }
}
