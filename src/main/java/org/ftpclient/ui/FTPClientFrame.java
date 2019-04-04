package org.ftpclient.ui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.ftpclient.model.FileModel;
import org.ftpclient.model.MyDefaultTableCellRenderer;
import org.ftpclient.model.MyDefaultTreeCellRenderer;
import org.ftpclient.model.RemoteFileModel;
import org.ftpclient.utils.FtpCliUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import static javax.swing.tree.TreeSelectionModel.*;

public class FTPClientFrame extends JFrame implements ActionListener {
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 850;
    private static final double KB = 1024;
    private static final double MB = 1024 * 1024;
    private static final double GB = 1024 * 1024 * 1024;
    public static final String dateFormat = "yyyy/MM/dd HH:mm:ss";
    public static final SimpleDateFormat simpleDateFormat=new SimpleDateFormat(dateFormat);
    private File[] roots = File.listRoots();
    FTPFile rootFile;
    private static boolean isConnected = false;
    //private JTextArea message;
    private JTextPane message;
    JTree tree;
    JTree treeRight;
    JScrollPane scrollPaneRight = new JScrollPane();
    JTable letfTable = new JTable();
    JTable rightTable = new JTable();
    JComboBox boxRight = new JComboBox();
    FtpCliUtils  ftpCli;
    RemoteFileModel remoteFileModel;
    private static final String[] columnNames  = {"文件名","文件大小","文件类型","最近修改"};
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
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        this.setLayout(new BorderLayout());
        JPanel top = new JPanel();
        JLabel hostLabel = new JLabel("主机(H):");
        JLabel usernameLabel = new JLabel("用户名(U):");
        JLabel passwordLabel = new JLabel("密码(W):");
        JLabel portLabel = new JLabel("端口(P):");
        JTextField hostText = new JTextField("127.0.0.1",10);
        JTextField usernameText = new JTextField("",10);
        JTextField passwordText = new JTextField("",10);
        JTextField portText = new JTextField("21",5);
        hostLabel.setLabelFor(hostText);
        usernameLabel.setLabelFor(usernameText);
        passwordLabel.setLabelFor(passwordText);
        portLabel.setLabelFor(portText);
        JButton connect = new JButton("快速连接(Q)");
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = hostText.getText();
                String username = usernameText.getText();
                String password = passwordText.getText();
                String port = portText.getText().equals("")?"21" : portText.getText();
                if(StringUtils.isEmpty(host)){
                    JOptionPane.showMessageDialog(null,"请输入主机IP地址","",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(StringUtils.isEmpty(username)){
                    JOptionPane.showMessageDialog(null,"请输入用户名","",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(StringUtils.isEmpty(password)){
                    JOptionPane.showMessageDialog(null,"请输入密码","",JOptionPane.ERROR_MESSAGE);
                    return;
                }


                ftpCli = FtpCliUtils.createFtpCliUtils(host, Integer.parseInt(port), username, password);
                try {
                    ftpCli.connect();
                    isConnected = true;
                } catch (IOException e1) {
                    e1.printStackTrace();
                    //message.setText(e1.getMessage());
                    //String text = message.getText();
                   // message.append(text);//这行兰色
                    StyledDocument d=message.getStyledDocument();
                    SimpleAttributeSet attr = new SimpleAttributeSet();
                    StyleConstants.setForeground(attr, Color.RED);
                    try {
                        d.insertString(d.getLength(),e1.getMessage() + "\n",attr);
                    } catch (BadLocationException e2) {
                        e2.printStackTrace();
                    }
//                    message.append("\n");
//                    message.setForeground(Color.RED);
//                    message.append(e1.getMessage());
//                    message.append("\n");
                    isConnected = false;
                }
                if(isConnected){
                    //String text = message.getText();
                    //message.append(text);//这行兰色
                    StyledDocument d=message.getStyledDocument();
                    SimpleAttributeSet attr = new SimpleAttributeSet();
                    StyleConstants.setForeground(attr, Color.BLACK);
                    try {
                        d.insertString(d.getLength(),"状态：  成功连接服务器\n" ,attr);
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
//                    message.append("\n");
//                    message.setForeground(Color.BLACK);
//                    message.append("状态：  成功连接服务器");
//                    message.append("\n");

                    rootFile = new FTPFile();
                    rootFile.setName(ftpCli.getFtpBasePath());
                    //try {
                        //ftpCli = FtpCliUtils.createFtpCliUtils(host, Integer.parseInt(port), username, password);
                        //ftpCli.connect();
                        //TODO
                    //} catch (IOException e1) {
                    //    e1.printStackTrace();
                    //}
                    remoteFileModel = new RemoteFileModel(rootFile,"/",ftpCli.printWorkingDirectory());
                    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(remoteFileModel,true);

                    treeRight = new JTree(rootNode);
                    treeRight.setShowsRootHandles(true);
                    treeRight.setEditable(false);

                    try {

                        FTPFile[] ftpFiles = ftpCli.listDirectories();
                        for (FTPFile f : ftpFiles){
                            RemoteFileModel nodeModel = new RemoteFileModel(f,f.getName(),ftpCli.printWorkingDirectory());
                            DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeModel,true);
                            rootNode.add(node);
                            boxRight.addItem(rootFile.getName());
                            boxRight.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    String item = (String) e.getItem();
                                    System.out.println("selected item : " + item);
                                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
                                    //visitAllNodes(root,item);
                                    displayRightTable(new RemoteFileModel(ftpCli.getFtpFileByPath(item),item),ftpCli);
                                }
                            });
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    treeRight.getSelectionModel().setSelectionMode(SINGLE_TREE_SELECTION);
                    treeRight.addTreeSelectionListener(new TreeSelectionListener() {
                        @Override
                        public void valueChanged(TreeSelectionEvent e) {
                            JTree tree = (JTree) e.getSource();
                            RemoteFileModel file = null;
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                            if(node != null){
                                file = (RemoteFileModel) node.getUserObject();
                            }

                            if(node != null && node.getLevel() >= 0){
                                try {
                                    boolean result = ftpCli.changeWorkDirectory(file.getAbsolutePath());
                                    if(!result){
                                        connect.doClick();
                                    }
                                    System.out.println(result);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                    //connect.doClick();

                                    try {
                                        ftpCli = FtpCliUtils.createFtpCliUtils(host,Integer.parseInt(port),username,password);
                                        ftpCli.connect();
                                        ftpCli.changeWorkDirectory(file.getAbsolutePath());
                                    } catch (IOException e2) {
                                        e2.printStackTrace();
                                    }
                                }
                                displayRightTable(file,ftpCli);
                                boxRight.addItem(file.getAbsolutePath());
                                boxRight.setSelectedIndex(boxRight.getItemCount() - 1);
                                try {
                                    //ftpCli.changeWorkDirectory(file.getFile().getName());
//                                    ftpCli = FtpCliUtils.createFtpCliUtils(host, Integer.parseInt(port), username, password);
//                                    ftpCli.connect();
                                    final FTPFile[] subFiles = ftpCli.listDirectories(file.getAbsolutePath());
                                    if(subFiles != null && subFiles.length > 0){
                                        for(FTPFile subFile : subFiles){
                                            if(subFile.isDirectory()){
//                                                ftpCli = FtpCliUtils.createFtpCliUtils(host, Integer.parseInt(port), username, password);
//                                                ftpCli.connect();
                                                RemoteFileModel fm = new RemoteFileModel(subFile,subFile.getName(),ftpCli.printWorkingDirectory());
                                                DefaultMutableTreeNode chileNode = new DefaultMutableTreeNode(fm,true);
                                                node.add(chileNode);
                                            }
                                        }
                                    }
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });

                    treeRight.setCellRenderer(new MyDefaultTreeCellRenderer());
                    scrollPaneRight.getViewport().add(treeRight);
                }

            }
        });
        //message = new JTextArea(5,120);
        //message = new JTextArea();
        message = new JTextPane();
        message.setPreferredSize(new Dimension(WIDTH - 35,80));
        //message.setLineWrap(true);
        message.setEditable(false);
        JScrollPane js=new JScrollPane(message);
        js.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


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
            FileModel fileModel = new FileModel(file,file.getPath());//注意，这里添加的是磁盘的分区，不能用file.getName()方法，否则方法返回的值为空
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileModel,true);
            rootNode.add(node);
            if(file.isDirectory()){
                //addNode(node,file);
            }
        }


        tree = new JTree(rootNode);
        tree.setShowsRootHandles(true);

        tree.getSelectionModel().setSelectionMode(SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() { //本地树型文件夹鼠标点击事件
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                JTree tree = (JTree) e.getSource();
                FileModel file = null;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if(node != null){
                    file = (FileModel) node.getUserObject();
                }
                //file = (FileModel) node.getUserObject();

                if(node != null && node.getLevel() >= 1){
//                    if(node.getLevel() == 1){
//                        ((DefaultTreeCellRenderer)tree.getCellRenderer()).setOpenIcon(getSmallIcon(new File("C://")));
//                    }else if(node.getLevel()>1){
//                        ((DefaultTreeCellRenderer)tree.getCellRenderer()).setOpenIcon(getSmallIcon(file.getFile()));
//                    }

                    displayLeftTable(file);
                    leftbox.addItem(file.getFile().getPath());
                    leftbox.setSelectedIndex(leftbox.getItemCount() - 1);
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
        tree.setCellRenderer(new MyDefaultTreeCellRenderer());

        // 设置树节点不可编辑
        tree.setEditable(false);
        JScrollPane scrollPane = new JScrollPane();

        scrollPane.setPreferredSize(new Dimension(600,200));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().add(tree);

        leftOfBottom.add(scrollPane, BorderLayout.CENTER);



        leftOfTop.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel lcoalSite= new JLabel("本地站点:");

        leftbox.setPreferredSize(new Dimension(520,20));
//        leftbox.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                String item = (String) e.getItem();
//                System.out.println(item);
//                displayLeftTable(new FileModel(new File(item),new File(item).getName()));
//            }
//        });
        leftOfTop.add(lcoalSite);
        leftOfTop.add(leftbox);
        left.add(leftOfTop,BorderLayout.NORTH);
        left.add(leftOfBottom,BorderLayout.CENTER);

        if(roots.length >= 1){
            leftbox.addItem(roots[0].getPath());
        }

        leftbox.addItemListener(new ItemListener() {//文件夹路径下拉列表框item切换事件
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    String item = (String) e.getItem();
                    System.out.println("selected item : " + item);
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
                    visitAllNodes(root,item);
                    displayLeftTable(new FileModel(new File(item),new File(item).getName()));
                }


            }
        });


        rightOfTop.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel romoteSite= new JLabel("远程站点:");

        boxRight.setPreferredSize(new Dimension(520,20));
        rightOfTop.add(romoteSite);
        rightOfTop.add(boxRight);
        right.add(rightOfTop,BorderLayout.NORTH);
        right.add(rightOfBottom,BorderLayout.CENTER);


        //JTree treeRight = new JTree();
        //treeRight.setShowsRootHandles(true);

        // 设置树节点可编辑
        //treeRight.setEditable(false);
        //JScrollPane scrollPaneRight = new JScrollPane();
        scrollPaneRight.setPreferredSize(new Dimension(600,200));
        scrollPaneRight.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneRight.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneRight.getViewport().add(treeRight);

        //treeRight.setPreferredSize(scrollPaneRight.getPreferredSize());
        rightOfBottom.add(scrollPaneRight, BorderLayout.CENTER);



        parentCenter.add(left);
        parentCenter.add(right);


        //JTable letfTable = new JTable();
        //JTable rightTable = new JTable();



        TableModel leftDataModel = new DefaultTableModel(new String[][] {},columnNames);
        letfTable.setModel(leftDataModel);
        letfTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        letfTable.addMouseListener(new MouseAdapter() {//本地表格单元鼠标双击事件
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    int row =((JTable)e.getSource()).rowAtPoint(e.getPoint()); //获得行位置
                    FileModel cellVal = (FileModel) letfTable.getValueAt(row, 0);
                    System.out.println("clicked cell value is : " + cellVal);
                    if(cellVal != null){
                        if(cellVal.getFile().isFile()){
                            return;
                        }
                        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
                        if(cellVal.getName().equals("..")){
                            File parent = cellVal.getFile().getParentFile();
                            if(parent == null){
                                cellVal = rootModel;
                            }else {
                                cellVal = new FileModel(cellVal.getFile().getParentFile(),cellVal.getFile().getParentFile().getName());
                            }

                        }
                        leftbox.addItem(cellVal.getFile().getPath());
                        leftbox.setSelectedIndex(leftbox.getItemCount() - 1);

                    }

                }
            }
        });


        JScrollPane leftTablePanel = new JScrollPane(letfTable);
        leftTablePanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);


        TableModel rightDataModel = new DefaultTableModel(new String[][] {},columnNames);
        rightTable.setModel(rightDataModel);
        rightTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        rightTable.addMouseListener(new MouseAdapter() {//本地表格单元鼠标双击事件
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    int row =((JTable)e.getSource()).rowAtPoint(e.getPoint()); //获得行位置
                    RemoteFileModel cellVal = (RemoteFileModel) rightDataModel.getValueAt(row, 0);
                    System.out.println("clicked cell value is : " + cellVal);
                    if(cellVal != null){
                        if(cellVal.getFile().isFile()){
                            return;
                        }
                        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeRight.getModel().getRoot();
                        if(cellVal.getName().equals("..")){
                            String parentPath = cellVal.getAbsolutePath().substring(0,cellVal.getAbsolutePath().lastIndexOf("/"));
                            boxRight.addItem(parentPath);
                            boxRight.setSelectedIndex(boxRight.getItemCount() - 1);
                        }else{
                            boxRight.addItem(cellVal.getAbsolutePath());
                            boxRight.setSelectedIndex(boxRight.getItemCount() - 1);
                        }


                    }

                }
            }
        });




        JScrollPane rightTablePanel = new JScrollPane(rightTable);
        rightTablePanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        parentCenter.add(leftTablePanel);
        parentCenter.add(rightTablePanel);


        this.add(parentCenter,BorderLayout.CENTER);












    }

    private void addNode(DefaultMutableTreeNode parentNode, File file) {
        File[] files = file.listFiles();
        if(files != null && files.length > 0){
            for(File f : files){
                if(f.isDirectory()){
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(new FileModel(f, f.getName()),true);
                    parentNode.add(node);
                    addNode(node,f);
                }
            }
        }
    }

    private void displayLeftTable2(FileModel file) {
        File parent = file.getFile();
        File[] files = parent.listFiles();
        Object[][] data = new Object[files.length + 1][4];
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length + 1; i++) {
                if (i == 0) {
                    File f = parent;
                    data[i][0] = new FileModel(f, "..");
                    data[i][1] = getFileSize(f);
                    data[i][2] = f.isDirectory() ? "文件夹" : "文件";
                    data[i][3] = formatDate(f.lastModified());
                } else {
                    File f = files[i - 1];
                    data[i][0] = new FileModel(f, f.getName());
                    data[i][1] = getFileSize(f);
                    data[i][2] = f.isDirectory() ? "文件夹" : "文件";
                    data[i][3] = formatDate(f.lastModified());
                }

            }
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        letfTable.setModel(model);
    }

    private void displayLeftTable(FileModel file) {
        File parent = file.getFile();
        File[] files = parent.listFiles();
        if(file.getFile().getPath().equals("\\")){
            files = roots;
        }
        if(files == null) {
            return;
        }
        Object[][] data = new Object[files.length + 1][4];
        if(files != null && files.length > 0){
            for(int i=0;i<files.length + 1;i++){
                if(i==0){
                    File f = parent;
//                    data[i][0] = new JLabel();
//                    ((JLabel)data[i][0]).setIcon(getSmallIcon(f));
//                    ((JLabel)data[i][0]).setText("..");
                    data[i][0] = new FileModel(f,"..");
                    data[i][1] = getFileSize(f);
                    data[i][2] = f.isDirectory() ? "文件夹":"文件";
                    data[i][3] = formatDate(f.lastModified());
                }else {
                    File f = files[i - 1];
//                    data[i][0] = new JLabel();
//                    ((JLabel)data[i][0]).setIcon(getSmallIcon(f));
//                    ((JLabel)data[i][0]).setText(f.getName());
                    data[i][0] = new FileModel(f,f.getName());
                    if(file.getFile().getPath().equals("\\")){
                        data[i][0] = new FileModel(f,f.getPath());//磁盘分区不能用f.getName()，否则文件名为空
                    }
                    data[i][1] = getFileSize(f);
                    data[i][2] = f.isDirectory() ? "文件夹":"文件";
                    data[i][3] = formatDate(f.lastModified());
                }

            }
        }else{//如果父目录下面没有文件和文件夹
            File f = parent;
//                    data[i][0] = new JLabel();
//                    ((JLabel)data[i][0]).setIcon(getSmallIcon(f));
//                    ((JLabel)data[i][0]).setText("..");
            data[0][0] = new FileModel(f,"..");
            data[0][1] = getFileSize(f);
            data[0][2] = f.isDirectory() ? "文件夹":"文件";
            data[0][3] = formatDate(f.lastModified());
        }
        DefaultTableModel model = new DefaultTableModel(data,columnNames){
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };


        letfTable.setModel(model);

        TableColumnModel columnModel = letfTable.getColumnModel();
        TableColumn column = columnModel.getColumn(0);
        column.setCellRenderer(new MyDefaultTableCellRenderer());
//        letfTable.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if(e.getClickCount() == 2){
//                    int row =((JTable)e.getSource()).rowAtPoint(e.getPoint()); //获得行位置
//                    FileModel cellVal = (FileModel) letfTable.getValueAt(row, 0);
//                    System.out.println("clicked cell value is : " + cellVal);
//                    if(cellVal != null){
//                        displayLeftTable2(cellVal);
//                    }
//
//                }
//            }
//        });
    }

    private void displayRightTable(RemoteFileModel file,FtpCliUtils cliUtils) {
        try {
            FTPFile parent = file.getFile();
            FTPFile[] files = cliUtils.listFiles();
            if(file.getFile().getName().equals("\\")){
                files[0] = rootFile;
            }
            if(files == null) {
                return;
            }
            Object[][] data = new Object[files.length + 1][4];
            if(files.length > 0){
                for(int i=0;i<files.length + 1;i++){
                    if(i==0){
                        FTPFile f = parent;
                        data[i][0] = new RemoteFileModel(f,"..");
                        data[i][1] = getRemoteFileSize(f);
                        data[i][2] = f.isDirectory() ? "文件夹":"文件";
                        //data[i][3] = formatDate(f.getTimestamp().getTimeInMillis());
                        data[i][3] = "";
                    }else {
                        FTPFile f = files[i - 1];
                        data[i][0] = new RemoteFileModel(f,f.getName());
                        if(file.getFile().getName().equals("\\")){
                            data[i][0] = new RemoteFileModel(f,f.getName());//磁盘分区不能用f.getName()，否则文件名为空
                        }
                        data[i][1] = getRemoteFileSize(f);
                        data[i][2] = f.isDirectory() ? "文件夹":"文件";
                        data[i][3] = formatDate(f.getTimestamp().getTimeInMillis());
                    }

                }
            }else{//如果父目录下面没有文件和文件夹
                FTPFile f = parent;
    //                    data[i][0] = new JLabel();
    //                    ((JLabel)data[i][0]).setIcon(getSmallIcon(f));
    //                    ((JLabel)data[i][0]).setText("..");
                data[0][0] = new RemoteFileModel(f,"..");
                data[0][1] = getRemoteFileSize(f);
                data[0][2] = f.isDirectory() ? "文件夹":"文件";
                data[0][3] = formatDate(f.getTimestamp().getTimeInMillis());
            }
            DefaultTableModel model = new DefaultTableModel(data,columnNames){
                public boolean isCellEditable(int row, int column)
                {
                    return false;
                }
            };


            rightTable.setModel(model);

            TableColumnModel columnModel = rightTable.getColumnModel();
            TableColumn column = columnModel.getColumn(0);
            column.setCellRenderer(new MyDefaultTableCellRenderer());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileSize(File f) {
        if(f.isDirectory()) return "";
        long length = f.length();
        long sizeOfGB = (long) (length / GB + 0.5);
        long sizeOfMB = (long) (length / MB + 0.5);
        long sizeOfKB = (long) (length / KB + 0.5);
        if(sizeOfGB > 0) return sizeOfGB + "GB";
        if(sizeOfMB > 0) return  sizeOfMB + "MB";
        if(sizeOfKB > 0) return  sizeOfKB + "KB";
        return length + "B";
    }

    private String getRemoteFileSize(FTPFile f) {
        if(f.isDirectory()) return "";
        long length = f.getSize();
        long sizeOfGB = (long) (length / GB + 0.5);
        long sizeOfMB = (long) (length / MB + 0.5);
        long sizeOfKB = (long) (length / KB + 0.5);
        if(sizeOfGB > 0) return sizeOfGB + "GB";
        if(sizeOfMB > 0) return  sizeOfMB + "MB";
        if(sizeOfKB > 0) return  sizeOfKB + "KB";
        return length + "B";
    }

    private String formatDate(long lastModified) {
       return simpleDateFormat.format(new Date(lastModified));
    }

    private static Icon getSmallIcon( File f )
    {
        if ( f != null && f.exists() )
        {
            FileSystemView fsv = FileSystemView.getFileSystemView();
            return(fsv.getSystemIcon( f ) );
        }
        return(null);
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
            if(ftpCli != null && ftpCli.isConnected()){
                ftpCli.disconnect();
            }
        }

        if(e.getActionCommand().equals("快速连接(Q)")){

        }
    }
}
