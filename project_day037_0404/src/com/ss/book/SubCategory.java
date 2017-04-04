/*��ü���� ����� �ڹٿ����� ������ �繰�� Ŭ������ ����������
 * DB������ ������ �繰�� Entity��� ��ü�������� ǥ���Ѵ�.
 * �ᱹ ��ü�� ǥ���ϴ� ����� �ٸ��� ������ ����.
 * ���� �ݿ��̶�� ������ ����.
 * 
 * ��ü���� ���� Ŭ������ �ν��Ͻ��� �����س��� ��Ǫ���̶��
 * DB�о߿��� ���̺��� ���ڵ带 ������ �� �ִ� Ʋ�� ���� �����ϴ�.
 * �̶�, �ϳ��� ���ڵ�� �ᱹ �ϳ��� ��ü�� �����Ѵ�.
 * 
 * ���)���̺� �����ϴ� ��ǰ ���ڵ��� ���� �� 5�����,
 *  �����ڴ� �� ������ ���ڵ带 5���� �ν��Ͻ��� ���� ������ �ȴ�.
 *  
 *  �Ʒ��� Ŭ������ ���� �ۼ����� �ƴϴ�. �� �Ѱ��� ���ڵ带 �������
 *  ��������뵵�� ����� Ŭ�����̴�.
 *  ���ü���о߿��� �̷��� ������ Ŭ������ ������ VO, DTO�� �Ѵ�.
 *  VO: Value Object = ���� ��� ��
 *  Data Transfer Object =���� �����ϱ� ���� ��ü
 *
 * */

package com.ss.book;

//������ ���� Ŭ����!! Dummy Ŭ����
//���� ������ ��� �׸���!! �迭���� ��������?
/*String[1][2][3] �迭�� ��ü�� �ƴ϶� ��ü���������� ���ϰ�
 * ũ�⸦ �̸� �����ؾ��ϸ�, �ڷ����� �Ѱ��� ����
 * �迭�� ��ü�� ������ Ʋ�� ������� ���ߵǹǷ�,
 * ��ü�� ó���ϴ� ���� �ξ� �� �۾������ ����.
 * 
 * bybatis
 * ���̺�����Ʈ��� �����ӿ�ũ�� �̷� ������� �ڵ����� ¦�� �����ش�.
 * 
 * DTO�� ������ �ϸ� �Ʒ��Ͱ��� �÷��̸��� private ����������ϰ� getter�� setter�� �����.
 * 
 * ctrl shift s r 
 * */
public class SubCategory {
	private int subcategory_id;
	private int topcategory_id;
	private String category_name;
	
	public int getSubcategory_id() {
		return subcategory_id;
	}
	public void setSubcategory_id(int subcategory_id) {
		this.subcategory_id = subcategory_id;
	}
	public int getTopcategory_id() {
		return topcategory_id;
	}
	public void setTopcategory_id(int topcategory_id) {
		this.topcategory_id = topcategory_id;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	
}
