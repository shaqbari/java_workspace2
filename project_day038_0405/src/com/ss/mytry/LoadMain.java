package com.ss.mytry;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;

public class LoadMain extends JFrame implements ActionListener, TableModelListener{
	JPanel p_north;
	JTextField t_path;
	JButton bt_open, bt_load, bt_exel, bt_del;
	JTable table;
	JScrollPane scroll;
	
	JFileChooser chooser;
	FileReader reader=null;//������ ������� �� ���ڱ�ݽ�Ʈ��(reader)
	BufferedReader buffer=null;
	
	//������â�� ������ �̹� ������ Ȯ���� ����
	DBManager manager=DBManager.getInstance();
	Connection con;
	Vector columnName;
	Vector<Vector> list;
	
	public LoadMain() {
		p_north= new JPanel();
		t_path=new JTextField(20);
		bt_open= new JButton("���Ͽ���");
		bt_load= new JButton("�ε��ϱ�");
		bt_exel= new JButton("�����ε�");
		bt_del= new JButton("�����ϱ�");
		table=new JTable(3,8);
		//���̺���� �̿��ϸ� cell�� �����ϴ� �͵� tablemodel�� å������ �Ѵ�.
		//�����Ѱ� �ݿ��ϴ°͵� �����ؾ��Ѵ�.
		
		scroll=new JScrollPane(table);
		chooser=new JFileChooser("c:/animal/");
				
		p_north.add(t_path);
		p_north.add(bt_open);
		p_north.add(bt_load);
		p_north.add(bt_exel);
		p_north.add(bt_del);
		
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_open.addActionListener(this);
		bt_load.addActionListener(this);
		bt_exel.addActionListener(this);
		bt_del.addActionListener(this);
		
		//������� ������ ����
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				//�����ͺ��̽� �ڿ� ����
				manager.disConnect(con);
				
				//���μ��� ����
				System.exit(0);
			}			
		});
		
		//���̺� �𵨰� �����ʿ��� ���� ���⼭�� ���� �������� �ʾҴ�
		//table.getModel().addTableModelListener(this);//���羲���ִ� ���� ��ȯ���ش�.
		
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		init();
	}
	
	public void init(){
		//connection ���� ����
		con=manager.getConnection();
		
	}
	
	
	//���� Ž���� ����
	public void open(){
		int result=chooser.showOpenDialog(this);
		//���⸦ ������ �������Ͽ� ��Ʈ���� ����
		if (result==JFileChooser.APPROVE_OPTION) {
			//������ ������ ����
			File file=chooser.getSelectedFile();
			t_path.setText(file.getAbsolutePath());
			try {
				reader=new FileReader(file);
				buffer=new BufferedReader(reader); //���پ� �б� ���� ���׷��̵�
				//������ �ȾƵΰ� �ε��ư�� ������ while���� ������ ����.
				//String data;
				
			/*	while (true) {//��Ʈ��ũ�� �������� �����ϰ� ���������� ������ ������ while���� ������ ������ �����忡�� sleep���� �ӵ��� ����� �Ѵ�.
					data=buffer.readLine();
					if (data==null) break;
					System.out.println(data);
				}*/
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
			//���⼭�� �ݾƼ��� �ȵȴ�.
		}		
	}	
	
	//CSV --> oracle�� ������ ����(migration)�ϱ�
	public void load(){
		//System.out.println(buffer);
		//���۽�Ʈ���� �̿��Ͽ� csv�� �����͸� 1�پ� �о�鿩 insert��Ű��!
		//���ڵ尡 ������ ���� while������ ������ �ʹ� �����Ƿ�, 
		//��Ʈ��ũ�� ������ �� ���� ������ �Ϻη� ������Ű�鼭..
		String data=null;			
		StringBuffer sb=new StringBuffer();	
		PreparedStatement pstmt=null; //sql���ϳ����� ����
		
		try {
			
			while (true) {
				data = buffer.readLine();
				if (data==null) break;					
				//�÷����̵���ִ� ó�������� �����ϱ� ���� ó���� seq�� ���ܽ�Ű��. count�� �Ἥ 0���� ���ܽ�ų���� ������ �����ϱ����ؼ�
				String[] value = data.split(",");// .�� \\(escape)�� �տ� ��������� ,�� Ư�����ڰ� �ƴϴ�.
				if (!value[0].equals("seq")) {//seq���� �����ϰ� insert�ϰڴ�.					
					sb.append("insert into hospital(seq, name, addr, regdate, status, dimension, type)");
					//sb.append(" values(seq, 'name', 'addr', 'regdate', 'status', dimension, 'type')"); //��ĭ �����Ѵ�. 
					//�����Է��Ұſ� Ȭ����ǥ�� ���ΰ� ����Ŭ���� ���� �ֵ���ǥ �߰��ϰ� ++���̿� ���� �Է�
					sb.append(" values("+value[0]+", '"+value[1]+"', '"+value[2]+"', '"+value[3]+"', '"+value[4]+"', "+value[5]+", '"+value[6]+"')");//�տ� ��ĭ ����
					
					System.out.println(sb.toString());					
					pstmt=con.prepareStatement(sb.toString());
					
					int result=pstmt.executeUpdate(); //��������
					//������ ������ StringBuffer�� �����͸� ��� �����
					sb.delete(0, sb.length()); //while���ȿ��� loop���� StringBuffer�� ���Ӱ� new�ص� �ȴ�.
					
					
					//������ �߰��� ,�� ������ ������ ����. ó���ӵ��� ���̰� ���� �������ɼ������Ƿ� �����带 �̿�����.
										
				} else {
					System.out.println("�� 1���̹Ƿ� ����");					
				}
			}
			JOptionPane.showMessageDialog(this, "���̱׷��̼� �Ϸ�");
			
			//jtable ������ ó��
			getList();
			table.setModel(new MyModel(columnName, list));
			
			//���̺� �𵨰� �����ʿ��� ����
			table.getModel().addTableModelListener(this);//���羲���ִ� ���� ��ȯ���ش�.
			
			table.updateUI();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	//�������� �о db�� ���̱׷��̼� �ϱ�
	//javaSE �������� ���̺귯�� �ִ�? X
	//open Source: ��������Ʈ����
	//copyright <->copyleft (����ġ ��ü)
	//POI ���̺귯��! http://apache.org
	/*
	 * HSSFWorkbook:�������� .xls���ϸ� �ؼ��Ҽ� �ִ°� ����. .xlsx������ XHSSFWorkbook�� �̿��ؾ� �ҵ�?
	 * HSSFSheet: sheet
	 * HSSFRow: row
	 * HSSFCell: cell
	 * 
	 * */
	public void loadExel(){
		//ó������ jar��ó��: �����Ѱ��� api�� ����.
		int result=chooser.showOpenDialog(this);
		
		
		if (result==JFileChooser.APPROVE_OPTION) {
			File file=chooser.getSelectedFile();
			FileInputStream fis=null;//try���� ���������� �ȵǾ� ���߿� ���� �� �ִ�.
			PreparedStatement pstmt=null;
			
			try {
				fis=new FileInputStream(file);//������ �Ⱦ����� �� ������ �� ���� �����̴�. �Ʊ�� ����
				HSSFWorkbook book=null;
				book=new HSSFWorkbook(fis);//���������� �����Ҽ� �ְ� �Ǿ���.
				HSSFSheet sheet=null;
				sheet=book.getSheet("��������");
				//System.out.println(sheet);
				
				//int total=sheet.getLastRowNum();
				//System.out.println(total);
				/*for(int i=1; i<=147; i++){
					HSSFRow row=sheet.getRow(i);
					HSSFCell seq=row.getCell(0);
					HSSFCell name=row.getCell(1);
					HSSFCell addr=row.getCell(2);
					HSSFCell regdate=row.getCell(3);
					HSSFCell status=row.getCell(4);
					HSSFCell dimension=row.getCell(5);
					HSSFCell type=row.getCell(6);
					
					StringBuffer sql=new StringBuffer();
					sql.append("insert into hospital(seq, name, addr, regdate, status, dimension, type)");
					sql.append(" values("+seq.getNumericCellValue()+", '"+name.getStringCellValue()+"', '"+addr.getStringCellValue()+"', '"+regdate.getStringCellValue()+"', '"+status.getStringCellValue()+"', "+dimension.getNumericCellValue()+", '"+type.getStringCellValue()+"')");
					
					System.out.println(sql.toString());
				}*/
				int total=sheet.getLastRowNum();
				DataFormatter df=new DataFormatter();//������ �ٲ� �� �ִ�.

				for (int i = 1; i <= total; i++) {
					HSSFRow row=sheet.getRow(i);
					
					
					int columnCount=row.getLastCellNum();
					String[] value=new String[columnCount];
					for (int j = 0; j < columnCount; j++) {
						HSSFCell cell=row.getCell(j);
						
						/*if (cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC) {
							
						}else if (cell.getCellType()==HSSFCell.CELL_TYPE_STRING){
														
						}*/
						//�ڷ����� ���ѵ��� �ʰ� ��� ��Ʈ�� ó��: DataFormatter�� �̿��Ѵ�.!
						value[j]=df.formatCellValue(cell);
						System.out.print(value[j]);
					}
					System.out.println("");
					StringBuffer sql=new StringBuffer();
					sql.append("insert into hospital(seq, name, addr, regdate, status, dimension, type)");
					//sql.append(" values(seq, 'name', 'addr', 'regdate', 'status', dimension, 'type')");
					sql.append(" values("+value[0]+", '"+value[1]+"', '"+value[2]+"', '"+value[3]+"', '"+value[4]+"', "+value[5]+", '"+value[6]+"')");
				
					pstmt=con.prepareStatement(sql.toString());
					pstmt.executeUpdate(); //insert���� update�� �̿��Ѵ�.
					//����! ������ �Է��Ѱ� ���������� ��������
				}
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (pstmt!=null) {
					try {
						pstmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (fis!=null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}	
			}
			
		}
		
	}
	
	//������ ���ڵ� ����
	public void delete(){
		
	}
	
	//��� ���ڵ� ��������
	public void getList(){
		String sql="select * from hospital order by seq asc";
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			//�÷��� ����!!
			ResultSetMetaData meta=rs.getMetaData();
			int count=meta.getColumnCount();
			columnName=new Vector<>();
			for (int i = 0; i < count; i++) {
				columnName.add(meta.getColumnName(i+1));//1������ ����
			}
			
			
			//TableModel �� �Ѱܹޱ� ���� ���������ͷ� ��������
			list=new Vector<Vector>();//������ ����!!
			//�ʿ��Ҷ� ȣ���ϰ� ��������� ����
			while(rs.next()){
				Vector vec=new Vector(); //���ڵ� 1�� ���� ���̴�.
				vec.add(rs.getString("seq"));
				vec.add(rs.getString("name"));
				vec.add(rs.getString("addr"));
				vec.add(rs.getString("regdate"));
				vec.add(rs.getString("status"));
				vec.add(rs.getString("dimension"));
				vec.add(rs.getString("type"));
				
				//columnname��� �ε��� �ᵵ ������
				
				list.add(vec);				
			}
						
			//���̱׷��̼� �� ���̸� ȣ���ϸ鼭 �Ѱ�����
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
	
	public void actionPerformed(ActionEvent e) {
		Object obj=e.getSource();
		if (obj==bt_open) {
			open();
		}else if (obj==bt_load) {
			load();
		}else if(obj==bt_exel	){
			loadExel();
		}else if (obj==bt_del) {
			delete();
		}
	}
	
	//���̺� ���� ������ ���� ������ �߻��ϸ�, �� ������ �����ϴ� �������̴�.
	public void tableChanged(TableModelEvent e) {
		//this.fireTableCellUpdated(row, col);�޼ҵ带 tablemodel�� setValueAt�޼ҵ忡 �߰��ؾ��Ѵ�.
		System.out.println("�ٲ��?");
		
		System.out.println(e.getFirstRow()+", "+e.getColumn());
		
		int row=e.getFirstRow();
		int col=e.getColumn();
		
		
		String colName=(String) columnName.elementAt(col);
		String changedValue=(String) list.get(row).elementAt(col);
		String seq=(String)list.get(row).elementAt(0);
		
		System.out.println(colName+", "+changedValue);
		
		StringBuffer sql=new StringBuffer();
		sql.append("update hospital set "+colName+"='"+changedValue+"'");
		sql.append(" where seq="+seq);
		
		System.out.println(sql.toString());	
		
		//����: ���޸� ���� �����Ǿ����� sysout���� �˾ƺ���. ���⼭ e�� �̿��Ѵ�.
		//update hospital set �÷���=�� where  ������Ʈ���� �� ��µǰ� �غ���.
	}
	
	public static void main(String[] args) {
		new LoadMain();
	}

}
