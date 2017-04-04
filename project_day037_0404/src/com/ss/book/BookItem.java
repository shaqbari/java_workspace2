/*
 *책 1권을 표현하는 UI컴포넌트
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
	
	public BookItem(Image img, String name, String price) {//이미지는 정해져 있지 않으므로 생성자로 받는다.
		can = new Canvas(){
			public void paint(Graphics g) {
				g.drawImage(img, 0, 0, 120, 120, this);				
			}			
		};
		
		la_name=new JLabel(name);
		la_price=new JLabel(price);
		
		can.setPreferredSize(new Dimension(120, 120)); //크기를 지정해야 이미지가 나온다.
		
		add(can);
		add(la_name);
		add(la_price);
		
		setPreferredSize(new Dimension(120, 180));				
		setBackground(Color.GRAY);
		setVisible(true);
				
	}
	
}
