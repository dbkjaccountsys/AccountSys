package com.dbkj.account.util;

public class CommonUtil {

	/**
	 * 
	 * @param arr
	 * @return
	 */
	public static int sum(int[] arr){
		if(arr==null){
			return 0;
		}
		int sum=0;
		for(int i=0;i<arr.length;i++){
			sum+=arr[i];
		}
		return sum;
	}
}
