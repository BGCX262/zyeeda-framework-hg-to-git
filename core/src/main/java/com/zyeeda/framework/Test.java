package com.zyeeda.framework;


public class Test {

	public static void main(String[] args)  {
		String a = "4d:defect:*";
		//String[] arg = a.split(":");
		int i = a.lastIndexOf(":");
		
		String b = a.substring(0, i);
		System.out.println(b);
		if(a.endsWith("*")){
		}
//		for(String e:arg){
//		}
		
//		for(int i = 0 ;i<arg.length;i++){
//			System.out.println("11111111111111111"+arg[i].toString());
//		}
	}

}
