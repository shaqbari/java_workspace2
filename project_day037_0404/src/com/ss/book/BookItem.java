/*
 *å 1���� ǥ���ϴ� UI������Ʈ
 * 
 * */

package com.ss.book;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class BookItem extends JPanel{
	Canvas can;
	JLabel la_name, la_price;
	
	public BookItem(Image img, String name, String price) {//�̹����� ������ ���� �����Ƿ� �����ڷ� �޴´�.
		can = new Canvas(){
			public void paint(Graphics g) {
				g.drawImage(img, 0, 0, 120, 120, this);				
			}			
		};
		
		la_name=new JLabel(name);
		la_price=new JLabel(price);
		
		can.setPreferredSize(new Dimension(120, 120)); //ũ�⸦ �����ؾ� �̹����� ���´�.
		
		add(can);
		add(la_name);
		add(la_price);
		
		setPreferredSize(new Dimension(120, 180));				
		setBackground(Color.GRAY);
		setVisible(true);
				
	}
	
}
