/*파일과 관련된 작업을 도와주는 재사용성있는 클래스 정의한다.*/

package com.ss.util.file;

public class FileUtil {
	/*넘겨받은 경로에서 확장자 구하기*/
	public static String getExt(String path){//new하지 않고 바로 사용할수 있게 static으로 만들자!
		//c:/aa/ddd/test...aa.jpg
		int last=path.lastIndexOf(".");
		
		return path.substring(last+1, path.length());
	}
}
