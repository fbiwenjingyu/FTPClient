package org.ftpclient.ui;

import org.ftpclient.model.FileModel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Enumeration;

import static javax.swing.tree.TreeSelectionModel.*;

public class FTPClientFrame extends JFrame implements ActionListener {
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 850;
    private File[] roots = File.listRoots();
    JTree tree;
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
        JTextField hostText = new JTextField("localhost",10);
        JTextField usernameText = new JTextField("",10);
        JTextField passwordText = new JTextField("",10);
        JTextField portText = new JTextField("21",5);
        hostLabel.setLabelFor(hostText);
        usernameLabel.setLabelFor(usernameText);
        passwordLabel.setLabelFor(passwordText);
        portLabel.setLabelFor(portText);
        JButton connect = new JButton("快速连接(Q)");
        JTextArea message = new JTextArea(5,120);
        message.setLineWrap(true);
        message.setEditable(false);
        JScrollPane js=new JScrollPane(message);
        js.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        p2.add(js);
       // p1.setLayout(new GridLayout(1,9));
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));
        p1.add(hostLabel);
        p1.add(hostText);
        p1.add(usernameLabel);
        p1.add(usernameText);
        p1.add(passwordLabel);
        p1.add(passwordText);
        p1.add(portLabel);
        p1.add(portText);
        p1.add(connect);

        top.setLayout(new BorderLayout());
        top.add(p1,BorderLayout.NORTH);
        top.add(p2,BorderLayout.CENTER);
        this.add(top,BorderLayout.NORTH);
    }

    private void initCenter(){
        JPanel parentCenter = new JPanel();
        parentCenter.setLayout(new GridLayout(2,2));
        JPanel left = new JPanel();
        left.setLayout(new BorderLayout());
        JPanel right = new JPanel();
        right.setLayout(new BorderLayout());

        JPanel leftOfTop = new JPanel();
        FlowLayout f= (FlowLayout)leftOfTop.getLayout();
        f.setHgap(0);//水平间距
        JComboBox leftbox = new JComboBox();
        JPanel leftOfBottom = new JPanel();

        leftOfBottom.setLayout(new BorderLayout());

        JPanel rightOfTop = new JPanel();
        FlowLayout fRight= (FlowLayout)rightOfTop.getLayout();
        fRight.setHgap(0);//水平间距
        JPanel rightOfBottom = new JPanel();

        rightOfBottom.setLayout(new BorderLayout());




        // 创建根节点
        FileModel rootModel = new FileModel(new File("/"),"计算机");
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootModel,true);

        for(File file : roots){
            FileModel fileModel = new FileModel(file,file.getPath());
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileModel,true);
            rootNode.add(node);
        }


        tree = new JTree(rootNode);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                JTree tree = (JTree) e.getSource();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if(node != null && node.getLevel() >= 1){
                    FileModel file = (FileModel) node.getUserObject();
                    leftbox.addItem(file.getFile().getPath());
                    final File[] subFiles = file.getFile().listFiles();
                    if(subFiles != null && subFiles.length > 0){
                        for(File subFile : subFiles){
                            if(subFile.isDirectory()){
                                FileModel fm = new FileModel(subFile,subFile.getName());
                                DefaultMutableTreeNode chileNode = new DefaultMutableTreeNode(fm,true);
                                node.add(chileNode);
                            }
                        }
                    }
                }
            }
        });

        // 设置树节点可编辑
        tree.setEditable(true);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(600,200));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().add(tree);

        tree.setPreferredSize(scrollPane.getPreferredSize());
        leftOfBottom.add(scrollPane, BorderLayout.CENTER);



        leftOfTop.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel lcoalSite= new JLabel("本地站点:");

        leftbox.setPreferredSize(new Dimension(520,20));
        leftOfTop.add(lcoalSite);
        leftOfTop.add(leftbox);
        left.add(leftOfTop,BorderLayout.NORTH);
        left.add(leftOfBottom,BorderLayout.CENTER);

        if(roots.length >= 1){
            leftbox.addItem(roots[0].getPath());
        }

        leftbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    String item = (String) e.getItem();
                    System.out.println("selected item : " + item);
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
                    visitAllNodes(root,item);
                }


            }
        });


        rightOfTop.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel romoteSite= new JLabel("远程站点:");
        JComboBox boxRight = new JComboBox();
        boxRight.setPreferredSize(new Dimension(520,20));
        rightOfTop.add(romoteSite);
        rightOfTop.add(boxRight);
        right.add(rightOfTop,BorderLayout.NORTH);
        right.add(rightOfBottom,BorderLayout.CENTER);


        JTree treeRight = new JTree();
        treeRight.setShowsRootHandles(true);

        // 设置树节点可编辑
        treeRight.setEditable(true);
        JScrollPane scrollPaneRight = new JScrollPane();
        scrollPaneRight.setPreferredSize(new Dimension(600,200));
        scrollPaneRight.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneRight.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneRight.getViewport().add(treeRight);

        treeRight.setPreferredSize(scrollPane.getPreferredSize());
        rightOfBottom.add(scrollPaneRight, BorderLayout.CENTER);



        parentCenter.add(left);
        parentCenter.add(right);


        JTable letfTable = new JTable();
        JTable rightTable = new JTable();


        JScrollPane leftTablePanel = new JScrollPane(letfTable);
        leftTablePanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        JScrollPane rightTablePanel = new JScrollPane(rightTable);
        rightTablePanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        parentCenter.add(leftTablePanel);
        parentCenter.add(rightTablePanel);


        this.add(parentCenter,BorderLayout.CENTER);







    }

    private void visitAllNodes(DefaultMutableTreeNode node,String item) {
        if(node != null){
            FileModel fileModel = (FileModel) node.getUserObject();
            if(fileModel.getFile().getPath().equals(item)){
                TreePath treePath = new TreePath(node.getPath());
                //tree.expandPath(treePath);
                tree.scrollPathToVisible(treePath);
                return;
            }
            if(node.getChildCount() > 0){
                for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                    DefaultMutableTreeNode n = (DefaultMutableTreeNode)e.nextElement();
                    visitAllNodes(n,item);//若有子节点则再次查找
                }
            }
        }

    }

    private void initBottom(){
        JTree jtree = new JTree();
        JScrollPane scrollPane = new JScrollPane(jtree);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(1200,150));
        tabbedPane.addTab("队列",scrollPane);
        tabbedPane.addTab("已上传",null);
        tabbedPane.addTab("已下载",null);
        this.add(tabbedPane,BorderLayout.SOUTH);


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
