package com.dbkj.account.sys.form;

import java.util.Date;

public class UserForgetPasswordForm
{
	private Date date;
	private int trycount;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getTrycount() {
		return trycount;
	}
	public void setTrycount(int trycount) {
		this.trycount = trycount;
	}
}