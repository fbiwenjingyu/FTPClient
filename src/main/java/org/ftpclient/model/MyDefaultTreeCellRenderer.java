package org.ftpclient.model;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;

public class MyDefaultTreeCellRenderer extends DefaultTreeCellRenderer {
    // 重写该方法
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                row, hasFocus); // 调用父类的该方法
        Icon icon = getSmallIcon(((FileModel)((DefaultMutableTreeNode)value).getUserObject()).getFile());// 从节点读取图片
        String txt = (((FileModel)((DefaultMutableTreeNode)value).getUserObject())).getName(); // 从节点读取文本
        setIcon(icon);// 设置图片
        setText(txt);// 设置文本
        return this;
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
