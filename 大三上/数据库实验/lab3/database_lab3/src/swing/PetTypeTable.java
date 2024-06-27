package swing;

import Main.DB_controller;
import Tables.PetInfo;
import Tables.TypeInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import static Main.ImageManager.hamsterImage;
import static Main.Main.cardLayout;
import static Main.Main.cardPanel;
import static Main.StringManager.*;

public class PetTypeTable {
    private JPanel PetTypeTabelPanel;
    private JLabel TypeLabel;
    private JScrollPane PetTypeScrollPane;
    private JTable TypeTabel;
    private JButton AddButton;
    private JButton UpdateButton;
    private JButton BackButton;
    private JButton DeleteButton;


public PetTypeTable(JFrame frame, DB_controller db_ctrl) throws SQLException {


    setTableData(db_ctrl,TypeTabel,PetTypeScrollPane);

    TypeLabel.setIcon(hamsterImage);

    AddButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //直接弹出对话框输入品种名就行了
            String type_name = JOptionPane.showInputDialog(null, "请输入品种名字");
            if(type_name.length()==0){
                JOptionPane.showMessageDialog(null,
                        "品种名为空！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if(type_name.length()>TYPE_NAME_MAX_LENGTH){
                JOptionPane.showMessageDialog(null,
                        "品种名太长！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
            }else{
                System.out.println("添加新品种");
                try {
                    db_ctrl.add_pet_type(type_name);
                    setTableData(db_ctrl,TypeTabel,PetTypeScrollPane);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    });
    UpdateButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //修改成功
            int row = TypeTabel.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(null,
                        "未选中品种信息", "Warning", JOptionPane.WARNING_MESSAGE);
            }else{
                ArrayList<TypeInfo> resu;
                try {
                    resu = db_ctrl.browse_type_info();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                TypeInfo origin = resu.get(row);
                int type_id = Integer.parseInt(origin.pet_type_id);
                String type_name = JOptionPane.showInputDialog(null, "请输入品种名字");
                if(type_name.length()==0){
                    JOptionPane.showMessageDialog(null,
                            "品种名为空！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                }else if(type_name.length()>TYPE_NAME_MAX_LENGTH){
                    JOptionPane.showMessageDialog(null,
                            "品种名太长！请重新填写", "Warning", JOptionPane.WARNING_MESSAGE);
                }else{
                    System.out.println("更新品种");
                    try {
                        db_ctrl.update_pet_type(type_id,type_name);
                        setTableData(db_ctrl,TypeTabel,PetTypeScrollPane);
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
            cardLayout.show(cardPanel,STAFF_PANEL);
            frame.setVisible(true);
        }
    });
    DeleteButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = TypeTabel.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(null,
                        "未选中品种信息", "Warning", JOptionPane.WARNING_MESSAGE);
            }else{
                int result = JOptionPane.showConfirmDialog(DeleteButton,
                        "是否确定删除该品种的信息？");
                if (JOptionPane.YES_OPTION == result && row != -1){
                    ArrayList<TypeInfo> resu;
                    try {
                        resu = db_ctrl.browse_type_info();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    TypeInfo origin = resu.get(row);
                    int type_id = Integer.parseInt(origin.pet_type_id);


                    boolean check;
                    //检查品种下是否有宠物，若有则不许删除
                    try {
                        check = db_ctrl.check_pet_type(type_id);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    if(check){
                        JOptionPane.showMessageDialog(null,
                                "该品种下还有宠物！不允许删除", "Warning", JOptionPane.WARNING_MESSAGE);
                    }else{
                        try {
                            db_ctrl.delete_pet_type(type_id);
                            setTableData(db_ctrl,TypeTabel,PetTypeScrollPane);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }

            }

        }
    });
}

    public void setTableData(DB_controller db_ctrl,JTable TypeTabel,
                             JScrollPane PetTypeScrollPane) throws SQLException {
        ((DefaultTableModel)TypeTabel.getModel()).getDataVector().clear();

        ArrayList<TypeInfo> res_type = db_ctrl.browse_type_info();
        String[] columnName={"品种id","品种名"};
        String[][]tableData = new String[res_type.size()][2];
        for(int i = 0; i< res_type.size(); i++){
            tableData[i][0] = res_type.get(i).pet_type_id;
            tableData[i][1] = res_type.get(i).pet_type_name;
        }

        DefaultTableModel newView = new DefaultTableModel(tableData,columnName){
            @Override
            public boolean isCellEditable(int row,int col){
                return false;
            }
        };

        TypeTabel.setModel(newView);
        PetTypeScrollPane.setViewportView(TypeTabel);
    }

    public JPanel getPanel() {
        return PetTypeTabelPanel;
    }
}
