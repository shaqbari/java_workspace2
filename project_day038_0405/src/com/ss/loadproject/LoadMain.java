package com.ss.loadproject;

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
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;

public class LoadMain extends JFrame implements ActionListener{
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
	
	public LoadMain() {
		p_north= new JPanel();
		t_path=new JTextField(20);
		bt_open= new JButton("파일열기");
		bt_load= new JButton("로드하기");
		bt_exel= new JButton("엑셀로드");
		bt_del= new JButton("삭제하기");
		table=new JTable(3, 4);
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
		
		//윈도우와 리스너 연결
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				//데이터베이스 자원 해제
				manager.disConnect(con);
				
				//프로세스 종료
				System.exit(0);
			}			
		});
		
		
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
					
					int resutl=pstmt.executeUpdate(); //쿼리수행
					//기존에 누적된 StringBuffer의 데이터를 모두 지우기
					sb.delete(0, sb.length()); //while문안에서 loop마다 StringBuffer를 새롭개 new해도 된다.
					
					
					//데이터 중간에 ,가 있으면 오류가 난다. 처리속도가 차이가 나도 누락가능성있으므로 쓰레드를 이용하자.
										
				} else {
					System.out.println("난 1줄이므로 제외");					
				}
			}
			JOptionPane.showMessageDialog(this, "마이그레이션 완료");
			
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
			
			
			try {
				fis=new FileInputStream(file);//빨때는 꽂았지만 잘 이해할 수 없는 형식이다. 아기와 소주
				HSSFWorkbook book=null;
				book=new HSSFWorkbook(fis);//엑셀파일을 이해할수 있게 되었다.
				HSSFSheet sheet=null;
				sheet=book.getSheet("동물병원");
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
				DataFormatter df=new DataFormatter();//형식을 바꿀 수 있다.

				for (int i = 1; i <= total; i++) {
					HSSFRow row=sheet.getRow(i);
					
					int columnCount=row.getLastCellNum();
					for (int j = 0; j < columnCount; j++) {
						HSSFCell cell=row.getCell(j);
						
						/*if (cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC) {
							
						}else if (cell.get){
														
						}*/
						//자료형에 국한되지 않고 모두 스트링 처리
						String value=df.formatCellValue(cell);
						System.out.print(value);
					}
					System.out.println("");
				}
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	//선택한 레코드 삭제
	public void delete(){
		
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
	
	public static void main(String[] args) {
		new LoadMain();
	}


}
