package com.ss.multi.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerMain extends JFrame implements ActionListener, Runnable{
	JPanel p_north; 
	JTextField t_port;
	JButton bt_start;
	JTextArea area;
	JScrollPane scroll;
	
	int port=7777;
	ServerSocket server;
	Thread thread; //서버 가동용 쓰레드
	Socket socket;
	/*BufferedReader buffr;
	BufferedWriter buffw;
	*/
	//안정성을 위해 arraylist보다 vector를 이용하자
	//멀티캐스팅을 위해서는 현재 서버에 몇명이 들어오고 나가는지 체크할 저장소가 필요하며,
	//유연해야 하므로 컬렉션 계열로 선언하자.
	//Vector<접속자>list=new Vector<접속자>();
	//접속자수만큼 ServerThread가 생기므로 generic으로 이걸 받자.
	Vector<ServerThread>list=new Vector<ServerThread>();
	
	
	public ServerMain() {
		p_north = new JPanel();
		t_port = new JTextField(Integer.toString(port) ,10);
		bt_start = new JButton("가동");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		p_north.add(t_port);
		p_north.add(bt_start);
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);
		
		setBounds(600,100,300,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	//서버를 가동 메소드
	public void startServer(){
		
		try {
			port=Integer.parseInt(t_port.getText());
			server = new ServerSocket(port);
			area.append("서버 생성\n");
			
			while (true) {
				socket = server.accept();
				String ip = socket.getInetAddress().getHostAddress();
				area.append(ip + "접속자 발견\n");
				
				//접속자 마다 쓰레드를 하나씩 할당해서 대화를 나누게 해준다.
				ServerThread st=new ServerThread(socket, area, this);
				st.start();//쓰레드 동작
				
				//접속자가 발견되면, 이 접속자와 대화를 나눌 쓰레드를 Vector 담는다.
				list.add(st);
				area.append("현재 접속자는"+list.size()+" 명\n");
				
			}			
			//위는 while문으로 계속 접속자 받고
			//아래는 thread로 빼서 접속자 마다 독립적으로 대화수행	
			/*buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			String msg=null;
			msg=buffr.readLine();//클라이언트 메세지 청취
			
			buffw.write(msg+"\n");
			buffw.flush();*/
			
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}	
	
	public void actionPerformed(ActionEvent e) {
		thread = new Thread(this);
		thread.start();
	}
	
	public void run() {
		startServer();
		
	}
	
	public static void main(String[] args) {
		new ServerMain();
		
	}

}