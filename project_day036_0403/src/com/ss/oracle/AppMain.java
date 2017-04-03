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
	String[][] item={ //htmló�� �����ִ°Ͱ� value�� ���߹迭�� ������
		{"�� ���̺��� �����ϼ���", ""},
		{"������̺�", "emp"},
		{"�μ����̺�", "dept"}
	};
	
	TableModel[] model=new TableModel[item.length];

	public AppMain() {
		//�����ΰ� ������ �и���Ű�� ���� �߰���(Controller)�� ���簡 �ʿ��ϴ�.
		//JTable������ �� ��Ʈ�ѷ��� ������ TableModel�� ���ش�.
		//TableModel�� ����� ���, JTable�� �ڽ��� ������� �� �����͸�
		//TableModel�� ���� ������ ���� ����Ѵ�.
		//getColumnCount();
		//getRowCount();
		//getValueAt();
		table=new JTable();//������ table�� ���� ���߿� method�̿��� model��ü
		scroll=new JScrollPane(table);
		p_west=new JPanel();
		p_center=new JPanel();
		choice=new Choice();		
		
		//���̺���� �÷�����
		model[0]=new DefaultTableModel();
		model[1]=new EmpModel();
		//model[2]=new DepttTableModel();
		
		/*choice.add("���̺� ����");
		choice.add("��� ���̺�"); //select * from emp;
		choice.add("�μ� ���̺�"); //select * from dept;
		htmló�� ��Ͽ� value�� ���߹迭�θ�����. */
		for (int i = 0; i < item.length; i++) {
			choice.add(item[i][0]);
		}
		
		//���̺� ������ �ֱ�
	/*	table.setValueAt("���", 0, 0);
		table.setValueAt("��", 0, 1);
		table.setValueAt("���", 1, 0);
		table.setValueAt("ƫ��", 1, 1);		
		table.setValueAt("�׾�", 2, 1);		
		table.setValueAt("�ؾ�", 2, 1);*/
		//�̷��� �ۼ��ϸ� �����ΰ� ������ ���� �����鼭 ���������� �����.
		
		p_west.add(choice);
		p_center.add(scroll);
		
		add(p_west, BorderLayout.WEST);
		add(p_center);
		
		pack();
		
		//���̽��� ������ ����
		choice.addItemListener(this);
		
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
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
		new AppMain();
	}


}
