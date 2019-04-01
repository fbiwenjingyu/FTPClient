package org.ftpclient.model;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;

public class MyDefaultTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        if(value != null){
            if(value instanceof  FileModel){
                FileModel fileModel = (FileModel)value;
                setIcon(getSmallIcon(fileModel.getFile()));// 设置图片
                setText(fileModel.getName());// 设置文本
            }

        }

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
