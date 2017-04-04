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
	JPanel p_west; // ���� �����
	JPanel p_content; // ���� ���� ��ü
	JPanel p_north; // ���� ���� ��� ����
	JPanel p_center; // flowlayout ���� p_table�� p_grid�� �����ϸ鼭 ��ư �����Ҷ����� �����ִ°� �޶���
	JPanel p_table; // jTable�� �ٿ��� �г�, ������ borderlayout
	JPanel p_grid; // flowlayout
	Choice ch_top;
	Choice ch_sub;
	JTextField t_name;
	JTextField t_price;
	Canvas can;
	JButton bt_regist;
	CheckboxGroup group;
	Checkbox ch_table, ch_grid;
	Toolkit kit = Toolkit.getDefaultToolkit(); // ������ singleton����
	Image img;
	JFileChooser chooser;
	File file;

	DBManager manager = DBManager.getInstance();
	Connection con;

	// html option���� �ٸ��Ƿ�, choice ������Ʈ�� ���� �̸��޾Ƶд�.
	// String[][] subcategory; ������ �迭�� DB data�� �迭�� ���� �ʴ´�
	// ��� DTO�� �÷��� �����ӿ�ũ�� �̿��Ѵ�.
	/*
	 * ���÷����� rs��ü�� ��ü�� ���̴� �׷����ν� ��� ���� ���̻� ra.last, rs.getRow ������� ����
	 */
	ArrayList<SubCategory> subCategory = new ArrayList<SubCategory>();

	public BookMain() {
		p_west = new JPanel();
		p_content = new JPanel();
		p_north = new JPanel();
		p_center = new JPanel();
		// p_table=new TablePanel(con); //�̽��������� con�� �������� �ʾ���. �׷��Ƿ� �޼ҵ�� ����
		p_table = new TablePanel(); // �̽��������� con�� �������� �ʾ���. �׷��Ƿ� �޼ҵ�� ����
		p_grid = new GridPanel();

		ch_top = new Choice();
		ch_sub = new Choice();
		t_name = new JTextField(10);
		t_price = new JTextField(10);
		can = new Canvas() { // �̹��������Ϸ��� canvas, jpanel��� toolkit, icon,
								// imageio�̿�
			public void paint(Graphics g) {
				// ImageIO.read(URL); //���ø����̼� ���� �̹����� �����ٶ� �ַ� ����.
				// ����ڰ� �̹����� ���Ƿ� �ø����� toolkit�� ����.

				g.drawImage(img, 0, 0, 140, 140, this);
			}
		};
		can.setPreferredSize(new Dimension(150, 150));// ĵ���� ũ�Ⱑ �־�� �׸��� ���´�.
		bt_regist = new JButton("���");
		group = new CheckboxGroup();
		ch_table = new Checkbox("���̺�", group, true);
		ch_grid = new Checkbox("�׸���", group, false);
		chooser = new JFileChooser("E:/git/java_workspace2/project_day037_0404/res/");

		URL url = this.getClass().getResource("/pica2.jpg");
		try {
			img = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// ���̽� ������Ʈ�� ũ�� �� ����
		ch_top.setPreferredSize(new Dimension(130, 45));
		ch_sub.setPreferredSize(new Dimension(130, 45));

		ch_top.add("�������");
		ch_sub.add("�������");

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
		can.addMouseListener(new MouseAdapter() { // tor�� �����°Ծƴϴ�.
			public void mouseClicked(MouseEvent e) {
				System.out.println("Ŭ���ߴ�?");
				openFile();
			}
		});
		// img�� can�� actionlistener�� �ȸ����Ƿ� mouseListener�̿�
		// �������̵� �ؾ��ϴ°� �ʹ� �����Ƿ� adaptor�� �����͸�Ŭ������ �̿�

		bt_regist.addActionListener(this);

		// ���̽� ������Ʈ�� �����ʿ���
		ch_table.addItemListener(this);
		ch_grid.addItemListener(this);

		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void init() {
		// ���̽� ������Ʈ�� �ֻ��� ��� ���̱�
		con = manager.getConnection();

		String sql = "select * from top_category order by top_category_id asc";
		// ����(order by)�� �־��ִ� ���� ��¼����� ȥ���� ���µ��� ����.

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

		// ���̺� �гΰ� �׸��� �гο��� Connection����
		// p_table.setConncetion(con) �ڽĿ��� �޼ҵ尡 �ִ�. �׷��Ƿ� �ڽ����� �����ؾ� �Ѵ�.
		((TablePanel) p_table).setConnection(con);// �ڽ������� ����
		((GridPanel) p_grid).setConnection(con);// �ڽ������� ����

	}

	// ���� ī�װ� ��������
	public void getSub(String v) {
		/*
		 * String sql="select * from subcategory ";
		 * sql+="where topcategory_id=(" �̷������� ������ ����� �߰��Ҷ����� String ��ü�� ���������
		 * ������ ������ �����Ҷ� �ַ� StringBuffer�� ����. ���� ���������� for�� ������ �ȵȴ�.
		 */

		// ������ �̹� ä���� �������� �ִٸ� ���� �� �����.
		ch_sub.removeAll();

		StringBuffer sb = new StringBuffer();// ������������ ���� ����.
		sb.append("select * from sub_category ");// �Ǹ������� ��ĭ ���� �Ѵ�.
		// ������ ����(order by)�� ���̴� ���� ���̴°��� ȥ���� ���µ��� ����.
		sb.append("where top_category_id=(");
		sb.append("select top_category_id from ");// �Ǹ������� ��ĭ ���� �Ѵ�.
		sb.append("top_category where top_category_name='" + v + "') "); // �Ǹ������� ��ĭ
																	// ���� �Ѵ�
		sb.append("order by sub_category_id asc");

		System.out.println(sb.toString());// ��ϼ��������� consoleâ�� ������ �������� toad��
											// ������ �ùٸ��� �Է��ߴ��� Ȯ�ΰ���

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = con.prepareStatement(sb.toString());
			// sb�� StringBuffer ���̹Ƿ� String������ �ٲ���� �Ѵ�.
			rs = pstmt.executeQuery();

			/*
			 * while (rs.next()) { ch_sub.add(rs.getString("category_name")); }
			 */
			// ���� ī�װ��� ������ 2���� �迭�� ���+���
			// subcategory=new String[3][2];

			// DBA: ���� entity(��ü)�� ����.
			// table�� Ʋ(Class)�̰� �ϳ��� row�� record�� instance��
			// table�� �ϳ���������� table�� ǥ���� class�� �ϳ��� ������!
			// �Ӽ��� ���������, ���ڵ带 �ν��Ͻ��� ǥ���Ѵ�. VO, DTO!!

			// rs���̿��ϸ� db�� data���� heap������ random�� ��ġ�� ����ȴ�.
			// rs.last();�� Ŀ���� ������ �Ĺ������� �̵��ؼ� recourd���� column���� ���ϴ� ���
			// DTO�� ArrayList�� �̿��Ѵ�.

			/* rs�� ����� ���ڵ� 1���� subcategoryŬ������ �ν��Ͻ�1���� ���� */
			// �Ʒ��� ���� �۾��� mapping�̶�� �Ѵ�.
			while (rs.next()) {
				SubCategory dto = new SubCategory();
				/*
				 * int subcategory_id=rs.getInt("subcategory_id");
				 * dto.setSubcategory_id(subcategory_id);
				 */
				dto.setSubcategory_id(rs.getInt("sub_category_id")); // �ش��ڷ������� �޴°��� ����
				dto.setCategory_name(rs.getString("sub_category_name"));
				dto.setTopcategory_id(rs.getInt("top_category_id"));

				subCategory.add(dto);// �÷��ǿ� ���!!
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

	// ��ǰ��� �޼ҵ�
	public void regist() {
		/*
		 * ���� ���� ������ ����ī�װ� ���̽��� index�� ���ؼ�, �� index�� ArrayList�� �����Ͽ� ��ü�� ��ȯ������
		 * ������ �����ϰ� �� �� �ִ�.
		 */
		int index = ch_sub.getSelectedIndex();
		SubCategory dto = subCategory.get(index); // subCategory ArrayList����
													// index�� �̿��ؼ� �ش� record��
													// �ҷ��´�.

		String book_name = t_name.getText();// å�̸�
		int price = Integer.parseInt(t_price.getText());
		// ������ String�̵ǹǷ� getText�� �޾Ƶ� ������ ���߿� ����ϰ��ϰų� �Ҷ��� ���� �ڷ����� ��Ȯ�����ִ� ���� ����
		String img = file.getName(); // ���ϸ�

		StringBuffer sb = new StringBuffer();
		sb.append("insert into book(book_id, sub_category_id, book_name, book_price, book_image)");
		// img��ü�� oracle db�ȿ� Blob������ ������ ������, oracle�� ��ιǷ� �������̳�, �̹����� �ϵ��ũ��
		// url ��η� �����ϴ°��� ����.
		sb.append("values(seq_book.nextVal, " + dto.getSubcategory_id() + ", '" + book_name + "', " + price + ", '"
				+ img + "')");

		// html�� option���� �ٸ��Ƿ�, choice ������Ʈ�� ���� �̸� �޾Ƶ���.

		System.out.println(sb.toString());// ���� �������� toad���� �����غ���

		PreparedStatement pstmt = null;

		try {
			pstmt = con.prepareStatement(sb.toString());

			// SQl���� DML(insert, delete, update)�� ���
			int result = pstmt.executeUpdate();
			// ���� �޼ҵ�� ���ڰ��� ��ȯ�ϸ�, �� ���ڰ��� �� ������ ���� ����޴� ���ڵ��� ���� ��ȯ�ȴ�.
			// insert�� ��� ������ 1�� ��ȯ�ȴ�.
			if (result != 0) {
				// System.out.println(book_name+"��ϼ���");
				copy();
				
				//((TablePanel)p_table).table.setModel(((TablePanel)p_table).model);
				((TablePanel)p_table).init(); //�ѹ��� ��ȸ ����Ű��
				((TablePanel)p_table).table.updateUI(); //UI����
				//���� �ΰ��� �ϸ� ������ �Ǿ���� �׷��Ƿ� vector���� ������ �Ѵ�.
				
				
				int leng=((GridPanel)p_grid).list.size();
				for (int i = 0; i <leng; i++) { //���⿡ size�� ���� ����� ���߿� size�� �۾�����. �׸��� 0��°�� ������ �Ѵ�.
					((GridPanel)p_grid).list.remove(0);
				}			
				
				((GridPanel)p_grid).loadData();; //�ѹ��� ��ȸ ����Ű�� ���⼱ loaddata���� �ϰ� �ִ�.
				((GridPanel)p_grid).updateUI(); //UI����
				
				
			} else {
				System.out.println(book_name + "��Ͻ���");
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
			// ������ �̹����� canvas�� �׸����̴�.
			file = chooser.getSelectedFile(); // ��������� �Ǿ�� �ٸ��޼ҵ忡�� ��밡��
			img = kit.getImage(file.getAbsolutePath());

			can.repaint();
		}

	}

	// �̹����� ���� �õ��ϴ°��� �̹����� ���� ��쿡 �������� ���Ἲ�� ���� ����
	// �̹����� ������ insert�Ѱ��� �ѹ��ϰų� �ƿ� insert�� �ؼ��� �ȵȴ�.
	/*
	 * �̹��� �����ϱ� ������ ������ �̹�����, �����ڰ� ������ ��ġ�� ���縦 �س���
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

			int data; // ���⼭�� �о�帰 �����Ͱ� x, ������ ������ ����ִ�.
			byte[] b = new byte[1024]; // 1kb�� �аڴ�. ������ ���� ����������. 2^x�� �ַ� ����.
			while (true) {
				data = fis.read(b);
				if (data == -1)
					break;
				fos.write(b);
			}
			JOptionPane.showMessageDialog(this, "��ϿϷ�");

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
			Choice ch = (Choice) e.getSource(); // checkbox�� �޾ƾ� �ϱ� ������ ����
			getSub(ch.getSelectedItem());
		} else if (obj == ch_table) {
			System.out.println("���̺���");
			p_table.setVisible(true);
			p_grid.setVisible(false);
			// 3���̻��̸� for������ ������.

		} else if (obj == ch_grid) {
			System.out.println("�׸��庼��");
			p_table.setVisible(false);
			p_grid.setVisible(true);
		}
	}

	public void actionPerformed(ActionEvent e) {
		System.out.println("������?");
		// e.getSource(); //��ư�� �ƴ϶� �̹����� �ü� �ֱ⶧���� object�� �޾ƾ� �Ѵ�.
		regist();
	}

	public static void main(String[] args) {
		new BookMain();
	}

}
