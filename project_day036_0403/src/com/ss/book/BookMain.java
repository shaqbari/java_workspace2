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
	JPanel p_west; //���� �����
	JPanel p_content; //���� ���� ��ü
	JPanel p_north; //���� ���� ��� ����
	JPanel p_table; //jTable�� �ٿ��� �г�
	JPanel p_grid;
	Choice ch_top;
	Choice ch_sub;
	JTextField t_name;
	JTextField t_price;
	Canvas can;
	JButton bt_regist;
	CheckboxGroup group;
	Checkbox ch_table, ch_grid;
	Toolkit kit=Toolkit.getDefaultToolkit(); //������ singleton����
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
		can=new Canvas(){ //�̹��������Ϸ��� canvas, jpanel��� toolkit, icon, imageio�̿�
			public void paint(Graphics g) {
				//ImageIO.read(URL); //���ø����̼� ���� �̹����� �����ٶ� �ַ� ����.
				//����ڰ� �̹����� ���Ƿ� �ø����� toolkit�� ����.
				
				g.drawImage(img, 0, 0, 140, 140, this);
			}
		};
		can.setPreferredSize(new Dimension(150, 150));//ĵ���� ũ�Ⱑ �־�� �׸��� ���´�.
		bt_regist=new JButton("���");
		group=new CheckboxGroup();
		ch_table=new Checkbox("���̺�", group, true);
		ch_grid=new Checkbox("�׸���", group, false);	
		chooser=new JFileChooser("E:/git/java_workspace2/project_day036_0403/res/");		
		
		URL url=this.getClass().getResource("/pica2.jpg");
		try {
			img=ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		//���̽� ������Ʈ�� ũ�� �� ����
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
		can.addMouseListener(new MouseAdapter(){ //tor�� �����°Ծƴϴ�.
			public void mouseClicked(MouseEvent e) {
				System.out.println("Ŭ���ߴ�?");
				openFile();
			}			
		});
		//img�� can�� actionlistener�� �ȸ����Ƿ� mouseListener�̿�
		//�������̵� �ؾ��ϴ°� �ʹ� �����Ƿ� adaptor�� �����͸�Ŭ������ �̿�
		
		
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void init() {
		//���̽� ������Ʈ�� �ֻ��� ��� ���̱�		
		con=manager.getConnection();		

		String sql="select * from topcategory";
		//*��� category_name�ְ� ��� ���� �� index��1���ϰų� �÷��� �ִ´�.
		
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
				}	���⼭�� row���̸� ���ʿ䰡 ����.		*/	
			
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
	
	//���� ī�װ� ��������
	public void getSub(String v){
		/*String sql="select * from subcategory ";
		 * sql+="where topcategory_id=("
		 * �̷������� ������ ����� �߰��Ҷ����� String ��ü�� ��������� ������
		 * ������ �����Ҷ� �ַ� StringBuffer�� ����.
		 * ���� ���������� for�� ������ �ȵȴ�.
		 * */
		
		//������ �̹� ä���� �������� �ִٸ� ���� �� �����.
		ch_sub.removeAll();
		
		StringBuffer sb=new StringBuffer();//������������ ���� ����.
		sb.append("select * from subcategory ");//�Ǹ������� ��ĭ ���� �Ѵ�.
		sb.append("where topcategory_id=(");
		sb.append("select topcategory_id from ");//�Ǹ������� ��ĭ ���� �Ѵ�.
		sb.append("topcategory where category_name='"+v+"')");
		
		System.out.println(sb.toString());//��ϼ��������� consoleâ�� ������ �������� toad�� ������ �ùٸ��� �Է��ߴ��� Ȯ�ΰ���
		
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
			//������ �̹����� canvas�� �׸����̴�.
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
		System.out.println("������?");
		//e.getSource(); //��ư�� �ƴ϶� �̹����� �ü� �ֱ⶧���� object�� �޾ƾ� �Ѵ�.
	}
	
	public static void main(String[] args) {
		new BookMain();
	}



}
