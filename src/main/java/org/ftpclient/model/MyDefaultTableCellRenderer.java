package org.ftpclient.model;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MyDefaultTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        Icon icon = ((JLabel) value).getIcon();
        setIcon(icon);// 设置图片
        setText(((JLabel) value).getText());// 设置文本
        return this;
    }
}
