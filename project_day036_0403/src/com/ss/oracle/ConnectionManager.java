/*각 TableModel마다 접속정보와 접속객체를 두게 되면, 접속정보가 바뀔때
 * 모든 클래스의 코드도 수정해야 하는 유지보수상의 문제 뿐만 아니라,
 * 각 TableModel마다 Connection을 생성하기 때문에 접속이 여러개 발생한다.
 * 하나의 어플리케이션이 오라클과 맺는 접속은 1개만으로도 충분하다.
 * 그리고 접속이 여러개이면 하나의 세션에서 발생시키는 각종 DML작업이
 * 통일되지 못하게 된다. 즉 다른 사람으로 인식된다.
 * 
 * 이번은 데이터를 받고 바로 접속을 종료 close 하기 때문에 큰 문제는 없지만
 * 나중에 접속을 열어둘때는 아래가 꼭 필요하다.
 * 
 * 객체의 인스터스를 메모리 힙에 1개만 만드는 방법
 * */

package com.ss.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	static private ConnectionManager instance;
	
	String driver="oracle.jdbc.driver.OracleDriver"; //라이브러리는 패키지마다 따로해주는것이 lib에 연결된 해당패키지가 날라갔을때를 대비해서 좋다.
	String url="jdbc:oracle:thin:@sist108:1521:XE";
	String user="batman";
	String password="1234";
	
	Connection con;
	
	//개발자가 제공하는 방법 이외의 접근은 아예 차단하자!! 
	//사용자에 의한 임의 생성을 막자. 즉 new 막자!
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
	
	/*인스턴스의 생성 없이도, 외부에서 메소드를 호출하여 이 객체의 인스턴스를
	가져갈 수 있도록 getter를 제공해준다.*/
	static public ConnectionManager getInstance() {
		if(instance==null){
			instance=new ConnectionManager();			
		}		
		return instance;
	}
	
	//이 메소드의 호출자는 Connection 객체를 반환받게 된다.
	public Connection getConnection(){
		return con;	//한번만 생성된 ConnectionManager의 con을 가져갈수 있다.
		
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
