/*Ű���� �Է½ø��� ������ �޽����� ������ �ٽ� �޾ƿ��� ó���ϸ�,
 * ����� ������? Ű���带 ġ�� ������ ������ �޽����� �ǽð��޾� ���� ����.
 * �ذ�å: �̺�Ʈ �߻��� ������� ������ ���ѷ����� ���鼭 ������ �޼�����
 * û���� �� �ִ� ������ ����θ�(������) ������.
 * 
 *  */

package com.ss.echo.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
//����!
import java.net.Socket;

import javax.swing.JTextArea;
public class ClientThread extends Thread{
	boolean flag=true;
	Socket socket; //���������� ����
	BufferedWriter buffw;
    BufferedReader buffr;
    JTextArea area;
    
    public ClientThread(Socket socket, JTextArea area) {
    	this.socket=socket;
    	this.area=area;
    
    	//��ȭ�� ������ ���� ��Ʈ�� ���α�!
		try {
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}		
    	
    }
    
    //������ �޽��� ������!(���ϱ�)
  	public void send(String msg){
  		try {
  			buffw.write(msg+"\n");
  			buffw.flush();
  		} catch (IOException e) {
  			e.printStackTrace();
  		}
  		
  	}
    
	//�������� �޽��� �޾ƿ���!(���)
	public void listen(){
		String msg=null;
		
		try {
			msg=buffr.readLine();
			area.append(msg+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while (flag) {
			
			//����
			listen(); //�ƹ��� ���� �������ص� readLine()���� ���Ѵ�� ���¿� ������.
			
			
		
		}
	}	
}
