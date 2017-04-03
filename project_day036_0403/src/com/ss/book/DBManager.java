/*1.�����ͺ��̽� ���Ӱ��� ������ �������� ���� �ʱ� ����
 *2.�̱������� ���������ν�, �ν��Ͻ��� ���ʿ��ϰ� ���� ������ �ʾƵ� �ȴ�.
 *3.�̱��� ���� connection ����� �����ϰ� �����Ƿ� Connection�� �ѹ��� ������ �� �ִ�.
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
				System.out.println("���Ӽ���");
			}else {
				System.out.println("���ӽ���");				
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
