package com.ss.multi.client;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ss.db.DBManager;
import com.ss.echo.client.Chat;

public class ClientMain extends JFrame implements ItemListener, ActionListener{
	JPanel p_north;
	Choice choice;
	JTextField t_port, t_input;
	JButton bt_connect;
	JTextArea area;
	JScrollPane scroll;
	int port=7777;
	DBManager manager;
	ArrayList<Chat> list=new ArrayList<Chat>();
	String ip;
	Socket socket;
	/*BufferedReader buffr;
	BufferedWriter buffw;*/
	
	ClientThread ct;
	String nickName="shj";
	
	
	public ClientMain() {
		p_north = new JPanel();
		choice = new Choice();
		t_port = new JTextField(Integer.toString(port),5);
		t_input = new JTextField();
		bt_connect = new JButton("접속");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		manager = DBManager.getInstance();
		
		p_north.add(choice);
		p_north.add(t_port);
		p_north.add(bt_connect);
		
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		add(t_input, BorderLayout.SOUTH);
		
		loadIP();
		
		for(int i=0;i<list.size();i++){
			choice.add(list.get(i).getName());
		}
		
		//리스너와 연결!!
		choice.addItemListener(this);
		bt_connect.addActionListener(this);
		
		t_input.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {
				int key=e.getKeyCode();
				
				if(key == KeyEvent.VK_ENTER){
					String msg=t_input.getText();
					
			/*		try {
						buffw.write(msg+"\n");
						buffw.flush();
						
						//쓰레드로 듣는다.
						msg=buffr.readLine();
						area.append(msg+"\n");
						
						t_input.setText("");						
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}*/
					
					
					ct.send(msg); //전송
					t_input.setText("");

				}
			}
		});
		
		
		setBounds(300, 100, 300, 400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//데이터베이스 가져오기!!
	public void loadIP(){
		Connection con=manager.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="select * from chat order by chat_id asc";
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			//rs의 모든 데이터를 dto 로 옮기는 과정..
			while(rs.next()){
				Chat dto = new Chat();
				dto.setChat_id(rs.getInt("chat_id"));
				dto.setName(rs.getString("name"));
				dto.setIp(rs.getString("ip"));
				
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			manager.disConnect(con);
		}
	}
		
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		int index=ch.getSelectedIndex();
		Chat chat=list.get(index);
		this.setTitle(chat.getIp());
		ip=chat.getIp(); //멤버변수에도 대입!!
	}
	
	public void connect(){
		
		try {
			port=Integer.parseInt(t_port.getText());
			socket = new Socket(ip, port);
			
			/*buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));*/
			//대화를 담당할 클라이언트 측의 쓰레드 생성 및 가동
			ct=new ClientThread(this);
			ct.start();
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
	
		connect();
	}
	
	public static void main(String[] args) {
		new ClientMain();
	}

}








