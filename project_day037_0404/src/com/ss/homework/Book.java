/*��Ŭ������ ������ �ۼ��ϱ� ������ �ƴ϶�
 * ���� �����ͺ��̽��� ���ڵ� 1���� ��� ���� ��ü�̴�.
 * �̷��� �������� ����� Ŭ������ ������
 *  ����о߿����� DTO(������ ���ް�ü), VO*/

package com.ss.homework;

public class Book {
	private int book_id;
	private int subcategory_id;
	private String book_name;
	private int book_price;
	private String book_img;

	public int getBook_id() {
		return book_id;
	}

	public void setBook_id(int book_id) {
		this.book_id = book_id;
	}

	public int getSubcategory_id() {
		return subcategory_id;
	}

	public void setSubcategory_id(int subcategory_id) {
		this.subcategory_id = subcategory_id;
	}

	public String getBook_name() {
		return book_name;
	}

	public void setBook_name(String book_name) {
		this.book_name = book_name;
	}

	public int getBook_price() {
		return book_price;
	}

	public void setBook_price(int book_price) {
		this.book_price = book_price;
	}

	public String getBook_img() {
		return book_img;
	}

	public void setBook_img(String book_img) {
		this.book_img = book_img;
	}

}
