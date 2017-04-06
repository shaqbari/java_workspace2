/*jtable이 수시로 정보르 얻어가는 컨트롤러*/

package com.ss.oracle;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;
	

public class MyModel extends AbstractTableModel{
	Vector columnName; //컬럼의 제목을 담을 벡터
	Vector<Vector> list; //레코드를 담을 이차원 벡터
	
	public MyModel(Vector columnName, Vector list) { //생성할때 rs로 부터 받자
		this.columnName=columnName;
		this.list=list;
	}
	
	public int getColumnCount() {
		return columnName.size();
	}
	
	//column이름 set
	public String getColumnName(int column) {
		return (String)columnName.elementAt(column);
	}

	public int getRowCount() {
		return list.size();
	}

	//row, col에 위치한 셀을 편집가능하게 하는 메소드를 오버라이드
	public boolean isCellEditable(int row, int col) {
		boolean flag=false; //0번째 column인 primary key로 사용되는 seq는 편집 안되게 하자
		if(col==0){
			flag=false;
		}else {
			flag=true;
		}
		
		return flag;
	}
	
	public void setValueAt(Object Value, int row, int col) {
		//층, 호수를 변경한다.
		Vector vec=list.get(row);
		vec.set(col, Value);		
		
		this.fireTableCellUpdated(row, col);
	}
	
	public Object getValueAt(int row, int col) {
		//이차원백터가 와야한다.
		Vector vec=list.get(row);
		
		return vec.elementAt(col);
	}

}
