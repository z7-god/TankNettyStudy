package com.mashibing.nettystudy.s03;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientFrame extends Frame {
    TextArea ta = new TextArea();   // 多行文本
    TextField tf = new TextField(); // 单行文本
    public ClientFrame() {
        this.setSize(600, 400);
        this.setLocation(100, 20);
        this.add(ta, BorderLayout.CENTER);
        this.add(tf, BorderLayout.SOUTH);
        this.setVisible(true);
        // 回车触发
        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 字符串发送到服务器，分发给各个客户端
                ta.setText(ta.getText() + tf.getText());
                tf.setText("");
            }
        });
    }

    public static void main(String[] args) {
        new ClientFrame();
    }
}
