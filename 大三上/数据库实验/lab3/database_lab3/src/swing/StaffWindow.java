package swing;

import Main.DB_controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import static Main.ImageManager.welcomeStaffImage;
import static Main.Main.cardLayout;
import static Main.Main.cardPanel;
import static Main.StringManager.*;

public class StaffWindow {
    private JPanel StaffPanel;
    private JLabel StaffLabel;
    private JButton PetContrlButton;
    private JButton ApplyControlButton;
    private JButton TypeButton;
    private JButton ExitButton;


    public StaffWindow(JFrame frame, DB_controller db_ctrl,int staff_id) throws SQLException {

        StaffLabel.setIcon(welcomeStaffImage);

        //db_ctrl.DB_close();
        PetContrlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //进入宠物信息管理界面
                System.out.println("进入宠物信息管理界面");

                PetInfoTableStaff staff_pet = null;
                try {
                    staff_pet = new PetInfoTableStaff(frame,db_ctrl);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                    cardPanel.add(STAFF_PET_PANEL,staff_pet.getPanel());
                    cardLayout.show(cardPanel,STAFF_PET_PANEL);
                    frame.setVisible(true);
            }
        });
        ApplyControlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //进入审核申请界面
                System.out.println("进入审核申请界面");
                ReviewApplyTable apply_review = null;
                try {
                    apply_review = new ReviewApplyTable(frame,db_ctrl,staff_id);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                cardPanel.add(apply_review.getPanel());
                cardLayout.last(cardPanel);
                frame.setVisible(true);
            }
        });
        TypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //品种信息管理界面
                System.out.println("进入品种信息管理界面");
                PetTypeTable type_table = null;
                try {
                    type_table = new PetTypeTable(frame,db_ctrl);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                cardPanel.add(type_table.getPanel());
                cardLayout.last(cardPanel);
                frame.setVisible(true);
            }
        });
        ExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    db_ctrl.DB_close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);
            }
        });
    }

    public JPanel getPanel() {
        return StaffPanel;
    }
}
