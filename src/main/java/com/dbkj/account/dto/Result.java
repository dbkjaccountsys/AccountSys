package com.dbkj.account.dto;

public class Result<T> {
	
	public Result() {
		super();
	}
	
	public Result(boolean success, String reason) {
		super();
		this.success = success;
		this.reason = reason;
	}

	public Result(boolean success, String reason, T data) {
		super();
		this.success = success;
		this.reason = reason;
		this.data = data;
	}




	/**
	 * 操作是否成功
	 */
	private boolean success;
	/**
	 * 失败原因
	 */
	private String reason;
	/**
	 * 附加数据
	 */
	private T data;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	
}
