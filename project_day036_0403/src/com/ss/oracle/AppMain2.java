package com.ss.oracle;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class AppMain2 extends JFrame implements ItemListener{
	//윈도우 닫을때 con도 닫혀야 하니 con은 여기에 있는 것이 좋다.
	ConnectionManager manager;
	Connection con; //모든 객체간 공유하기 위해,
	JTable table;
	JScrollPane scroll;
	JPanel p_west, p_center;
	Choice choice;
	String[][] item={ //html처럼 보여주는것과 value를 이중배열로 만들자
		{"▼ 테이블을 선택하세요", ""},
		{"사원테이블", "emp"},
		{"부서테이블", "dept"}
	};
	
	TableModel[] model=new TableModel[item.length];

	public AppMain2() {
		manager=ConnectionManager.getInstance();
		con=manager.getConnection();
		
		table=new JTable();//유저가 table모델 결정 나중에 method이용해 model교체
		scroll=new JScrollPane(table);
		p_west=new JPanel();
		p_center=new JPanel();
		choice=new Choice();		

		model[0]=new DefaultTableModel();
		model[1]=new EmpModel2(con);
		model[2]=new DeptModel(con);
		//con을 하나로 공유하면 속도가 좀더 빠르면서 안정적이다.
		
		for (int i = 0; i < item.length; i++) {
			choice.add(item[i][0]);
		}

		p_west.add(choice);
		p_center.add(scroll);
		
		add(p_west, BorderLayout.WEST);
		add(p_center);
		
		pack();
		
		//초이스와 리스너 연결
		choice.addItemListener(this);
		
		//윈도우창 닫을 때 오라클 접속 끊기
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				//커넥션 닫기
				manager.disConnect(con);
				//프로그램 종료
				System.exit(0);
			}
		});		
		
		setVisible(true);
		//setDefaultCloseOperation(EXIT_ON_CLOSE);닫을때 여러 작업을 해야하므로 여기서는 필요없다.		
	}

	//해당되는 테이블 보여주기
	public void showData(int index){		
		System.out.println("당신이 보게될 테이블은"+item[index][1]);
		
		//해당되는 테이블 모델을 사용하면된다.
		//emp -->EmpModel
		//dept --> DeptModel
		//아무것도 아니면 --> DefaultTableModel
		table.setModel(model[index]);
	}
	
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		int index=ch.getSelectedIndex();
		showData(index);
	}
	
	public static void main(String[] args) {
		new AppMain2();
	}


}
