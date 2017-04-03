package com.ss.book;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BookMain extends JFrame implements ItemListener, ActionListener{
	JPanel p_west; //좌측 등록폼
	JPanel p_content; //우측 영역 전체
	JPanel p_north; //우측 선택 모드 영역
	JPanel p_table; //jTable이 붙여질 패널
	JPanel p_grid;
	Choice ch_top;
	Choice ch_sub;
	JTextField t_name;
	JTextField t_price;
	Canvas can;
	JButton bt_regist;
	CheckboxGroup group;
	Checkbox ch_table, ch_grid;
	Toolkit kit=Toolkit.getDefaultToolkit(); //일종의 singleton패턴
	Image img;
	JFileChooser chooser;
	
	DBManager manager=DBManager.getInstance();
	Connection con;	
	
	String[][] topcate={
			
	};
	
	public BookMain() {
		p_west=new JPanel();
		p_content=new JPanel();
		p_north=new JPanel();
		p_table=new JPanel();
		p_grid=new JPanel();
		ch_top=new Choice();
		ch_sub=new Choice();
		t_name=new JTextField(10);
		t_price=new JTextField(10);
		can=new Canvas(){ //이미지삽입하려면 canvas, jpanel등에서 toolkit, icon, imageio이용
			public void paint(Graphics g) {
				//ImageIO.read(URL); //어플리케이션 내의 이미지를 보여줄때 주로 쓴다.
				//사용자가 이미지를 임의로 올릴때는 toolkit를 쓴다.
				
				g.drawImage(img, 0, 0, 140, 140, this);
			}
		};
		can.setPreferredSize(new Dimension(150, 150));//캔버스 크기가 있어야 그림이 나온다.
		bt_regist=new JButton("등록");
		group=new CheckboxGroup();
		ch_table=new Checkbox("테이블", group, true);
		ch_grid=new Checkbox("그리드", group, false);	
		chooser=new JFileChooser("E:/git/java_workspace2/project_day036_0403/res/");		
		
		URL url=this.getClass().getResource("/pica2.jpg");
		try {
			img=ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		//초이스 컴포넌트의 크기 폭 조정
		ch_top.setPreferredSize(new Dimension(130, 45));
		ch_sub.setPreferredSize(new Dimension(130, 45));
				
		ch_top.add("상위목록");
		ch_sub.add("하위목록");	
		
		p_west.setPreferredSize(new Dimension(150, 600));
		p_west.add(ch_top);
		p_west.add(ch_sub);
		p_west.add(t_name);
		p_west.add(t_price);
		p_west.add(can);
		p_west.add(bt_regist);				
				
		p_north.setPreferredSize(new Dimension(650, 100));
		p_north.add(ch_table);
		p_north.add(ch_grid);
		
		p_table.setPreferredSize(new Dimension(650, 500));
		
		p_content.setLayout(new BorderLayout());
		p_content.add(p_north, BorderLayout.NORTH);
		p_content.add(p_table);
		
		add(p_west, BorderLayout.WEST);
		add(p_content);				
		
		init();
		
		ch_top.addItemListener(this);
		can.addMouseListener(new MouseAdapter(){ //tor로 끝나는게아니다.
			public void mouseClicked(MouseEvent e) {
				System.out.println("클릭했니?");
				openFile();
			}			
		});
		//img나 can이 actionlistener가 안먹히므로 mouseListener이용
		//오버라이드 해야하는게 너무 많으므로 adaptor를 내부익명클래스로 이용
		
		
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void init() {
		//초이스 컴포넌트에 최상위 목록 보이기		
		con=manager.getConnection();		

		String sql="select * from topcategory";
		//*대신 category_name넣고 결과 얻을 때 index를1로하거나 컬럼명 넣는다.
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;		
		try {
			pstmt=con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs=pstmt.executeQuery();
			
			while (rs.next()) {
				ch_top.add(rs.getString("category_name"));
			}				
			
/*				rs.last();
				int total=rs.getRow();
				rs.beforeFirst();
				
				for (int i = 0; i < total; i++) {
					rs.next();
					ch_top.add(rs.getString(2));
				}	여기서는 row길이를 알필요가 없다.		*/	
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
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
	
	//하위 카테고리 가져오기
	public void getSub(String v){
		/*String sql="select * from subcategory ";
		 * sql+="where topcategory_id=("
		 * 이런식으로 쿼리문 만들면 추가할때마다 String 객체가 만들어지기 때문에
		 * 쿼리문 조립할때 주로 StringBuffer를 쓴다.
		 * 절대 저런식으로 for문 돌리면 안된다.
		 * */
		
		//기존에 이미 채워진 아이템이 있다면 먼저 싹 지운다.
		ch_sub.removeAll();
		
		StringBuffer sb=new StringBuffer();//쿼리문날릴때 많이 쓴다.
		sb.append("select * from subcategory ");//맨마지막에 한칸 띄어야 한다.
		sb.append("where topcategory_id=(");
		sb.append("select topcategory_id from ");//맨마지막에 한칸 띄어야 한다.
		sb.append("topcategory where category_name='"+v+"')");
		
		System.out.println(sb.toString());//목록선택했을때 console창에 나오는 쿼리문을 toad에 돌려서 올바르게 입력했는지 확인가능
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sb.toString());
			rs=pstmt.executeQuery();
			
			while (rs.next()) {
				ch_sub.add(rs.getString("category_name"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}
		}
	}
	
	public void openFile(){
		int result=chooser.showOpenDialog(this);
		
		if (result==JFileChooser.APPROVE_OPTION) {
			//선택한 이미지를 canvas에 그릴것이다.
			File file=chooser.getSelectedFile();
			img=kit.getImage(file.getAbsolutePath());
			
			can.repaint();
		}
		
	}
	
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		getSub(ch.getSelectedItem());
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("눌렀어?");
		//e.getSource(); //버튼이 아니라 이미지도 올수 있기때문에 object로 받아야 한다.
	}
	
	public static void main(String[] args) {
		new BookMain();
	}



}
