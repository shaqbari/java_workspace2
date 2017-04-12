/*이 클래스는 데이터베이스 테이블 Chat라는 레코드 1건을 담기위한 클래스로서
 * 이러한 목적의 객체를 가리켜 DTO, VO라 한다.
 * 
 * 사물에 존재하는 사물에 대한 표현
 * 언어                          db
 * class          -->   Entity(논리적)   (테이블)
 * instance     -->   레코드
 * 속성              -->   컬럼
 * 
 * */

package com.ss.echo.client;

public class Chat {//==table, 
	private int chat_id;
	private String name;
	private String ip;
	
	public int getChat_id() {
		return chat_id;
	}
	public void setChat_id(int chat_id) {
		this.chat_id = chat_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	
	
}
