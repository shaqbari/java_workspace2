/*이클래스는 로직을 작성하기 위함이 아니라
 * 오직 데이터베이스의 레코드 1건을 담기 위한 객체이다.
 * 이러한 목적으로 설계된 클래스를 가리켜
 *  설계분야에서는 DTO(데이터 전달객체), VO*/

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
