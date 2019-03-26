package org.ftpclient.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class FTPClientFrame extends JFrame implements ActionListener {
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 850;
    public static void main(String[] args) {
        FTPClientFrame frame = new FTPClientFrame();
    }

    public FTPClientFrame(){
        addMenuBar();
        initTop();
        initCenter();
        initBottom();
        setProperties();
    }

    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("文件(F)");
        JMenu edit = new JMenu("编辑(E)");
        JMenu view = new JMenu("查看(V)");
        JMenu transfer = new JMenu("传输(T)");
        JMenu server = new JMenu("服务器(S)");
        JMenu bookmark = new JMenu("书签(B)");
        JMenu help = new JMenu("帮助(H)");
        JMenuItem file_site_manager = new JMenuItem("站点管理器(S)");
        file_site_manager.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        JMenuItem file_site_add = new JMenuItem("添加当前连接到站点管理器(C)");
        file_site_add.setEnabled(false);
        JMenuItem file_new_tab = new JMenuItem("新标签(T)");
        JMenuItem file_close_tab = new JMenuItem("关闭标签(O)");
        JMenuItem file_export = new JMenuItem("导出(E)");
        JMenuItem file_import = new JMenuItem("导入(I)");
        JMenuItem file_display_editfile = new JMenuItem("显示正在被编辑的文件(H)");
        JMenuItem file_exit = new JMenuItem("退出(X)");
        file_exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        file.add(file_site_manager);
        file.add(file_site_add);
        file.add(file_new_tab);
        file.add(file_close_tab);
        file.add(file_export);
        file.add(file_import);
        file.add(file_display_editfile);
        file.add(file_exit);
        file.setMnemonic('F');
        menuBar.add(file);
        menuBar.add(Box.createHorizontalStrut(12));
        menuBar.add(edit);
        menuBar.add(Box.createHorizontalStrut(12));
        menuBar.add(view);
        menuBar.add(Box.createHorizontalStrut(12));
        menuBar.add(transfer);
        menuBar.add(Box.createHorizontalStrut(12));
        menuBar.add(server);
        menuBar.add(Box.createHorizontalStrut(12));
        menuBar.add(bookmark);
        menuBar.add(Box.createHorizontalStrut(12));
        menuBar.add(help);
        this.setJMenuBar(menuBar);
        file_exit.addActionListener(this);
    }

    private void initTop() {
        this.setLayout(new BorderLayout());
        JPanel top = new JPanel();
        JLabel hostLabel = new JLabel("主机(H):");
        JLabel usernameLabel = new JLabel("用户名(U):");
        JLabel passwordLabel = new JLabel("密码(W):");
        JLabel portLabel = new JLabel("端口(P):");
        JTextField hostText = new JTextField();
        JTextField usernameText = new JTextField();
        JTextField passwordText = new JTextField();
        JTextField portText = new JTextField();
        hostLabel.setLabelFor(hostText);
        usernameLabel.setLabelFor(usernameText);
        passwordLabel.setLabelFor(passwordText);
        portLabel.setLabelFor(portText);
        JButton connect = new JButton("快速连接(Q)");
        JTextArea message = new JTextArea();

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        p2.add(message);
        p1.setLayout(new FlowLayout());
        p1.add(hostLabel);
        p1.add(hostText);
        p1.add(usernameLabel);
        p1.add(usernameText);
        p1.add(passwordLabel);
        p1.add(passwordText);
        p1.add(portLabel);
        p1.add(portText);

        top.setLayout(new BorderLayout());
        top.add(p1,BorderLayout.NORTH);
        top.add(p2,BorderLayout.CENTER);
        this.add(top,BorderLayout.NORTH);
    }

    private void initCenter(){

    }

    private void initBottom(){

    }

    private void setProperties(){
        this.setSize(WIDTH,HEIGHT);
        this.setLocationRelativeTo(null);
        ImageIcon icon=new ImageIcon("resource/filezilla.png");
        this.setTitle("FileZilla");
        this.setIconImage(icon.getImage());
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("退出(X)")){
            this.dispose();
        }
    }
}
