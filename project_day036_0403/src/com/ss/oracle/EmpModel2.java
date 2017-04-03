//emp ���̺��� �����͸� ó���ϴ� ��Ʈ�ѷ�
//������ ������ �и��ؾ������� �װ� ���߿� ����.

package com.ss.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class EmpModel2 extends AbstractTableModel{ //�׳����̺���� override�Ұ� �ʹ� ����.
	ConnectionManager manager;
	Connection con;
	//���α׷��� 1���̸� ���ӵ� 1���̾�� �Ѵ�. ������ �������� �������׻������� ���α׷��� �Ҿ�������.
	//connection�� model�� �����ڿ� ������ ������.
	
/*	String driver="oracle.jdbc.driver.OracleDriver"; //���̺귯���� ��Ű������ �������ִ°��� lib�� ����� �ش���Ű���� ���������� ����ؼ� ����.
	String url="jdbc:oracle:thin:@sist108:1521:XE";
	String user="batman";
	String password="1234";*/
	
	
	//Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	
	String[] column; //�÷��� ���� �迭
	String[][] data; //���ڵ带 ���� �迭
	
	public EmpModel2(Connection con) {
		//manager=ConnectionManager.getInstance();
		this.con=con; //�����ڷ� appMain���� ������.
		
		try {
			//1.�ε�/2.w����
			//con=manager.getConnection();
			System.out.println("����̹� ����, �ε� ����");

			if (con!=null) {
				System.out.println("���� ����");
				String sql="select * from emp";
				
				//3.����������
				/*�Ʒ��� pstmt�� ���� �����Ǵ� rs�� Ŀ���� �����ο� �� �ִ�.*/
				pstmt=con.prepareStatement(sql,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				
				//������� ��ȯ
				rs=pstmt.executeQuery();
				
				//�÷��� ���غ���.!!
				ResultSetMetaData meta=rs.getMetaData();
				int count=meta.getColumnCount();
				column=new String[count];
				//�÷����� ä����
				for (int i = 0; i < column.length; i++) {
					column[i]=meta.getColumnName(i+1); //ù��°�÷��� 1���� ���۵ȴ�.					
				}
				
				//�����͸� ���غ���				
				rs.last(); //���ϸ��������� ����
				int total=rs.getRow();//���ڵ� ��ȣ ��ȯ				
				rs.beforeFirst(); //ó�����ڵ� �������� ����
				
				//�ѷ��ڵ���� �˾����� �������迭�� �����غ���
				data=new String[total][column.length];
				
/*				int index=0;
				while (rs.next()) {//Ŀ���� �ű�鼭 t/f�� ��ȯ��
					for (int i = 0; i < column.length; i++) {
						data[index][i]=rs.getString(i+1);						
					}
					index++;
				}//�Ʒ�ó�� �ص��ȴ�.*/
				
				for (int i = 0; i < data.length; i++) {
					rs.next();
					for (int j = 0; j < column.length; j++) {
						data[i][j]=rs.getString(j+1); //�ڷ����� ��ġ���� �ʾƵ� �� String���� ������ �� �ִ�.
						//���� getString�� ���ڷ� index�� ��������
						//rs.getString(column[i]);�� ���ڷ� String�� ��������
					}
				}
				
				//ó�����ʹ� �����Ұ��� �� �Ⱥ��̴� fm��� �ϴٰ� ���ݾ� �ڵ带 �����غ���.
				
			} else {
				System.out.println("���� ����");

			}			
			
		} /*catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("����̹� �ε� ����");
		} */catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//���� �ݱ�
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
			//con�� ������ ������â ������ �ݴ´�.
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

	public Object getValueAt(int rowIndex, int columnIndex) {//JTable�� ȣ��
		return data[rowIndex][columnIndex];
	}
	
}
