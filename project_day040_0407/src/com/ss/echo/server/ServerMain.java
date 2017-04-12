package com.ss.echo.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerMain extends JFrame implements ActionListener{
	JPanel p_north;
	JTextField t_port;
	JButton bt_start;
	JTextArea area;
	JScrollPane scroll;
	
	int port=7777;
	ServerSocket server; //접속 감지용 소켓
	Thread thread;//서버 가동용 쓰레드!
	BufferedReader buffr;
	BufferedWriter buffw;
	
	public ServerMain() {
		p_north=new JPanel();
		t_port=new JTextField(Integer.toString(port), 15);
		bt_start=new JButton("가동");
		area=new JTextArea(20, 20);
		scroll=new JScrollPane(area);
		
		p_north.add(t_port);
		p_north.add(bt_start);
		
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);
		
		//setSize(300, 500);
		//setLocationRelativeTo(null);		
		setBounds(600, 100, 300, 400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);	
		
	}
	
	//서버 생성 및 가동
	public void startServer(){	
		bt_start.setEnabled(false);//버튼 비활성화! 여러번 눌러서 쓰레드 엉키는거 방지 //null을 이용해도된다.
		
		/*예외의 종류 -checked Exeption 예외처리 강요
		
						 -runtime Exeption 예외처리 강요x 개발자가 알아서
		*/
		
		try {//컴파일러에서 강요하지 않지만 누군가 숫자가 아닌걸 입력하면 비정상종료가 되어버리므로 try catch문을 이용하자
			port=Integer.parseInt(t_port.getText()); //try catch를 강요하지 않지만 예외처리를 해보자
			server=new ServerSocket(port);
			area.append("서버 준비됨..\n");
			//가동
			
			//실행부라 불리는 메인 쓰레드는 절대 무한루프나 대기, 지연 상태에 빠지게 해서는 안된다.
			//왜? 실행부는 유저들의 이벤트를 감지하거나, 프로그램 운영을 해야 하므로
			//무한루프나, 대기에 빠지면, 본연의 역할을 할 수 없게 된다.
			//스마트폰 개발분야에서는 이와같은 코드는 컴파일타임부터 에러발생한다.
			
			
			Socket socket=server.accept();  //메인스레드에 read() 나 accept()같은 무한루프나 무한대기 상태의 메소드를 쓰면 안된다.!!
			//종이컵으로 받는다.
			area.append("서버 가동.\n");
			
			//클라이언트는 대화를 하기 위해 접속한 것이므로, 접속이 되는 순간 스트림을 얻어놓자
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//클라이언트의 메세지 받기
			String data;
			
			while(true){
				//받기
				data=buffr.readLine(); //보내는 쪽에서 \n을 입력해야하고, 계속 듣고 있어야 한다.
				area.append("클라이언트의 말:"+data+"\n"); //area에서 보기 좋게 줄바꿈\n
				
				//보내기
				buffw.write(data+"\n");
				buffw.flush();//버퍼 비우기!
			}
			
		} catch (NumberFormatException e) {//unchecked exception(runtime exception) 개발자가 해도되고 안해도 되고, 강요하지 않는 예외
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "포트는 숫자로 넣어주세요");
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
	}
	
	public void actionPerformed(ActionEvent e) {		
		thread=new Thread(){
			public void run() {
				startServer();
			}			
		};
		thread.start();
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}

	
	
}
