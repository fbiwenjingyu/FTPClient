package org.ftpclient.model;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MyDefaultTreeCellRenderer extends DefaultTreeCellRenderer {
    private final ImageIcon folderIcon = new ImageIcon("resource/folderIcon.png");
    private final ImageIcon fileIcon = new ImageIcon("resource/file.png");
    // 重写该方法
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                row, hasFocus); // 调用父类的该方法
        value = ((DefaultMutableTreeNode)value).getUserObject();
        if(value instanceof FileModel){
            Icon icon = getSmallIcon(((FileModel)value).getFile());// 从节点读取图片
            String txt = ((FileModel)value).getName(); // 从节点读取文本
            setIcon(icon);// 设置图片
            setText(txt);// 设置文本
            return this;
        }else if(value instanceof RemoteFileModel){
            RemoteFileModel file = (RemoteFileModel) value;
            File tempfile = null;
            //try {
//                tempfile = File.createTempFile("tempfile_",file.getName());
//                Icon icon = getSmallIcon(tempfile);
//                String txt = ((RemoteFileModel)value).getName(); // 从节点读取文本
//                if(tempfile!=null){
//                    tempfile.delete();
//                }

                setIcon(folderIcon);// 设置图片
                setText(file.getName());// 设置文本
//            } catch (IOException e) {
//                e.printStackTrace();
//                setIcon(folderIcon);
            //}

            return this;
        }else {
            setIcon(folderIcon);
            setText(value.toString());
            return this;
        }

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

}
