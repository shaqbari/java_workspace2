/*키보드 입력시마다 서버에 메시지를 보내고 다시 받아오게 처리하면,
 * 생기는 문제점? 키보드를 치지 않으면 서버의 메시지를 실시간받아 볼수 없다.
 * 해결책: 이벤트 발생과 상관없이 언제나 무한루프를 돌면서 서버의 메세지를
 * 청취할 수 있는 별도의 실행부를(쓰레드) 만들자.
 * 
 *  */

package com.ss.echo.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
//듣자!
import java.net.Socket;

import javax.swing.JTextArea;
public class ClientThread extends Thread{
	boolean flag=true;
	Socket socket; //종이컵으로 받자
	BufferedWriter buffw;
    BufferedReader buffr;
    JTextArea area;
    
    public ClientThread(Socket socket, JTextArea area) {
    	this.socket=socket;
    	this.area=area;
    
    	//대화를 나누기 전에 스트림 얻어두기!
		try {
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}		
    	
    }
    
    //서버에 메시지 보내기!(말하기)
  	public void send(String msg){
  		try {
  			buffw.write(msg+"\n");
  			buffw.flush();
  		} catch (IOException e) {
  			e.printStackTrace();
  		}
  		
  	}
    
	//서버에서 메시지 받아오기!(듣기)
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
			
			//듣자
			listen(); //아무리 빨리 돌리려해도 readLine()에서 무한대기 상태에 빠진다.
			
			
		
		}
	}	
}
