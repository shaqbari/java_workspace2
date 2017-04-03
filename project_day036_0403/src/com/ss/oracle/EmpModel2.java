//emp 테이블의 데이터를 처리하는 컨트롤러
//원래는 로직을 분리해야하지만 그건 나중에 배운다.

package com.ss.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class EmpModel2 extends AbstractTableModel{ //그냥테이블모델은 override할게 너무 많다.
	ConnectionManager manager;
	Connection con;
	//프로그램이 1개이면 접속도 1개이어야 한다. 접속이 여러개면 수정사항생겼을때 프로그램이 불안해진다.
	//connection이 model의 생성자에 있으면 안좋다.
	
/*	String driver="oracle.jdbc.driver.OracleDriver"; //라이브러리는 패키지마다 따로해주는것이 lib에 연결된 해당패키지가 날라갔을때를 대비해서 좋다.
	String url="jdbc:oracle:thin:@sist108:1521:XE";
	String user="batman";
	String password="1234";*/
	
	
	//Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	
	String[] column; //컬럼을 넣을 배열
	String[][] data; //레코드를 넣을 배열
	
	public EmpModel2(Connection con) {
		//manager=ConnectionManager.getInstance();
		this.con=con; //생성자로 appMain에서 얻어오자.
		
		try {
			//1.로드/2.w접속
			//con=manager.getConnection();
			System.out.println("드라이버 접속, 로드 성공");

			if (con!=null) {
				System.out.println("접속 성공");
				String sql="select * from emp";
				
				//3.쿼리문수행
				/*아래의 pstmt에 의해 생성되는 rs는 커서가 자유로울 수 있다.*/
				pstmt=con.prepareStatement(sql,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				
				//결과집합 반환
				rs=pstmt.executeQuery();
				
				//컬럼을 구해보자.!!
				ResultSetMetaData meta=rs.getMetaData();
				int count=meta.getColumnCount();
				column=new String[count];
				//컬럼명을 채우자
				for (int i = 0; i < column.length; i++) {
					column[i]=meta.getColumnName(i+1); //첫번째컬럼은 1부터 시작된다.					
				}
				
				//데이터를 구해보자				
				rs.last(); //제일마지막으로 보냄
				int total=rs.getRow();//레코드 번호 번환				
				rs.beforeFirst(); //처음레코드 이전으로 보냄
				
				//총레코드수를 알았으니 이차원배열을 생성해보자
				data=new String[total][column.length];
				
/*				int index=0;
				while (rs.next()) {//커서를 옮기면서 t/f를 반환함
					for (int i = 0; i < column.length; i++) {
						data[index][i]=rs.getString(i+1);						
					}
					index++;
				}//아래처럼 해도된다.*/
				
				for (int i = 0; i < data.length; i++) {
					rs.next();
					for (int j = 0; j < column.length; j++) {
						data[i][j]=rs.getString(j+1); //자료형이 일치하지 않아도 다 String으로 가져올 수 있다.
						//위는 getString의 인자로 index를 넣은것임
						//rs.getString(column[i]);로 인자로 String을 넣은것임
					}
				}
				
				//처음부터는 정리할것이 잘 안보이니 fm대로 하다가 조금씩 코드를 정리해보자.
				
			} else {
				System.out.println("접속 실패");

			}			
			
		} /*catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("드라이버 로드 실패");
		} */catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//접속 닫기
			if (rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
/*			if (con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}*/
			//con은 앞으로 윈도우창 닫을때 닫는다.
		}
	}
	

	public int getColumnCount() {
		return column.length;
	}
	
	public String getColumnName(int index) {
		return column[index];
	}	

	public int getRowCount() {
		return data.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {//JTable이 호출
		return data[rowIndex][columnIndex];
	}
	
}
