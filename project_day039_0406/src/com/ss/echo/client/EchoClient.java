package com.ss.echo.client;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class EchoClient extends JFrame{
	JTextArea area;
	JScrollPane scroll;
	JPanel p_south;
	JTextField t_input;
	JButton bt_connect;
	
	public EchoClient() {
		area=new JTextArea();
		scroll=new JScrollPane(area);
		p_south=new JPanel();
		t_input=new JTextField(15);
		bt_connect=new JButton("Á¢¼Ó");
		
		p_south.add(t_input);
		p_south.add(bt_connect);
				
		add(scroll);
		add(p_south, BorderLayout.SOUTH);
		
		setSize(300, 400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	public static void main(String[] args) {
		new EchoClient();
		
	}

}
