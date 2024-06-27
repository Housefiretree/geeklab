package swing;

import Main.DB_controller;
import Tables.ApplyInfo;
import Tables.PetInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import static Main.ImageManager.applyImage;
import static Main.Main.cardLayout;
import static Main.Main.cardPanel;
import static Main.StringManager.*;

public class ReviewApplyTable {
    private JPanel ReviewApplyTablePanel;
    private JLabel ApplyLabel;
    private JScrollPane ApplyScrollPane;
    private JTable ApplyTable;
    private JButton ReviewButton;
    private JButton BackButton;


    public ReviewApplyTable(JFrame frame, DB_controller db_ctrl,int staff_id) throws SQLException {


        setTableData(db_ctrl,ApplyTable,ApplyScrollPane,staff_id);

        ApplyLabel.setIcon(applyImage);
        ReviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = ApplyTable.getSelectedRow();
                if(row==-1){
                    JOptionPane.showMessageDialog(null,
                            "未选中审核信息", "Warning", JOptionPane.WARNING_MESSAGE);
                }else{
                    ArrayList<ApplyInfo> resu;
                    try {
                        resu = db_ctrl.browse_apply_info_staff(staff_id);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                    String apply_id = resu.get(row).apply_id;

                    String[] options = { "通过", "驳回", "取消" };

                    // 创建一个选项对话框并显示
                    int result = JOptionPane.showOptionDialog(null, "对于该申请：", "审核申请", JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                    // 处理用户选择
                    if (result == JOptionPane.YES_OPTION) {

                        boolean yes = false;
                        try {
                            yes = db_ctrl.check_pet_apply_pass(apply_id);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }


                        if(yes){
                            JOptionPane.showMessageDialog(null,
                                    "该宠物已经有其他申请通过，该申请不允许通过", "Warning", JOptionPane.WARNING_MESSAGE);
                        }else{
                            try {
                                db_ctrl.review_apply(apply_id,true);
                                setTableData(db_ctrl,ApplyTable,ApplyScrollPane,staff_id);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        }


                    }else if(result == JOptionPane.NO_OPTION){
                        //System.out.println("驳回");
                        try {
                            db_ctrl.review_apply(apply_id,false);
                            setTableData(db_ctrl,ApplyTable,ApplyScrollPane,staff_id);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }

                    }else{
                        //System.out.println("取消");
                        //do nothing就行
                    }
                }

            }
        });
        BackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel,STAFF_PANEL);
                frame.setVisible(true);
            }
        });
    }
    public void setTableData(DB_controller db_ctrl,JTable ApplyTable,
                             JScrollPane ApplyScrollPane,int staff_id) throws SQLException {
        ((DefaultTableModel)ApplyTable.getModel()).getDataVector().clear();

        String[] columnName={"申请id","申请人","宠物昵称","宠物品种","拥有申请数","申请时间"};
        ArrayList<ApplyInfo> res = db_ctrl.browse_apply_info_staff(staff_id);
        String[][]tableData = new String[res.size()][6];
        for(int i = 0; i< res.size(); i++){
            tableData[i][0] = res.get(i).apply_id;
            tableData[i][1] = res.get(i).claimant_name;
            tableData[i][2] = res.get(i).pet_name;
            tableData[i][3] = res.get(i).pet_type;
            tableData[i][4] = res.get(i).apply_count;
            tableData[i][5] = res.get(i).apply_time;
        }

        DefaultTableModel newView = new DefaultTableModel(tableData,columnName){
            @Override
            public boolean isCellEditable(int row,int col){
                return false;
            }
        };

        ApplyTable.setModel(newView);
        ApplyScrollPane.setViewportView(ApplyTable);
    }

    public JPanel getPanel() {
        return ReviewApplyTablePanel;
    }


}
