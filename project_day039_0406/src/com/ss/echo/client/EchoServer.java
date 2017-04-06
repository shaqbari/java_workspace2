/*�������α׷��̶�?
 * Ŭ���̾�Ʈ�� �޽����� �״�� �ٽ� �����ϴ� ����� ����
 * ä�ñ��� 1�ܰ�
 * */

package com.ss.echo.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
	ServerSocket server;
	int port=7777;
	
	public EchoServer() {
		try {
			server=new ServerSocket(port);
			System.out.println("��������");
							
			Socket socket=server.accept();//�����ڰ� ���������� ���Ѵ��!! like read()
			InetAddress inet=socket.getInetAddress();
			String ip=inet.getHostAddress();
			System.out.println(ip+"�߰�");
			
			//Ŭ���̾�Ʈ�� �����͸� �ޱ� ����
			//�Է½�Ʈ�� ���
			//����Ʈ -->����-->����			
			//�Է½�Ʈ�� ���׷��̵�
			BufferedReader buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//��½�Ʈ�� ���׷��̵�
			BufferedWriter buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//Ŭ���̾�Ʈ�� �� ���!
			String msg;
			
			//��ȭ�� ��� ���������� ����ΰ� �Ʒ��� while���ȿ� ���������Ƿ�
			//���̻� �߰� �����ڿ� ���� ��������� �Ұ��ϴ�.
			//���:���� ���� ���� ���� ����뼭��
			while(true){
				//2.Ŭ���̾�Ʈ ���
				msg=buffr.readLine(); //������ ���������� ������ ���Ѵ�⿡ ������.
				System.out.println("Ŭ���̾�Ʈ�� ���� ��:"+msg);
				
				//3.Ŭ���̾�Ʈ�� ������
				buffw.write("������ ���� ��:"+msg+"\n"); //���ٺ�����
				buffw.flush();//���ۺ���
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new EchoServer();
	}
}
