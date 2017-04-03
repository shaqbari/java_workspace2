//������ Ŭ������ �ν��Ͻ��� ���� 1���� �����

package com.ss.oracle;

//SingleTon pattern: ���� ���� �� �ϳ��̴�.
/*javaSE,
 *javaEE ��ޱ��(javaSE�� �����ؼ� �������������)
 * 
 * */
public class Dog {	
	/*private Dog instance;//Dog���� ������ �� �ֵ��� ��ȸ�� �ش�.*/
	static Dog instance; //memory�� ���� static������ �־� ���� ������ null�λ����̴�.
	
	//new�� ���� ������ ����!!
	private Dog(){//private�ϸ� �ڱ� Ŭ������������ ȣ�Ⱑ��  �ʹ� ���ϴ�.		
		
	}
	
/*	public Dog getInstance() { //�ν��Ͻ��޼ҵ� �̱⶧���� new�� �ν��Ͻ������ϰ� �� ������ ȣ�Ⱑ���ϴ�.*
        //but �������� private�� new�� ���ұ� ������ �̸޼ҵ�� �ٸ� Ŭ�������� ȣ������ ���Ѵ�.
		return instance;
	}*/
	
	//static������ instance�� ���������� ������ �ʴ´�.
	static public Dog getInstance() { //static�̱� ������ new���� ��������
		//but heap ������ ���������� �˼� ����. ������ static���� ��������.
		if (instance==null) {
			instance=new Dog(); //null�϶��� �ѹ� Dog �ν��Ͻ��� ���������.
			//������ instance�� ���⼭ �ѹ��� ������� �ν��Ͻ��� ����Ų��.
			//�̷��� ����� singleton pattern�̶��Ѵ�.
		}
		
		return instance;
	}
}
