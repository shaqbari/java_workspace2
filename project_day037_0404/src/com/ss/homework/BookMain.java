package com.ss.homework;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BookMain extends JFrame implements ItemListener, ActionListener {
	JPanel p_west; // 좌측 등록폼
	JPanel p_content; // 우측 영역 전체
	JPanel p_north; // 우측 선택 모드 영역
	JPanel p_center; // flowlayout 적용 p_table과 p_grid를 포함하면서 버튼 선택할때마다 보여주는게 달라짐
	JPanel p_table; // jTable이 붙여질 패널, 꽉차게 borderlayout
	JPanel p_grid; // flowlayout
	Choice ch_top;
	Choice ch_sub;
	JTextField t_name;
	JTextField t_price;
	Canvas can;
	JButton bt_regist;
	CheckboxGroup group;
	Checkbox ch_table, ch_grid;
	Toolkit kit = Toolkit.getDefaultToolkit(); // 일종의 singleton패턴
	Image img;
	JFileChooser chooser;
	File file;

	DBManager manager = DBManager.getInstance();
	Connection con;

	// html option과는 다르므로, choice 컴포넌트의 값을 미리받아둔다.
	// String[][] subcategory; 앞으로 배열에 DB data를 배열로 담지 않는다
	// 대신 DTO와 컬렉션 프레임워크를 이용한다.
	/*
	 * 이컬렉션은 rs객체를 대체할 것이다 그럼으로써 얻는 장점 더이상 ra.last, rs.getRow 고생하지 말자
	 */
	ArrayList<SubCategory> subCategory = new ArrayList<SubCategory>();

	public BookMain() {
		p_west = new JPanel();
		p_content = new JPanel();
		p_north = new JPanel();
		p_center = new JPanel();
		// p_table=new TablePanel(con); //이시점에서는 con이 생성되지 않았음. 그러므로 메소드로 전달
		p_table = new TablePanel(); // 이시점에서는 con이 생성되지 않았음. 그러므로 메소드로 전달
		p_grid = new GridPanel();

		ch_top = new Choice();
		ch_sub = new Choice();
		t_name = new JTextField(10);
		t_price = new JTextField(10);
		can = new Canvas() { // 이미지삽입하려면 canvas, jpanel등에서 toolkit, icon,
								// imageio이용
			public void paint(Graphics g) {
				// ImageIO.read(URL); //어플리케이션 내의 이미지를 보여줄때 주로 쓴다.
				// 사용자가 이미지를 임의로 올릴때는 toolkit를 쓴다.

				g.drawImage(img, 0, 0, 140, 140, this);
			}
		};
		can.setPreferredSize(new Dimension(150, 150));// 캔버스 크기가 있어야 그림이 나온다.
		bt_regist = new JButton("등록");
		group = new CheckboxGroup();
		ch_table = new Checkbox("테이블", group, true);
		ch_grid = new Checkbox("그리드", group, false);
		chooser = new JFileChooser("E:/git/java_workspace2/project_day037_0404/res/");

		URL url = this.getClass().getResource("/pica2.jpg");
		try {
			img = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 초이스 컴포넌트의 크기 폭 조정
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

		p_north.setPreferredSize(new Dimension(650, 50));
		p_north.add(ch_table);
		p_north.add(ch_grid);

		p_center.setBackground(Color.YELLOW);
		p_center.add(p_table);
		p_center.add(p_grid);

		p_content.setLayout(new BorderLayout());
		p_content.add(p_north, BorderLayout.NORTH);
		p_content.add(p_center);

		add(p_west, BorderLayout.WEST);
		add(p_content);

		init();

		ch_top.addItemListener(this);
		can.addMouseListener(new MouseAdapter() { // tor로 끝나는게아니다.
			public void mouseClicked(MouseEvent e) {
				System.out.println("클릭했니?");
				openFile();
			}
		});
		// img나 can이 actionlistener가 안먹히므로 mouseListener이용
		// 오버라이드 해야하는게 너무 많으므로 adaptor를 내부익명클래스로 이용

		bt_regist.addActionListener(this);

		// 초이스 컴포넌트와 리스너연결
		ch_table.addItemListener(this);
		ch_grid.addItemListener(this);

		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void init() {
		// 초이스 컴포넌트에 최상위 목록 보이기
		con = manager.getConnection();

		String sql = "select * from top_category order by top_category_id asc";
		// 정렬(order by)을 넣어주는 것이 출력순서의 혼란을 막는데에 좋다.

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ch_top.add(rs.getString("top_category_name"));
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

		// 테이블 패널과 그리드 패널에게 Connection전달
		// p_table.setConncetion(con) 자식에게 메소드가 있다. 그러므로 자식으로 변신해야 한다.
		((TablePanel) p_table).setConnection(con);// 자식형으로 변신
		((GridPanel) p_grid).setConnection(con);// 자식형으로 변신

	}

	// 하위 카테고리 가져오기
	public void getSub(String v) {
		/*
		 * String sql="select * from subcategory ";
		 * sql+="where topcategory_id=(" 이런식으로 쿼리문 만들면 추가할때마다 String 객체가 만들어지기
		 * 때문에 쿼리문 조립할때 주로 StringBuffer를 쓴다. 절대 저런식으로 for문 돌리면 안된다.
		 */

		// 기존에 이미 채워진 아이템이 있다면 먼저 싹 지운다.
		ch_sub.removeAll();

		StringBuffer sb = new StringBuffer();// 쿼리문날릴때 많이 쓴다.
		sb.append("select * from sub_category ");// 맨마지막에 한칸 띄어야 한다.
		// 원래는 정렬(order by)을 붙이는 것이 보이는것의 혼란을 막는데에 좋다.
		sb.append("where top_category_id=(");
		sb.append("select top_category_id from ");// 맨마지막에 한칸 띄어야 한다.
		sb.append("top_category where top_category_name='" + v + "') "); // 맨마지막에 한칸
																	// 띄어야 한다
		sb.append("order by sub_category_id asc");

		System.out.println(sb.toString());// 목록선택했을때 console창에 나오는 쿼리문을 toad에
											// 돌려서 올바르게 입력했는지 확인가능

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = con.prepareStatement(sb.toString());
			// sb는 StringBuffer 형이므로 String형으로 바꿔줘야 한다.
			rs = pstmt.executeQuery();

			/*
			 * while (rs.next()) { ch_sub.add(rs.getString("category_name")); }
			 */
			// 서브 카테고리의 정보를 2차원 배열에 담기+출력
			// subcategory=new String[3][2];

			// DBA: 모든걸 entity(개체)로 본다.
			// table은 틀(Class)이고 하나의 row인 record가 instance다
			// table이 하나만들어지면 table을 표현한 class를 하나씩 만들자!
			// 속성을 멤버변수로, 레코드를 인스턴스로 표현한다. VO, DTO!!

			// rs를이용하면 db의 data들은 heap영역의 random한 위치에 저장된다.
			// rs.last();로 커서를 전방향 후방향으로 이동해서 recourd수와 column수를 구하는 대신
			// DTO와 ArrayList를 이용한다.

			/* rs에 담겨진 레코드 1개는 subcategory클래스의 인스턴스1개로 받자 */
			// 아래와 같은 작업을 mapping이라고 한다.
			while (rs.next()) {
				SubCategory dto = new SubCategory();
				/*
				 * int subcategory_id=rs.getInt("subcategory_id");
				 * dto.setSubcategory_id(subcategory_id);
				 */
				dto.setSubcategory_id(rs.getInt("sub_category_id")); // 해당자료형으로 받는것이 좋다
				dto.setCategory_name(rs.getString("sub_category_name"));
				dto.setTopcategory_id(rs.getInt("top_category_id"));

				subCategory.add(dto);// 컬렉션에 담기!!
				ch_sub.add(dto.getCategory_name());
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

	// 상품등록 메소드
	public void regist() {
		/*
		 * 내가 지금 선택한 서브카테고리 초이스의 index를 구해서, 그 index로 ArrayList를 접근하여 객체를 반환받으면
		 * 정보를 유용하게 쓸 수 있다.
		 */
		int index = ch_sub.getSelectedIndex();
		SubCategory dto = subCategory.get(index); // subCategory ArrayList에서
													// index를 이용해서 해당 record를
													// 불러온다.

		String book_name = t_name.getText();// 책이름
		int price = Integer.parseInt(t_price.getText());
		// 어차피 String이되므로 getText로 받아도 되지만 나중에 계산하거하거나 할때를 위해 자료형을 정확히해주는 것이 좋다
		String img = file.getName(); // 파일명

		StringBuffer sb = new StringBuffer();
		sb.append("insert into book(book_id, sub_category_id, book_name, book_price, book_image)");
		// img자체를 oracle db안에 Blob형으로 넣을수 있지만, oracle이 비싸므로 동영상이나, 이미지를 하드디스크나
		// url 경로로 지정하는것이 좋다.
		sb.append("values(seq_book.nextVal, " + dto.getSubcategory_id() + ", '" + book_name + "', " + price + ", '"
				+ img + "')");

		// html의 option과는 다르므로, choice 컴포넌트의 값을 미리 받아두자.

		System.out.println(sb.toString());// 찍힌 쿼리문을 toad에서 실행해보자

		PreparedStatement pstmt = null;

		try {
			pstmt = con.prepareStatement(sb.toString());

			// SQl문이 DML(insert, delete, update)일 경우
			int result = pstmt.executeUpdate();
			// 위의 메소드는 숫자값을 반환하며, 이 숫자값은 이 쿼리에 의해 영향받는 레코드의 수가 반환된다.
			// insert의 경우 언제나 1이 반환된다.
			if (result != 0) {
				// System.out.println(book_name+"등록성공");
				copy();
				
				//((TablePanel)p_table).table.setModel(((TablePanel)p_table).model);
				((TablePanel)p_table).init(); //한번더 조회 일으키기
				((TablePanel)p_table).table.updateUI(); //UI갱신
				//위에 두개만 하면 누적이 되어버림 그러므로 vector에서 지워야 한다.
				
				
				int leng=((GridPanel)p_grid).list.size();
				for (int i = 0; i <leng; i++) { //여기에 size가 오면 지우는 도중에 size가 작아진다. 그리고 0번째를 지워야 한다.
					((GridPanel)p_grid).list.remove(0);
				}			
				
				((GridPanel)p_grid).loadData();; //한번더 조회 일으키기 여기선 loaddata에서 하고 있다.
				((GridPanel)p_grid).updateUI(); //UI갱신
				
				
			} else {
				System.out.println(book_name + "등록실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void openFile() {
		int result = chooser.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			// 선택한 이미지를 canvas에 그릴것이다.
			file = chooser.getSelectedFile(); // 멤버변수가 되어야 다른메소드에서 사용가능
			img = kit.getImage(file.getAbsolutePath());

			can.repaint();
		}

	}

	// 이미지를 먼저 시도하는것이 이미지가 없을 경우에 데이터의 무결성을 위해 좋다
	// 이미지가 없으면 insert한것을 롤백하거나 아예 insert를 해서는 안된다.
	/*
	 * 이미지 복사하기 유저가 선택한 이미지를, 개발자가 지정한 위치로 복사를 해놓자
	 */
	public void copy() {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(file);
			String filename = file.getName();
			fos = new FileOutputStream("E:/git/java_workspace2/project_day037_0404/data/" + filename);

			/*
			 * int data; while (true) { data = fis.read(); if(data==-1) break;
			 * fos.write(data);
			 */

			int data; // 여기서는 읽어드린 데이터가 x, 데이터 갯수만 들어있다.
			byte[] b = new byte[1024]; // 1kb씩 읽겠다. 일종의 버퍼 무지빠르다. 2^x를 주로 쓴다.
			while (true) {
				data = fis.read(b);
				if (data == -1)
					break;
				fos.write(b);
			}
			JOptionPane.showMessageDialog(this, "등록완료");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getSource();
		if (obj == ch_top) {
			Choice ch = (Choice) e.getSource(); // checkbox도 받아야 하기 때문에 위험
			getSub(ch.getSelectedItem());
		} else if (obj == ch_table) {
			System.out.println("테이블볼래");
			p_table.setVisible(true);
			p_grid.setVisible(false);
			// 3개이상이면 for문으로 돌리자.

		} else if (obj == ch_grid) {
			System.out.println("그리드볼래");
			p_table.setVisible(false);
			p_grid.setVisible(true);
		}
	}

	public void actionPerformed(ActionEvent e) {
		System.out.println("눌렀어?");
		// e.getSource(); //버튼이 아니라 이미지도 올수 있기때문에 object로 받아야 한다.
		regist();
	}

	public static void main(String[] args) {
		new BookMain();
	}

}
