/*접속자 마다 클라이언트와 1:1로 대화를 나눌 쓰레드*/

package com.ss.multi.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JTextArea;

public class ServerThread extends Thread{
	ServerMain main;
	Socket socket;
	BufferedReader buffr;
	BufferedWriter buffw;
	JTextArea area;
	
	Boolean flag=true;
	
	public ServerThread(Socket socket, JTextArea area, ServerMain main) {
		this.main=main;
		this.socket=socket;
		this.area=area;		
		
		try {
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	//클라이언트 메세지 받기
	public void listen(){
						
		try{
			String msg=buffr.readLine();			
			area.append(msg+"\n");
			send(msg); //다시보내기
			
		} catch (IOException e) {//갑자기 client가 나가버리면 이 예외에 계속 걸린다.
			System.out.println("읽기불가");
			flag=false;//현재 쓰레드 죽이기
			
			//벡터에서 이 스레드를 제거
			main.list.remove(this);
			
			main.area.append("1명 퇴장후 현재 접속자"+main.list.size()+"\n");			
			//e.printStackTrace();						
		}		
		
	}
	
	public void send(String msg){
		try {
			//현재 접속한 자 전부에게 보내야 한다.
			//접속한자의 수:
			for (int i = 0; i < main.list.size(); i++) {
				ServerThread st=main.list.elementAt(i);
				st.buffw.write(msg+"\n");
				st.buffw.flush();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		
		while (flag) {
			listen();
		}
	
	}
}
