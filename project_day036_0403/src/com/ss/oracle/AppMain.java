package com.ss.oracle;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class AppMain extends JFrame implements ItemListener{
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

	public AppMain() {
		//디자인과 로직을 분리시키기 위해 중간자(Controller)의 존재가 필요하다.
		//JTable에서는 이 컨트롤러의 역할을 TableModel이 해준다.
		//TableModel을 사용할 경우, JTable은 자신이 보여줘야 할 데이터를
		//TableModel로 부터 정보를 얻어와 출력한다.
		//getColumnCount();
		//getRowCount();
		//getValueAt();
		table=new JTable();//유저가 table모델 결정 나중에 method이용해 model교체
		scroll=new JScrollPane(table);
		p_west=new JPanel();
		p_center=new JPanel();
		choice=new Choice();		
		
		//테이블들을 올려두자
		model[0]=new DefaultTableModel();
		model[1]=new EmpModel();
		//model[2]=new DepttTableModel();
		
		/*choice.add("테이블 선택");
		choice.add("사원 테이블"); //select * from emp;
		choice.add("부서 테이블"); //select * from dept;
		html처럼 목록에 value를 이중배열로만들어보자. */
		for (int i = 0; i < item.length; i++) {
			choice.add(item[i][0]);
		}
		
		//테이블에 데이터 넣기
	/*	table.setValueAt("사과", 0, 0);
		table.setValueAt("배", 0, 1);
		table.setValueAt("장미", 1, 0);
		table.setValueAt("튤립", 1, 1);		
		table.setValueAt("잉어", 2, 1);		
		table.setValueAt("붕어", 2, 1);*/
		//이렇게 작성하면 디자인과 로직이 섞여 있으면서 유지보수가 힘들다.
		
		p_west.add(choice);
		p_center.add(scroll);
		
		add(p_west, BorderLayout.WEST);
		add(p_center);
		
		pack();
		
		//초이스와 리스너 연결
		choice.addItemListener(this);
		
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
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
		new AppMain();
	}


}
