/*jtable이 얹혀질 패널*/

package com.ss.homework;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.print.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class TablePanel extends JPanel {
	Connection con;
	TableModel model;//인터페이스다.
	JTable table;
	JScrollPane scroll;

	// Vector arrayList는 둘다 같다
	// 차이점? 동기화 지원 여부
	Vector list = new Vector();
	Vector<Vector> book = new Vector<Vector>();
	Vector<String> columnName = new Vector<String>();

	int cols;
	String[] colsName;

	public TablePanel() {		
		table = new JTable();
		scroll = new JScrollPane(table);

		this.setLayout(new BorderLayout());

		add(scroll);

		setPreferredSize(new Dimension(650, 550));
		setBackground(Color.PINK);
	}

	public void setConnection(Connection con) {
		this.con = con;
		init(); // con 얻어온뒤에야 sql실행

		model = new AbstractTableModel() {

			public int getRowCount() {
				return list.size();
			}

			public int getColumnCount() {
				return cols;
			}

			public String getColumnName(int index) {
				return colsName[index];
			}

			public Object getValueAt(int rowIndex, int columnIndex) {
				// Book book=list.get(rowIndex);

				// return book.get(rowIndex).get(columnIndex);
				Vector vec = (Vector) list.get(rowIndex);
				return vec.elementAt(columnIndex);
			}
		};

		table.setModel(model);
	}

	public void init() {
		String sql = "select * from book order by book_id asc";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			cols = rs.getMetaData().getColumnCount();
			
			//기존 데이터 모두 지우기
			list.removeAll(list);//갱신때(새로 db조회할때 ) 기존의 list지워야 누적이 안된다.
			
			colsName = new String[cols];
			for (int i = 0; i < cols; i++) {
				colsName[i] = rs.getMetaData().getColumnName(i + 1);
			}

			/*
			 * while (rs.next()) { //rs의 정보를 커렉션의 dto로 옮겨담자 //Book dto = new
			 * Book(); //dto.s
			 * 
			 * // list.add(rs.getInt("book_id"));
			 * list.add(rs.getString("book_name"));
			 * list.add(rs.getInt("book_price"));
			 * list.add(rs.getInt("book_img"));
			 * list.add(rs.getInt("subcategory_id"));
			 * 
			 * //이차원벡터로 만들어 출력하자. book.add(list); //list.add(dto); }
			 */

			// Book dto=new Book();

			while (rs.next()) {
				Vector<String> data = new Vector<String>();

				data.add(Integer.toString(rs.getInt("book_id")));
				data.add(rs.getString("book_name"));
				data.add(Integer.toString(rs.getInt("book_price")));
				data.add(rs.getString("book_image"));
				data.add(Integer.toString(rs.getInt("sub_category_id")));

				list.add(data);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

	}

}
