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
	//������ ������ con�� ������ �ϴ� con�� ���⿡ �ִ� ���� ����.
	ConnectionManager manager;
	Connection con; //��� ��ü�� �����ϱ� ����,
	JTable table;
	JScrollPane scroll;
	JPanel p_west, p_center;
	Choice choice;
	String[][] item={ //htmló�� �����ִ°Ͱ� value�� ���߹迭�� ������
		{"�� ���̺��� �����ϼ���", ""},
		{"������̺�", "emp"},
		{"�μ����̺�", "dept"}
	};
	
	TableModel[] model=new TableModel[item.length];

	public AppMain2() {
		manager=ConnectionManager.getInstance();
		con=manager.getConnection();
		
		table=new JTable();//������ table�� ���� ���߿� method�̿��� model��ü
		scroll=new JScrollPane(table);
		p_west=new JPanel();
		p_center=new JPanel();
		choice=new Choice();		

		model[0]=new DefaultTableModel();
		model[1]=new EmpModel2(con);
		model[2]=new DeptModel(con);
		//con�� �ϳ��� �����ϸ� �ӵ��� ���� �����鼭 �������̴�.
		
		for (int i = 0; i < item.length; i++) {
			choice.add(item[i][0]);
		}

		p_west.add(choice);
		p_center.add(scroll);
		
		add(p_west, BorderLayout.WEST);
		add(p_center);
		
		pack();
		
		//���̽��� ������ ����
		choice.addItemListener(this);
		
		//������â ���� �� ����Ŭ ���� ����
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				//Ŀ�ؼ� �ݱ�
				manager.disConnect(con);
				//���α׷� ����
				System.exit(0);
			}
		});		
		
		setVisible(true);
		//setDefaultCloseOperation(EXIT_ON_CLOSE);������ ���� �۾��� �ؾ��ϹǷ� ���⼭�� �ʿ����.		
	}

	//�ش�Ǵ� ���̺� �����ֱ�
	public void showData(int index){		
		System.out.println("����� ���Ե� ���̺���"+item[index][1]);
		
		//�ش�Ǵ� ���̺� ���� ����ϸ�ȴ�.
		//emp -->EmpModel
		//dept --> DeptModel
		//�ƹ��͵� �ƴϸ� --> DefaultTableModel
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
