package org.ftpclient.model;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MyDefaultTableCellRenderer extends DefaultTableCellRenderer {
    private final ImageIcon folderIcon = new ImageIcon("resource/folderIcon.png");
    private final ImageIcon fileIcon = new ImageIcon("resource/file.png");
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        if(value != null){
            if(value instanceof  FileModel){
                FileModel fileModel = (FileModel)value;
                setIcon(getSmallIcon(fileModel.getFile()));// 设置图片
                setText(fileModel.getName());// 设置文本
            }else if(value instanceof RemoteFileModel){
                try {
                    RemoteFileModel fileModel = (RemoteFileModel) value;
                    File tempfile = null;
                    tempfile = File.createTempFile("tempfile_",fileModel.getName());
                    Icon icon = getSmallIcon(tempfile);
                    String txt = fileModel.getName(); // 从节点读取文本
                    if(tempfile!=null){
                        tempfile.delete();
                    }
                    setIcon(icon);
                    if(fileModel.getFile().isDirectory()){
                        setIcon(folderIcon);
                    }
                    setText(fileModel.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
