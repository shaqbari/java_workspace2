/*자바를 이용하여 서버측 프로그램을 작성한다.
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
	//대화를 나누기 전에 접속을 알려주기 위한 객체! 즉 아직 대화는 못 나눈다.
	//서버는 클라이언트가 찾아오길 기다리므로, 클라이언트와 약속한 포트번호만 보유하면 된다.
	//포트번호를 정하는데 원칙은 자유롭게 정하면 된다.
	//예외1)0~1023 이미 시스템이 점유하고 있다.
	//예외2) 유명한 프로그램들은 피하자
	//오라클 1521, mariadb 3306, web80
	
	int port=7777;
	Socket socket;
	
	public MyServer() {
		try {
			//다른프로그램과 충돌나지 않게 포트번호를 정한다.
			server=new ServerSocket(port);
			System.out.println("서버 생성");
			
			//accept() 클라이언트의 접속을 기다린다.
			//접속이 있을때까지 무한대기 즉 지연에 빠진다.
			//마치 스트림의 read()계열과 같다.			
			while (true) {
				socket=server.accept();
				System.out.println("접속자 발견!!");
				
				//소켓을 이용하여 데이터를 받고자 하는 경우엔 입력스트림을, 데이터를 보내고자 하는 경우에는 출력스트림을					
				InputStream is=socket.getInputStream();
				InputStreamReader reader=null;
				reader=new InputStreamReader(is); //업그레이드빨때
				int data;
				
				while (true) {
					//data = is.read(); //1byte 읽어들임
					data = reader.read(); //1byte 읽어들임
					System.out.print((char) data);
					
					//telnet은 영문명령어 입력기이므로 욕심내지말자
					//에코--> 유니캐스팅 -->멀티캐스팅
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
