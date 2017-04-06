/*�ڹٸ� �̿��Ͽ� ������ ���α׷��� �ۼ��Ѵ�.
 * 
 * */

package com.ss.echo.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {
	ServerSocket server;
	//��ȭ�� ������ ���� ������ �˷��ֱ� ���� ��ü! �� ���� ��ȭ�� �� ������.
	//������ Ŭ���̾�Ʈ�� ã�ƿ��� ��ٸ��Ƿ�, Ŭ���̾�Ʈ�� ����� ��Ʈ��ȣ�� �����ϸ� �ȴ�.
	//��Ʈ��ȣ�� ���ϴµ� ��Ģ�� �����Ӱ� ���ϸ� �ȴ�.
	//����1)0~1023 �̹� �ý����� �����ϰ� �ִ�.
	//����2) ������ ���α׷����� ������
	//����Ŭ 1521, mariadb 3306, web80
	
	int port=7777;
	Socket socket;
	
	public MyServer() {
		try {
			//�ٸ����α׷��� �浹���� �ʰ� ��Ʈ��ȣ�� ���Ѵ�.
			server=new ServerSocket(port);
			System.out.println("���� ����");
			
			//accept() Ŭ���̾�Ʈ�� ������ ��ٸ���.
			//������ ���������� ���Ѵ�� �� ������ ������.
			//��ġ ��Ʈ���� read()�迭�� ����.			
			while (true) {
				socket=server.accept();
				System.out.println("������ �߰�!!");
				
				//������ �̿��Ͽ� �����͸� �ް��� �ϴ� ��쿣 �Է½�Ʈ����, �����͸� �������� �ϴ� ��쿡�� ��½�Ʈ����					
				InputStream is=socket.getInputStream();
				InputStreamReader reader=null;
				reader=new InputStreamReader(is); //���׷��̵廡��
				int data;
				
				while (true) {
					//data = is.read(); //1byte �о����
					data = reader.read(); //1byte �о����
					System.out.print((char) data);
					
					//telnet�� ������ɾ� �Է±��̹Ƿ� ��ɳ�������
					//����--> ����ĳ���� -->��Ƽĳ����
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		new MyServer();
	}
}
