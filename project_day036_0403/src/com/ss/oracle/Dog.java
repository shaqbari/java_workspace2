//강아지 클래스의 인스턴스를 오직 1개만 만들기

package com.ss.oracle;

//SingleTon pattern: 개발 패턴 중 하나이다.
/*javaSE,
 *javaEE 고급기술(javaSE를 포함해서 엔터프라이즈급)
 * 
 * */
public class Dog {	
	/*private Dog instance;//Dog형에 접근할 수 있도록 기회를 준다.*/
	static Dog instance; //memory의 같은 static영역에 있어 볼수 있지만 null인상태이다.
	
	//new에 의한 생성을 막자!!
	private Dog(){//private하면 자기 클래스내에서만 호출가능  너무 강하다.		
		
	}
	
/*	public Dog getInstance() { //인스턴스메소드 이기때문에 new로 인스턴스생성하고 난 위에야 호출가능하다.*
        //but 생성자의 private로 new를 막았기 때문에 이메소드는 다른 클래스에서 호출하지 못한다.
		return instance;
	}*/
	
	//static에서는 instance의 참조변수가 보이지 않는다.
	static public Dog getInstance() { //static이기 때문에 new없이 생성가능
		//but heap 영역의 참조변수를 알수 없다. 변수도 static으로 선언하자.
		if (instance==null) {
			instance=new Dog(); //null일때만 한번 Dog 인스턴스가 만들어진다.
			//원본인 instance는 여기서 한번만 만들어진 인스턴스를 가리킨다.
			//이러한 방법은 singleton pattern이라한다.
		}
		
		return instance;
	}
}
