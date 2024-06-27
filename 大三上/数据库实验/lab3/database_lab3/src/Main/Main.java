package Main;

import swing.*;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static Main.StringManager.*;

/**
 * @author 86188
 *
 * 注意分工，DB_controller负责数据库数据处理，swing负责界面显示和交互
 *
 */
public class Main {

    public static final int WINDOW_WIDTH = 666;
    public static final int WINDOW_HEIGHT = 566;

    public static final CardLayout cardLayout = new CardLayout(0,0);
    public static final JPanel cardPanel = new JPanel(cardLayout);


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DB_controller db_ctrl = new DB_controller();

        db_ctrl.DB_open();
        JFrame.setDefaultLookAndFeelDecorated(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("宠物领养平台");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        //设置窗口的大小和位置,居中放置
        frame.setBounds((int)(screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        frame.setBackground(new Color(0,0,0,0));



        frame.add(cardPanel);


        StartWindow start = new StartWindow(frame,db_ctrl);
        cardPanel.add(START_PANEL,start.getPanel());
        frame.setVisible(true);

        //LoginWindow login = new LoginWindow(frame,db_ctrl);
        //cardPanel.add(login.getPanel());
        //frame.setVisible(true);*/
    }
}