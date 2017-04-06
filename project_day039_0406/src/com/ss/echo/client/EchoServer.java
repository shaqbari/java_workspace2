/*에코프로그램이란?
 * 클라이언트의 메시지를 그대로 다시 전달하는 방식의 서버
 * 채팅기초 1단계
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
			System.out.println("서버생성");
							
			Socket socket=server.accept();//접속자가 있을때까지 무한대기!! like read()
			InetAddress inet=socket.getInetAddress();
			String ip=inet.getHostAddress();
			System.out.println(ip+"발견");
			
			//클라이언트의 데이터를 받기 위해
			//입력스트림 얻기
			//바이트 -->문자-->버퍼			
			//입력스트림 업그레이드
			BufferedReader buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//출력스트림 업그레이드
			BufferedWriter buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//클라이언트의 말 듣기!
			String msg;
			
			//대화는 계속 가능하지만 실행부가 아래의 while문안에 갇혀있으므로
			//더이상 추가 접속자에 대한 접속허용은 불가하다.
			//결론:최초 가장 빨리 들어온 사람용서버
			while(true){
				//2.클라이언트 듣기
				msg=buffr.readLine(); //데이터 오기전까지 서버가 무한대기에 빠진다.
				System.out.println("클라이언트가 보낸 말:"+msg);
				
				//3.클라이언트에 보내기
				buffw.write("서버가 보낸 말:"+msg+"\n"); //한줄보내기
				buffw.flush();//버퍼비우기
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new EchoServer();
	}
}
