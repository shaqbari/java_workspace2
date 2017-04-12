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
	ServerSocket server; //���� ������ ����
	Thread thread;//���� ������ ������!
	BufferedReader buffr;
	BufferedWriter buffw;
	
	public ServerMain() {
		p_north=new JPanel();
		t_port=new JTextField(Integer.toString(port), 15);
		bt_start=new JButton("����");
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
	
	//���� ���� �� ����
	public void startServer(){	
		bt_start.setEnabled(false);//��ư ��Ȱ��ȭ! ������ ������ ������ ��Ű�°� ���� //null�� �̿��ص��ȴ�.
		
		/*������ ���� -checked Exeption ����ó�� ����
		
						 -runtime Exeption ����ó�� ����x �����ڰ� �˾Ƽ�
		*/
		
		try {//�����Ϸ����� �������� ������ ������ ���ڰ� �ƴѰ� �Է��ϸ� ���������ᰡ �Ǿ�����Ƿ� try catch���� �̿�����
			port=Integer.parseInt(t_port.getText()); //try catch�� �������� ������ ����ó���� �غ���
			server=new ServerSocket(port);
			area.append("���� �غ��..\n");
			//����
			
			//����ζ� �Ҹ��� ���� ������� ���� ���ѷ����� ���, ���� ���¿� ������ �ؼ��� �ȵȴ�.
			//��? ����δ� �������� �̺�Ʈ�� �����ϰų�, ���α׷� ��� �ؾ� �ϹǷ�
			//���ѷ�����, ��⿡ ������, ������ ������ �� �� ���� �ȴ�.
			//����Ʈ�� ���ߺо߿����� �̿Ͱ��� �ڵ�� ������Ÿ�Ӻ��� �����߻��Ѵ�.
			
			
			Socket socket=server.accept();  //���ν����忡 read() �� accept()���� ���ѷ����� ���Ѵ�� ������ �޼ҵ带 ���� �ȵȴ�.!!
			//���������� �޴´�.
			area.append("���� ����.\n");
			
			//Ŭ���̾�Ʈ�� ��ȭ�� �ϱ� ���� ������ ���̹Ƿ�, ������ �Ǵ� ���� ��Ʈ���� ������
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//Ŭ���̾�Ʈ�� �޼��� �ޱ�
			String data;
			
			while(true){
				//�ޱ�
				data=buffr.readLine(); //������ �ʿ��� \n�� �Է��ؾ��ϰ�, ��� ��� �־�� �Ѵ�.
				area.append("Ŭ���̾�Ʈ�� ��:"+data+"\n"); //area���� ���� ���� �ٹٲ�\n
				
				//������
				buffw.write(data+"\n");
				buffw.flush();//���� ����!
			}
			
		} catch (NumberFormatException e) {//unchecked exception(runtime exception) �����ڰ� �ص��ǰ� ���ص� �ǰ�, �������� �ʴ� ����
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "��Ʈ�� ���ڷ� �־��ּ���");
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
