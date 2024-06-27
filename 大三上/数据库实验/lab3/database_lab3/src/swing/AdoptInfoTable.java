package swing;

import Main.DB_controller;
import Tables.AdoptInfo_Claimant;
import Tables.ApplyInfo_Claimant;

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

public class AdoptInfoTable {
    private JPanel AdoptInfoTablePanel;
    private JLabel AdoptInfoLabel;
    private JScrollPane AdoptInfoScrollPane;
    private JTable AdoptTable;
    private JButton UpdateButton;
    private JButton BackButton;


    public AdoptInfoTable(JFrame frame, DB_controller db_ctrl,int claimant_id) throws SQLException {

        setTableData(db_ctrl,AdoptTable,AdoptInfoScrollPane,claimant_id);

        AdoptInfoLabel.setIcon(rabbitImage);

        UpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = AdoptTable.getSelectedRow();
                if(row==-1){
                    JOptionPane.showMessageDialog(null,
                            "未选中领养信息", "Warning", JOptionPane.WARNING_MESSAGE);
                }else{
                    int result = JOptionPane.showConfirmDialog(UpdateButton,
                            "是否确定更新该宠物的领养信息？");
                    if (JOptionPane.YES_OPTION == result && row != -1){
                        ArrayList<AdoptInfo_Claimant> resu;
                        //model.removeRow(row);
                        try {
                            resu = db_ctrl.browse_adopt_info_claimant(claimant_id);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        AdoptInfo_Claimant adopt = resu.get(row);
                        String adopt_id = adopt.adopt_id;

                        String new_feedback = JOptionPane.showInputDialog(null, "请更新领养信息");
                        if(new_feedback.length()==0){
                            JOptionPane.showMessageDialog(null,
                                    "领养信息为空！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                        }else{
                            try {
                                db_ctrl.update_adopt_feedback(adopt_id,new_feedback);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }


                            try {
                                setTableData(db_ctrl,AdoptTable,AdoptInfoScrollPane,claimant_id);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                    }else{
                        //do nothing
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


    public void setTableData(DB_controller db_ctrl, JTable AdoptTable,
                                          JScrollPane AdoptInfoScrollPane, int claimant_id) throws SQLException {
        ((DefaultTableModel)AdoptTable.getModel()).getDataVector().clear();

        String[] columnName={"领养id","宠物昵称","领养时间","领养情况"};
        ArrayList<AdoptInfo_Claimant> res = db_ctrl.browse_adopt_info_claimant(claimant_id);

        String[][]tableData = new String[res.size()][4];
        for(int i = 0; i< res.size(); i++){
            tableData[i][0] = res.get(i).adopt_id;
            tableData[i][1] = res.get(i).pet_name;
            tableData[i][2] = res.get(i).adopt_time;
            tableData[i][3] = res.get(i).adopt_feedback;
        }

        DefaultTableModel newView = new DefaultTableModel(tableData,columnName){
            @Override
            public boolean isCellEditable(int row,int col){
                return false;
            }
        };

        AdoptTable.setModel(newView);
        AdoptInfoScrollPane.setViewportView(AdoptTable);

    }


    public JPanel getPanel() {
        return AdoptInfoTablePanel;
    }
}
