/*1.데이터베이스 접속관련 정보를 여러군데 두지 않기 위함
 *2.싱글톤으로 관리함으로써, 인스턴스를 불필요하게 많이 만들지 않아도 된다.
 *3.싱글턴 안의 connection 멤버로 보유하고 있으므로 Connection을 한번만 생성할 수 있다.
 * 
 * */

package com.ss.book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
	static private DBManager instance;
	private String driver="oracle.jdbc.driver.OracleDriver";
	private String url="jdbc:oracle:thin:@sist108:1521:XE";
	private String user="batman";
	private String password="1234";
	
	Connection con;
	
	private DBManager(){
		try {
			Class.forName(driver);
			con=DriverManager.getConnection(url, user, password);
			
			if(con!=null){
				System.out.println("접속성공");
			}else {
				System.out.println("접속실패");				
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	static public DBManager getInstance() {
		if (instance==null) {
			instance=new DBManager();
		}
		
		return instance;
	}
	
	public Connection getConnection(){
		return con;
	}
	
	public void disConnect(Connection con){
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
