package swing;

import Main.DB_controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import static Main.ImageManager.welcomeClaimantImage;
import static Main.Main.cardLayout;
import static Main.Main.cardPanel;
import static Main.StringManager.*;

public class ClaimantWindow {
    private JPanel ClaimantPanel;
    private JLabel WelcomeLabel;
    private JButton PetInfoButton;
    private JButton ApplyInfoButton;
    private JButton AdoptInfoButton;
    private JButton ExitButton;



    public ClaimantWindow(JFrame frame, DB_controller db_ctrl, String user_id){

        WelcomeLabel.setIcon(welcomeClaimantImage);

        PetInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("进入浏览宠物信息界面");

                PetInfoTableClaimant claimant_pet = null;
                try {
                    claimant_pet = new PetInfoTableClaimant(frame,db_ctrl,user_id);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                cardPanel.add(CLAIMANT_PET_PANEL,claimant_pet.getPanel());
                cardLayout.show(cardPanel,CLAIMANT_PET_PANEL);
                frame.setVisible(true);
            }
        });
        ApplyInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //进入我的申请界面
                boolean check;
                try {
                    check = db_ctrl.check_claimant_contact(user_id);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                if(check){
                    int claimant_id=0;
                    try {
                        claimant_id = db_ctrl.getClaimantID(user_id);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                    ApplyInfoTable apply_table = null;
                    try {
                        apply_table = new ApplyInfoTable(frame,db_ctrl,claimant_id);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    cardPanel.add(CLAIMANT_APPLY_PANEL,apply_table.getPanel());
                    cardLayout.show(cardPanel,CLAIMANT_APPLY_PANEL);
                    frame.setVisible(true);
                }else{
                    JOptionPane.showMessageDialog(null,
                            "未添加联系方式，无法查看申请信息", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        AdoptInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean check;
                try {
                    check = db_ctrl.check_claimant_contact(user_id);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                if(check){
                    int claimant_id=0;
                    try {
                        claimant_id = db_ctrl.getClaimantID(user_id);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                    AdoptInfoTable adopt_table = null;
                    try {
                        adopt_table = new AdoptInfoTable(frame,db_ctrl,claimant_id);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    cardPanel.add(CLAIMANT_ADOPT_PANEL,adopt_table.getPanel());
                    cardLayout.show(cardPanel,CLAIMANT_ADOPT_PANEL);
                    frame.setVisible(true);
                }else{
                    JOptionPane.showMessageDialog(null,
                            "未添加联系方式，无法查看领养信息", "Warning", JOptionPane.WARNING_MESSAGE);
                }

            }
        });
        ExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //退出平台
                try {
                    db_ctrl.DB_close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);
            }
        });
    }

    public JPanel getPanel() {return ClaimantPanel;}
}
