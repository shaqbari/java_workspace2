/*���ϰ� ���õ� �۾��� �����ִ� ���뼺�ִ� Ŭ���� �����Ѵ�.*/

package com.ss.util.file;

public class FileUtil {
	/*�Ѱܹ��� ��ο��� Ȯ���� ���ϱ�*/
	public static String getExt(String path){//new���� �ʰ� �ٷ� ����Ҽ� �ְ� static���� ������!
		//c:/aa/ddd/test...aa.jpg
		int last=path.lastIndexOf(".");
		
		return path.substring(last+1, path.length());
	}
}
