/*키입력을 해야 서버의 메시지를 받는 현재의 기능을 보완한다.
 * 무한루프를 돌며서 서버의 메시지를 받을 존재가 필요하며
 * 무한루프를 실행해야 하므로 쓰레드로 정의한다.*/
package com.ss.multi.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JTextArea;

public class ClientThread extends Thread {
	ClientMain clientMain;//클라이언트 프레임 자체를 보유
	Socket socket;
	BufferedReader buffr;
	BufferedWriter buffw;
	JTextArea area;
	
	
	public ClientThread(ClientMain clientMain) {
		this. clientMain=clientMain;
		this.socket=clientMain.socket;
		this.area=clientMain.area;
		
		try {
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void listen(){
		try {
			String msg=buffr.readLine();
			area.append(clientMain.nickName+"의 말: "+msg+"\n");			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void send(String msg){
		try {
			buffw.write(msg+"\n");
			buffw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		while (true) {
			listen();
		}
	}
}
