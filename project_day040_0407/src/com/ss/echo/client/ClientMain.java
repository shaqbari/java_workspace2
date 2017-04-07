package com.ss.echo.client;

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

public class ClientMain extends JFrame implements ItemListener, ActionListener{
	JPanel p_north;
	Choice choice;
	JTextField t_port;
	JButton bt_connect;
	JTextArea area;
	JScrollPane scroll;
	JTextField t_input;
	int port=7777;
	
    DBManager manager;
    ArrayList<Chat> chatList=new ArrayList<Chat>(); //chat table의 정보를 담을 cf
    Socket socket;//대화용 소켓 !! 따라서 스트림도 뽑아낼것이다.
    String ip;

    ClientThread ct;
    
	public ClientMain() {
		p_north=new JPanel();
		choice=new Choice();
		t_port=new JTextField(Integer.toString(port), 10);
		bt_connect=new JButton("접속");
		area=new JTextArea(20, 20);
		scroll=new JScrollPane(area);
		t_input=new JTextField(20);
		manager=DBManager.getInstance();
				
		p_north.add(choice);
		p_north.add(t_port);
		p_north.add(bt_connect);
		
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		add(t_input, BorderLayout.SOUTH);
		
		loadIp();//화면에 보이기 직전 db연동해 ip정보를 가져온다.
		for (int i = 0; i < chatList.size(); i++) {
			choice.add(chatList.get(i).getName());
			
		}
		
		choice.addItemListener(this);
		bt_connect.addActionListener(this);
		t_input.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {
				int key=e.getKeyCode();
				if (key==KeyEvent.VK_ENTER) {
					String msg=t_input.getText();
										
					ct.send(msg);//보내기					
					t_input.setText("");//입력한 글씨 지우기					
					//listen();//받기 동새이 대신듣는다.
				}
				
			}
		});
		
		this.setTitle("211.238.142.98");		
		setBounds(300, 100, 300, 400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}	

	//데이터베이스 가져오기
	public void loadIp(){
		Connection con=manager.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
					
		String sql="select * from chat order by chat_id asc";
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			//rs의 모든 데이터를 dto로 옮기는 과정.. mapping이라고 한다. mybatis등을 이용하면 편하다.
			while (rs.next()) {
				Chat dto=new Chat();
				dto.setChat_id(rs.getInt("chat_id"));
				dto.setName(rs.getString("name"));
				dto.setIp(rs.getString("ip"));
				
				chatList.add(dto);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			manager.disConnect(con); //접속종료
			
		}
		
		
	}

	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		int index=ch.getSelectedIndex();
		Chat chat=chatList.get(index);
		
		this.setTitle(chat.getIp());
		ip=chat.getIp();//멤버변수에도 대입!
	}
	
	//서버에 접속을 시도하자
	public void connect(){
		//소켓 생성시 접속이 발생한다.
		try {
			port=Integer.parseInt(t_port.getText());
			socket=new Socket(ip, port);	
			
			/*//대화를 나누기 전에 스트림 얻어두기!
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));*/
			
			//실시간으로 서버의 메세지를 청취하기 위해, 쓰레드를 생성하여 대화업무를 다 맡겨버리자
			//따라서 종이컵&실의 보유자는 동생!
			ct=new ClientThread(socket, area); //멤버변수로 바꾸자
			ct.start();
			
			//send("안녕?\n");
			ct.send("안녕?");
			
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}

	/*//서버에 메시지 보내기!(말하기)
	public void send(String msg){
		try {
			buffw.write(msg+"\n");
			buffw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}//말하고 듣는거는 다 동생에게 맡기자.*/
	
	/*//서버에서 메시지 받아오기!(듣기) 
		public void listen(){
			String msg=null;
			
			try {
				msg=buffr.readLine();
				area.append(msg+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}//동생쓰레드에 맡기자*/

	
	public void actionPerformed(ActionEvent e) {
		connect();
	}
	
	public static void main(String[] args) {
		new ClientMain();
	}
}
