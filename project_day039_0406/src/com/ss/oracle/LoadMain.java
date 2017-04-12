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
	FileReader reader=null;//파일을 대상으로 한 문자기반스트림(reader)
	BufferedReader buffer=null;
	
	//윈도우창이 열리면 이미 접속을 확보해 두자
	DBManager manager=DBManager.getInstance();
	Connection con;
	Vector columnName;
	Vector<Vector> list;
	Thread thread; //엑셀 등록시 사용될 쓰레드 왜? 
	/*데이터량이 너무 많을경우, 혹은 네트워크 상태가 좋지 않을경우
	 * insert가 while문 속도를 못따라간다.
	 * 따라서 안정성을 위해 일부러 시간지연을 일으켜 insert를 시도할 것이다.
	 * 
	 * */
	
	//엑셀 파일에 의해 생성된 쿼리문을 쓰레드가 사용할 수 있는 상태로 저장해놓자
	StringBuffer insertSql=new StringBuffer();
	
	String seq; //선택한 row의 seq저장
<<<<<<< HEAD
	int row; //선택한 row의 index
	
=======
	int row; //선택한 row index
	MyModel myModel;
>>>>>>> 1c3f0b0d7e6e845e392bbc27d1d5aeed3b090df3
	
	public LoadMain() {
		p_north= new JPanel();
		t_path=new JTextField(20);
		bt_open= new JButton("csv파일열기");
		bt_load= new JButton("csv로드하기");
		bt_exel= new JButton("엑셀로드");
		bt_del= new JButton("삭제하기");
		table=new JTable(3,8);
		//테이블모델을 이용하면 cell을 편집하는 것도 tablemodel이 책임져야 한다.
		//편집한걸 반영하는것도 설정해야한다.
		
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
				
				row=t.getSelectedRow(); //list에서도 삭제할수 있게 멤버변수로 빼자.
				int col=0; //seq는 첫번째 컬림이니깐.
				
				seq=(String) t.getValueAt(row, 0); //멤버변수로 빼자				
				//JOptionPane.showConfirmDialog(LoadMain.this, seq+"삭제하시겠습니까?");//LoadMain.this 대신 js settimeout에서처럼 var me=this;처럼 쓸수도 있다.
				//위는 삭제버튼 눌렀을때 호출
			}			
		});
		
		//윈도우와 리스너 연결
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				//데이터베이스 자원 해제
				manager.disConnect(con);
				
				//프로세스 종료
				System.exit(0);
			}			
		});
		
		//테이블 모델과 리스너와의 연결 여기서는 모델이 결정되지 않았다
		//table.getModel().addTableModelListener(this);//현재쓰고있는 모델을 반환해준다.
		
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		init();
	}
	
	public void init(){
		//connection 얻어다 놓기
		con=manager.getConnection();
		
	}
	
	
	//파일 탐색기 띄우기
	public void open(){
		int result=chooser.showOpenDialog(this);
		//열기를 누르면 목적파일에 스트림을 생성
		if (result==JFileChooser.APPROVE_OPTION) {
			//유저가 선택한 파일
			File file=chooser.getSelectedFile();
			/*String[] name=file.getName().split("\\."); // .은 특수문자이므로 앞에 역슬래시가 있어야 한다.
			System.out.println(name.length);
			System.out.println(name);
			
			int leng=name.length;			
			if(name[leng-1]=="CSV"){ //파일명에 .이포함되어 있을수도 있으므로, 확장자를 얻기 위해 마지막 배열을 사용한다.
			
			나만의 라이브러리를 만드러 사용하자.*/			
			String ext=FileUtil.getExt(file.getName());
			
			if(ext.equals("csv")){ //String은 객체이므로 비교할때 equals를 사용하자!
				t_path.setText(file.getAbsolutePath());
				try {
					reader=new FileReader(file);
					buffer=new BufferedReader(reader); //한줄씩 읽기 위해 업그레이드
					//빨때만 꽂아두고 로드버튼을 누를때 while문이 돌도록 하자.
					//String data;
					
				/*	while (true) {//네트워크는 원격지에 도달하고 인증과정등 때문에 느린데 while문은 빠르기 때문에 쓰레드에서 sleep으로 속도를 늦춰야 한다.
						data=buffer.readLine();
						if (data==null) break;
						System.out.println(data);
					}*/
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} 
				//여기서는 닫아서는 안된다.
			}else{
				System.out.println("csv파일만 선택하세요");
				JOptionPane.showMessageDialog(this, "csv파일만 선택하세요");
				return;
			}
		}	
	}	
	
	//CSV --> oracle로 데이터 이전(migration)하기
	public void load(){
		//System.out.println(buffer);
		//버퍼스트림을 이용하여 csv의 데이터를 1줄씩 읽어들여 insert시키자!
		//레코드가 없을때 까지 while문으로 돌리면 너무 빠르므로, 
		//네트워크가 감당할 수 없기 때문에 일부러 지연시키면서..
		String data=null;			
		StringBuffer sb=new StringBuffer();	
		PreparedStatement pstmt=null; //sql문하나씩만 수행
		
		try {
			
			while (true) {
				data = buffer.readLine();
				if (data==null) break;					
				//컬럼명이들어있는 처음라인을 제외하기 위해 처음이 seq면 제외시키자. count를 써서 0번을 제외시킬수도 있지만 공부하기위해서
				String[] value = data.split(",");// .은 \\(escape)을 앞에 써야하지만 ,는 특수문자가 아니다.
				if (!value[0].equals("seq")) {//seq줄을 제외하고 insert하겠다.					
					sb.append("insert into hospital(seq, name, addr, regdate, status, dimension, type)");
					//sb.append(" values(seq, 'name', 'addr', 'regdate', 'status', dimension, 'type')"); //한칸 띄어야한다. 
					//문자입력할거에 홑따옴표로 감싸고 더블클릭후 삭제 쌍따옴표 추가하고 ++사이에 변수 입력
					sb.append(" values("+value[0]+", '"+value[1]+"', '"+value[2]+"', '"+value[3]+"', '"+value[4]+"', "+value[5]+", '"+value[6]+"')");//앞에 한칸 띄자
					
					System.out.println(sb.toString());					
					pstmt=con.prepareStatement(sb.toString());
					
					int result=pstmt.executeUpdate(); //쿼리수행
					//기존에 누적된 StringBuffer의 데이터를 모두 지우기
					sb.delete(0, sb.length()); //while문안에서 loop마다 StringBuffer를 새롭개 new해도 된다.
					
					
					//데이터 중간에 ,가 있으면 오류가 난다. 처리속도가 차이가 나도 누락가능성있으므로 쓰레드를 이용하자.
										
				} else {
					System.out.println("난 1줄이므로 제외");					
				}
			}
			JOptionPane.showMessageDialog(this, "마이그레이션 완료");
			
			//jtable 나오게 처리
			getList();
			
			
			//table.setModel(new MyModel(columnName, list));
			//나중에 갱신할때 사용하기 위해 따로 멤버변수로 저장ㄴ
			myModel=new MyModel(columnName, list);
			table.setModel(myModel);
			
			//테이블 모델과 리스너와의 연결
			table.getModel().addTableModelListener(this);//현재쓰고있는 모델을 반환해준다.
			
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
	
	//엑셀파일 읽어서 db에 마이그레이션 하기
	//javaSE 엑셀제어 라이브러리 있다? X
	//open Source: 공개소프트웨어
	//copyright <->copyleft (아파치 단체)
	//POI 라이브러리! http://apache.org
	/*
	 * HSSFWorkbook:엑셀파일 .xls파일만 해석할수 있는거 같다. .xlsx파일은 XHSSFWorkbook을 이용해야 할듯?
	 * HSSFSheet: sheet
	 * HSSFRow: row
	 * HSSFCell: cell
	 * 
	 * */
	public void loadExel(){
		//처음보는 jar대처법: 제공한곳의 api를 본다.
		int result=chooser.showOpenDialog(this);
		
		
		if (result==JFileChooser.APPROVE_OPTION) {
			File file=chooser.getSelectedFile();
			FileInputStream fis=null;//try문의 지역변수가 안되야 나중에 닫을 수 있다.
			PreparedStatement pstmt=null;
			StringBuffer cols=new StringBuffer(); //엑셀에서 얻어온 컬럼네임을 받을 스트링버퍼
			StringBuffer data=new StringBuffer(); //엑셀에서 얻어온 데이터을 받을 스트링버퍼
			
			
			try {
				fis=new FileInputStream(file);//빨때는 꽂았지만 잘 이해할 수 없는 형식이다. 아기와 소주
				HSSFWorkbook book=null;
				book=new HSSFWorkbook(fis);//엑셀파일을 이해할수 있게 되었다.
				HSSFSheet sheet=null;
				sheet=book.getSheet("동물병원");				
				//System.out.println(sheet);
								/*------------------------------------------------------------
				//이시점에서 컬럼명을 구해올 수 있다.
				 * 첫번째 row는 데이터가 아닌 컬럼정보이므로, 이 정보들을 추출하여 insert into table
				 ------------------------------------------------------------ */
				
				//int firstRow=sheet.getFirstRowNum(); //변수명은 소중하니깐 나중에 써먹자
				System.out.println("이 파일의 첫번째 row 번호는 "+sheet.getFirstRowNum());
				HSSFRow firstRow=sheet.getRow(sheet.getFirstRowNum());
				
				//Row를 얻었으니, 컬럼을 분석한다.
				cols.delete(0, cols.length());//먼저 StringBuffer비우기				
				
				//firstRow.getLastCellNum(); //마지막 셀번호		
				for(int i=0; i<firstRow.getLastCellNum(); i++){
					HSSFCell cell=firstRow.getCell(i);
					if(i<firstRow.getLastCellNum()-1){//마지막에는 쉼표를 안찍기 위해서 다음과 같은 조건을 붙인다.
						System.out.print(cell.getStringCellValue()+", ");
						cols.append(cell.getStringCellValue()+", ");
					}else{
						System.out.print(cell.getStringCellValue());
						cols.append(cell.getStringCellValue());						
					}
				}
				
				int total=sheet.getLastRowNum();
				DataFormatter df=new DataFormatter();//형식을 바꿀 수 있다.

				for (int i = 1; i <= total; i++) {
					HSSFRow row=sheet.getRow(i);					
					
					int columnCount=row.getLastCellNum();
					//String[] value=new String[columnCount];스트링버퍼로도 모을 수 있다.
					
					data.delete(0, data.length());
					
					for (int j = 0; j < columnCount; j++) {
						HSSFCell cell=row.getCell(j);						
						//value[j]=df.formatCellValue(cell);
						String value=df.formatCellValue(cell); //자료형에 국한되지 않고 모두 스트링 처리: DataFormatter를 이용한다.!
												
						if (cell.getCellType()==HSSFCell.CELL_TYPE_STRING) {//버전을 낮추거나 이걸쓰거나하자. 아직 더 좋은 메소드가 나오지 않았다.
							value="'"+value+"'";//문자열은 홑따옴표로 감싸야한다.
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
					
					//엑셀 파일에 의해 생성된 쿼리문을 쓰레드가 사용할 수 있는 상태로 저장해놓자
					insertSql.append("insert into hospital("+cols.toString()+") values("+data.toString()+");"); //;는 구분자용으로 사용
										
					
					/*StringBuffer sql=new StringBuffer();
					//sql.append("insert into hospital(seq, name, addr, regdate, status, dimension, type)"); //컬럼명도 파일에서 가져오자 데이터베이스에서 가져오는게 좋지않아?
					sql.append("insert into hospital("+cols.toString()+")");
					
					//sql.append(" values(seq, 'name', 'addr', 'regdate', 'status', dimension, 'type')");
					//sql.append(" values("+value[0]+", '"+value[1]+"', '"+value[2]+"', '"+value[3]+"', '"+value[4]+"', "+value[5]+", '"+value[6]+"')");
					sql.append(" values("+data.toString()+")"); //문자열은 홑따옴표로 감싸야한다.
				
					pstmt=con.prepareStatement(sql.toString());
					pstmt.executeUpdate(); //insert문은 update를 이용한다.
*/					//과제! 엑셀로 입력한걸 쿼리문으로 만들어오기					
				}
				
				//모든게 끝났으니, 편안하게 쓰레드에게 일 시키자.
				//Runnable 인터페이스를 인수로 넣으면 Thread의 run을 수행하는 것이아니라 Runnable 인터페이스를 구현한 자의 run()을 수행하게 된다.
				//따라서 우리꺼 수행
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
	
	//선택한 레코드 삭제
	public void delete(){
		int ans=JOptionPane.showConfirmDialog(LoadMain.this, seq+"삭제하시겠습니까?");//LoadMain.this 대신 js settimeout에서처럼 var me=this;처럼 쓸수도 있다.
		
		if (ans ==JOptionPane.OK_OPTION) {
			String sql="delete from hospital where seq="+seq;
			
			PreparedStatement pstmt=null;
			try {
				pstmt=con.prepareStatement(sql);
				int result=pstmt.executeUpdate();
				if (result!=0) {
					JOptionPane.showMessageDialog(this, "삭제완료");
					
					
					
					//갱신전에 list에서 삭제?
<<<<<<< HEAD
					list.remove(row);
=======
					//list.remove(row);
					
					//방금 완성된 list를 다시 MyModel에 대입 필요
					getList();
					myModel.list=list;
					
>>>>>>> 1c3f0b0d7e6e845e392bbc27d1d5aeed3b090df3
					table.updateUI();//테이블갱신
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
	
	//모든 레코드 가져오기
	public void getList(){
		String sql="select * from hospital order by seq asc";
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			//컬럼명 추출!!
			ResultSetMetaData meta=rs.getMetaData();
			int count=meta.getColumnCount();
			columnName=new Vector<>();
			for (int i = 0; i < count; i++) {
				columnName.add(meta.getColumnName(i+1));//1번부터 시작
			}
			
			
			//TableModel 이 넘겨받기 쉽게 이차원벡터로 가공하자
			list=new Vector<Vector>();//이차원 벡터!!
			//필요할때 호출하게 멤버변수로 빼자
			while(rs.next()){
				Vector vec=new Vector(); //레코드 1건 담을 것이다.
				vec.add(rs.getString("seq"));
				vec.add(rs.getString("name"));
				vec.add(rs.getString("addr"));
				vec.add(rs.getString("regdate"));
				vec.add(rs.getString("status"));
				vec.add(rs.getString("dimension"));
				vec.add(rs.getString("type"));
				
				//columnname대신 인덱스 써도 되지만
				
				list.add(vec);				
			}
						
			//마이그레이션 후 마이모델 호출하면서 넘겨주자
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
	
	//테이블 모델의 데이터 값의 변경이 발생하면, 그 찰나를 감지하는 리스너이다.
	public void tableChanged(TableModelEvent e) {
		/*//this.fireTableCellUpdated(row, col);메소드를 tablemodel의 setValueAt메소드에 추가해야한다.
		System.out.println("바꿨어?");
		
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
		
		//과제: 몇콤마 몇이 수정되었는지 sysout으로 알아보자. 여기서 e를 이용한다.
		//update hospital set 컬럼명=값 where  업데이트문도 잘 출력되게 해보자.*/
	
		
		//먼저seq는 수정 안되게!-mymodeldml iseditable에서
		
		int row=table.getSelectedRow();
		int col=table.getSelectedColumn();
		String column=(String) columnName.elementAt(col);
		
		//지정한 좌표의 값 반환 
		String value=(String) table.getValueAt(row, col);
		String seq=(String)table.getValueAt(row, 0);
		
		String sql="update hospital set "+column+"="+"'"+value+"'";
		sql+=" where seq="+seq;
		
		System.out.println(sql);
		
		//쿼리문 실행
		PreparedStatement pstmt=null;
		try {
			pstmt=con.prepareStatement(sql);
			int result=pstmt.executeUpdate();
			
			if (result!=0) {
				System.out.println("수정성공");
				JOptionPane.showMessageDialog(this, "수정성공");
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
		//insertSql에 insert문이 몇개인지 알아보자
		String[] str=insertSql.toString().split(";");
		System.out.println("insert문의 수는"+str.length);
		
		PreparedStatement pstmt=null;
		
		for (int i = 0; i < str.length; i++) {
			System.out.println(str[i]);
			try {
				Thread.sleep(2000); //for문을 늦추자.
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
		
		//기존에 사용했던 StringBuffer비우기		
		insertSql.delete(0, insertSql.length());
		
		//pstmt닫기
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
