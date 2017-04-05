package com.ss.mytry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//싱글톤으로 한번만 만들어지게
public class DBManager {
	static private DBManager instance;	
	private String driver="oracle.jdbc.driver.OracleDriver";
	private String url="jdbc:oracle:thin:@sist108:1521:XE";
	private String user="batman";
	private String password="1234";
	
	Connection con; //접속 후, 그 정보를 담는 객체 //윈도우 창이 열릴때 생성되고, 윈도우 닫힐때 닫힌다.
	
	
	/* 1.드라이버 로드
	 * 2.접속
	 * 3.쿼리실행
	 * 4.닫기
	 * */
	//new막기 위함
	private DBManager(){
				
		try {
			Class.forName(driver);			
			
			this.con=DriverManager.getConnection(url, user, password);
			if (con!=null) {
				System.out.println("접속성공");
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static public DBManager getInstance(){
		if (instance==null) {
			instance=new DBManager();
		}
		
		return instance;		
	}	
	
	//접속
	public Connection getConnection(){
		return con;
	}
	
	//접속해제
	public void disConnect(Connection con){
		if (con!=null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
	}
	
}
