/*�� TableModel���� ���������� ���Ӱ�ü�� �ΰ� �Ǹ�, ���������� �ٲ�
 * ��� Ŭ������ �ڵ嵵 �����ؾ� �ϴ� ������������ ���� �Ӹ� �ƴ϶�,
 * �� TableModel���� Connection�� �����ϱ� ������ ������ ������ �߻��Ѵ�.
 * �ϳ��� ���ø����̼��� ����Ŭ�� �δ� ������ 1�������ε� ����ϴ�.
 * �׸��� ������ �������̸� �ϳ��� ���ǿ��� �߻���Ű�� ���� DML�۾���
 * ���ϵ��� ���ϰ� �ȴ�. �� �ٸ� ������� �νĵȴ�.
 * 
 * �̹��� �����͸� �ް� �ٷ� ������ ���� close �ϱ� ������ ū ������ ������
 * ���߿� ������ ����Ѷ��� �Ʒ��� �� �ʿ��ϴ�.
 * 
 * ��ü�� �ν��ͽ��� �޸� ���� 1���� ����� ���
 * */

package com.ss.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	static private ConnectionManager instance;
	
	String driver="oracle.jdbc.driver.OracleDriver"; //���̺귯���� ��Ű������ �������ִ°��� lib�� ����� �ش���Ű���� ���������� ����ؼ� ����.
	String url="jdbc:oracle:thin:@sist108:1521:XE";
	String user="batman";
	String password="1234";
	
	Connection con;
	
	//�����ڰ� �����ϴ� ��� �̿��� ������ �ƿ� ��������!! 
	//����ڿ� ���� ���� ������ ����. �� new ����!
	private ConnectionManager(){
		try {
			Class.forName(driver);
			this.con=DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*�ν��Ͻ��� ���� ���̵�, �ܺο��� �޼ҵ带 ȣ���Ͽ� �� ��ü�� �ν��Ͻ���
	������ �� �ֵ��� getter�� �������ش�.*/
	static public ConnectionManager getInstance() {
		if(instance==null){
			instance=new ConnectionManager();			
		}		
		return instance;
	}
	
	//�� �޼ҵ��� ȣ���ڴ� Connection ��ü�� ��ȯ�ް� �ȴ�.
	public Connection getConnection(){
		return con;	//�ѹ��� ������ ConnectionManager�� con�� �������� �ִ�.
		
	}
	
	public void disConnect(Connection con){
		if (con!=null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}		
	}
	
}
