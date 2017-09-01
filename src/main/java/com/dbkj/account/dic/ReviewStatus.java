package com.dbkj.account.dic;

/**
 * 审核状态
 * @author DELL
 *
 */
public enum ReviewStatus {
	PENDING(0),//待审核
	PASS(1),//通过
	REJECT(2);//未通过
	

	private ReviewStatus(int code) {
		this.code = code;
	}

	private int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	
}
