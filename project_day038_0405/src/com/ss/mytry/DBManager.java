package com.ss.mytry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//�̱������� �ѹ��� ���������
public class DBManager {
	static private DBManager instance;	
	private String driver="oracle.jdbc.driver.OracleDriver";
	private String url="jdbc:oracle:thin:@sist108:1521:XE";
	private String user="batman";
	private String password="1234";
	
	Connection con; //���� ��, �� ������ ��� ��ü //������ â�� ������ �����ǰ�, ������ ������ ������.
	
	
	/* 1.����̹� �ε�
	 * 2.����
	 * 3.��������
	 * 4.�ݱ�
	 * */
	//new���� ����
	private DBManager(){
				
		try {
			Class.forName(driver);			
			
			this.con=DriverManager.getConnection(url, user, password);
			if (con!=null) {
				System.out.println("���Ӽ���");
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
	
	//����
	public Connection getConnection(){
		return con;
	}
	
	//��������
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
