package com.ss.oracle;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import com.ss.util.file.FileUtil;

public class LoadMain extends JFrame implements ActionListener, TableModelListener, Runnable{
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
	Thread thread; //���� ��Ͻ� ���� ������ ��? 
	/*�����ͷ��� �ʹ� �������, Ȥ�� ��Ʈ��ũ ���°� ���� �������
	 * insert�� while�� �ӵ��� �����󰣴�.
	 * ���� �������� ���� �Ϻη� �ð������� ������ insert�� �õ��� ���̴�.
	 * 
	 * */
	
	//���� ���Ͽ� ���� ������ �������� �����尡 ����� �� �ִ� ���·� �����س���
	StringBuffer insertSql=new StringBuffer();
	
	String seq; //������ row�� seq����
<<<<<<< HEAD
	int row; //������ row�� index
	
=======
	int row; //������ row index
	MyModel myModel;
>>>>>>> 1c3f0b0d7e6e845e392bbc27d1d5aeed3b090df3
	
	public LoadMain() {
		p_north= new JPanel();
		t_path=new JTextField(20);
		bt_open= new JButton("csv���Ͽ���");
		bt_load= new JButton("csv�ε��ϱ�");
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
		table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				JTable t=(JTable) e.getSource();
				
				row=t.getSelectedRow(); //list������ �����Ҽ� �ְ� ��������� ����.
				int col=0; //seq�� ù��° �ø��̴ϱ�.
				
				seq=(String) t.getValueAt(row, 0); //��������� ����				
				//JOptionPane.showConfirmDialog(LoadMain.this, seq+"�����Ͻðڽ��ϱ�?");//LoadMain.this ��� js settimeout����ó�� var me=this;ó�� ������ �ִ�.
				//���� ������ư �������� ȣ��
			}			
		});
		
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
			/*String[] name=file.getName().split("\\."); // .�� Ư�������̹Ƿ� �տ� �������ð� �־�� �Ѵ�.
			System.out.println(name.length);
			System.out.println(name);
			
			int leng=name.length;			
			if(name[leng-1]=="CSV"){ //���ϸ� .�����ԵǾ� �������� �����Ƿ�, Ȯ���ڸ� ��� ���� ������ �迭�� ����Ѵ�.
			
			������ ���̺귯���� ���巯 �������.*/			
			String ext=FileUtil.getExt(file.getName());
			
			if(ext.equals("csv")){ //String�� ��ü�̹Ƿ� ���Ҷ� equals�� �������!
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
			}else{
				System.out.println("csv���ϸ� �����ϼ���");
				JOptionPane.showMessageDialog(this, "csv���ϸ� �����ϼ���");
				return;
			}
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
			
			
			//table.setModel(new MyModel(columnName, list));
			//���߿� �����Ҷ� ����ϱ� ���� ���� ��������� ���夤
			myModel=new MyModel(columnName, list);
			table.setModel(myModel);
			
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
			StringBuffer cols=new StringBuffer(); //�������� ���� �÷������� ���� ��Ʈ������
			StringBuffer data=new StringBuffer(); //�������� ���� �������� ���� ��Ʈ������
			
			
			try {
				fis=new FileInputStream(file);//������ �Ⱦ����� �� ������ �� ���� �����̴�. �Ʊ�� ����
				HSSFWorkbook book=null;
				book=new HSSFWorkbook(fis);//���������� �����Ҽ� �ְ� �Ǿ���.
				HSSFSheet sheet=null;
				sheet=book.getSheet("��������");				
				//System.out.println(sheet);
								/*------------------------------------------------------------
				//�̽������� �÷����� ���ؿ� �� �ִ�.
				 * ù��° row�� �����Ͱ� �ƴ� �÷������̹Ƿ�, �� �������� �����Ͽ� insert into table
				 ------------------------------------------------------------ */
				
				//int firstRow=sheet.getFirstRowNum(); //�������� �����ϴϱ� ���߿� �����
				System.out.println("�� ������ ù��° row ��ȣ�� "+sheet.getFirstRowNum());
				HSSFRow firstRow=sheet.getRow(sheet.getFirstRowNum());
				
				//Row�� �������, �÷��� �м��Ѵ�.
				cols.delete(0, cols.length());//���� StringBuffer����				
				
				//firstRow.getLastCellNum(); //������ ����ȣ		
				for(int i=0; i<firstRow.getLastCellNum(); i++){
					HSSFCell cell=firstRow.getCell(i);
					if(i<firstRow.getLastCellNum()-1){//���������� ��ǥ�� ����� ���ؼ� ������ ���� ������ ���δ�.
						System.out.print(cell.getStringCellValue()+", ");
						cols.append(cell.getStringCellValue()+", ");
					}else{
						System.out.print(cell.getStringCellValue());
						cols.append(cell.getStringCellValue());						
					}
				}
				
				int total=sheet.getLastRowNum();
				DataFormatter df=new DataFormatter();//������ �ٲ� �� �ִ�.

				for (int i = 1; i <= total; i++) {
					HSSFRow row=sheet.getRow(i);					
					
					int columnCount=row.getLastCellNum();
					//String[] value=new String[columnCount];��Ʈ�����۷ε� ���� �� �ִ�.
					
					data.delete(0, data.length());
					
					for (int j = 0; j < columnCount; j++) {
						HSSFCell cell=row.getCell(j);						
						//value[j]=df.formatCellValue(cell);
						String value=df.formatCellValue(cell); //�ڷ����� ���ѵ��� �ʰ� ��� ��Ʈ�� ó��: DataFormatter�� �̿��Ѵ�.!
												
						if (cell.getCellType()==HSSFCell.CELL_TYPE_STRING) {//������ ���߰ų� �̰ɾ��ų�����. ���� �� ���� �޼ҵ尡 ������ �ʾҴ�.
							value="'"+value+"'";//���ڿ��� Ȭ����ǥ�� ���ξ��Ѵ�.
						}
						
						if (j<columnCount-1) {
							data.append(value+", ");
						}else{
							data.append(value);							
						}
						
						//System.out.print(value[j]);
					}
					//System.out.println("");
					//System.out.println("insert into hospital("+cols.toString()+") values("+data.toString()+")");
					
					//���� ���Ͽ� ���� ������ �������� �����尡 ����� �� �ִ� ���·� �����س���
					insertSql.append("insert into hospital("+cols.toString()+") values("+data.toString()+");"); //;�� �����ڿ����� ���
										
					
					/*StringBuffer sql=new StringBuffer();
					//sql.append("insert into hospital(seq, name, addr, regdate, status, dimension, type)"); //�÷��� ���Ͽ��� �������� �����ͺ��̽����� �������°� �����ʾ�?
					sql.append("insert into hospital("+cols.toString()+")");
					
					//sql.append(" values(seq, 'name', 'addr', 'regdate', 'status', dimension, 'type')");
					//sql.append(" values("+value[0]+", '"+value[1]+"', '"+value[2]+"', '"+value[3]+"', '"+value[4]+"', "+value[5]+", '"+value[6]+"')");
					sql.append(" values("+data.toString()+")"); //���ڿ��� Ȭ����ǥ�� ���ξ��Ѵ�.
				
					pstmt=con.prepareStatement(sql.toString());
					pstmt.executeUpdate(); //insert���� update�� �̿��Ѵ�.
*/					//����! ������ �Է��Ѱ� ���������� ��������					
				}
				
				//���� ��������, ����ϰ� �����忡�� �� ��Ű��.
				//Runnable �������̽��� �μ��� ������ Thread�� run�� �����ϴ� ���̾ƴ϶� Runnable �������̽��� ������ ���� run()�� �����ϰ� �ȴ�.
				//���� �츮�� ����
				thread = new Thread(this);
				thread.start();
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} /*catch (SQLException e) {
				e.printStackTrace();
			} */finally {
				/*if (pstmt!=null) {
					try {
						pstmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}*/
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
		int ans=JOptionPane.showConfirmDialog(LoadMain.this, seq+"�����Ͻðڽ��ϱ�?");//LoadMain.this ��� js settimeout����ó�� var me=this;ó�� ������ �ִ�.
		
		if (ans ==JOptionPane.OK_OPTION) {
			String sql="delete from hospital where seq="+seq;
			
			PreparedStatement pstmt=null;
			try {
				pstmt=con.prepareStatement(sql);
				int result=pstmt.executeUpdate();
				if (result!=0) {
					JOptionPane.showMessageDialog(this, "�����Ϸ�");
					
					
					
					//�������� list���� ����?
<<<<<<< HEAD
					list.remove(row);
=======
					//list.remove(row);
					
					//��� �ϼ��� list�� �ٽ� MyModel�� ���� �ʿ�
					getList();
					myModel.list=list;
					
>>>>>>> 1c3f0b0d7e6e845e392bbc27d1d5aeed3b090df3
					table.updateUI();//���̺���
				}			
				
			} catch (SQLException e) {
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
		/*//this.fireTableCellUpdated(row, col);�޼ҵ带 tablemodel�� setValueAt�޼ҵ忡 �߰��ؾ��Ѵ�.
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
		//update hospital set �÷���=�� where  ������Ʈ���� �� ��µǰ� �غ���.*/
	
		
		//����seq�� ���� �ȵǰ�!-mymodeldml iseditable����
		
		int row=table.getSelectedRow();
		int col=table.getSelectedColumn();
		String column=(String) columnName.elementAt(col);
		
		//������ ��ǥ�� �� ��ȯ 
		String value=(String) table.getValueAt(row, col);
		String seq=(String)table.getValueAt(row, 0);
		
		String sql="update hospital set "+column+"="+"'"+value+"'";
		sql+=" where seq="+seq;
		
		System.out.println(sql);
		
		//������ ����
		PreparedStatement pstmt=null;
		try {
			pstmt=con.prepareStatement(sql);
			int result=pstmt.executeUpdate();
			
			if (result!=0) {
				System.out.println("��������");
				JOptionPane.showMessageDialog(this, "��������");
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			if (pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		
	}
	
	public void run() {
		//insertSql�� insert���� ����� �˾ƺ���
		String[] str=insertSql.toString().split(";");
		System.out.println("insert���� ����"+str.length);
		
		PreparedStatement pstmt=null;
		
		for (int i = 0; i < str.length; i++) {
			System.out.println(str[i]);
			try {
				Thread.sleep(2000); //for���� ������.
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
			
			try {
				pstmt=con.prepareStatement(str[i]);
				int result=pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		//������ ����ߴ� StringBuffer����		
		insertSql.delete(0, insertSql.length());
		
		//pstmt�ݱ�
		if (pstmt!=null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	public static void main(String[] args) {
		new LoadMain();
	}


}
