/*jtable�� ���÷� ������ ���� ��Ʈ�ѷ�*/

package com.ss.oracle;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;
	

public class MyModel extends AbstractTableModel{
	Vector columnName; //�÷��� ������ ���� ����
	Vector<Vector> list; //���ڵ带 ���� ������ ����
	
	public MyModel(Vector columnName, Vector list) { //�����Ҷ� rs�� ���� ����
		this.columnName=columnName;
		this.list=list;
	}
	
	public int getColumnCount() {
		return columnName.size();
	}
	
	//column�̸� set
	public String getColumnName(int column) {
		return (String)columnName.elementAt(column);
	}

	public int getRowCount() {
		return list.size();
	}

	//row, col�� ��ġ�� ���� ���������ϰ� �ϴ� �޼ҵ带 �������̵�
	public boolean isCellEditable(int row, int col) {
		boolean flag=false; //0��° column�� primary key�� ���Ǵ� seq�� ���� �ȵǰ� ����
		if(col==0){
			flag=false;
		}else {
			flag=true;
		}
		
		return flag;
	}
	
	public void setValueAt(Object Value, int row, int col) {
		//��, ȣ���� �����Ѵ�.
		Vector vec=list.get(row);
		vec.set(col, Value);		
		
		this.fireTableCellUpdated(row, col);
	}
	
	public Object getValueAt(int row, int col) {
		//���������Ͱ� �;��Ѵ�.
		Vector vec=list.get(row);
		
		return vec.elementAt(col);
	}

}
