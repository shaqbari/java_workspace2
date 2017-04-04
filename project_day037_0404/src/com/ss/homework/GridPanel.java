/*jtable이 얹혀질 패널*/

package com.ss.homework;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GridPanel extends JPanel{
	private Connection con;
	String path="E:/git/java_workspace2/project_day037_0404/data/";
	ArrayList<Book> list = new ArrayList<Book>();
	
	public GridPanel(){
		//loadData(); 타이밍이 맞아야 한다.
		//init(); //디자인 보기위해 억지로 호출한것임
		
		setVisible(false); //tablepanel이 먼저 보여야 한다.
		setPreferredSize(new Dimension(650, 550));
		setBackground(Color.CYAN);		
	}
	
	
	public void setConnection(Connection con) {
		this.con= con;
		loadData();
	}

	public void loadData(){
		String sql="select * from book order by book_id asc";
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery(); //쿼리실행!!	
			
			//기존의 bookitem지우기
			/*this.removeAll();
			for (int i = 0; i < list.size(); i++) {
				list.remove(i);
			} 여기서는 붙기 전이므로 프레임에서 지워보자.*/		
			
			this.removeAll(); //이건 된다.
/*			int leng=list.size(); //list에 들어가기전이다.
			for (int i = 0; i <leng; i++) {
				list.remove(0);
			}*/
			
			while (rs.next()) {
				Book dto = new Book();//레코드 1건 담기 위한 인스턴스
				
				dto.setBook_id(rs.getInt("book_id"));
				dto.setBook_name(rs.getString("book_name"));
				dto.setBook_price(rs.getInt("book_price"));
				dto.setBook_img(rs.getString("book_image"));
				dto.setSubcategory_id(rs.getInt("sub_category_id"));
				
				list.add(dto);
			}
			
			//데이터베이스를 모두 가져왔으므로, 디자인에 반영하자.
			init();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
		}		
		
	}
	
	public void init(){
		for (int i = 0; i < list.size(); i++) {
			Book book=list.get(i);			
			try {
				Image img=ImageIO.read(new File(path+book.getBook_img()));
				String name=book.getBook_name();
				String price=Integer.toString(book.getBook_price());
				
				BookItem item=new BookItem(img, name, price);
				add(item);
						
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Bookmain에서 con을 연결해줘야 한다
		}
	}
}
