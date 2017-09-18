package com.dbkj.account.sys.form;

public class MoneyRecordForm
{
	private String id;
	private String userid;
	private String time;
	private String charge;
	private String realcharge;
	private String chargetype;
	private String chargetypestr;
	private String serialnum;
	private String chargeuser;
	private String remark;
	private String status;
	private String statusstr;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getCharge() {
		return charge;
	}
	public void setCharge(String charge) {
		this.charge = charge;
	}
	public String getRealcharge() {
		return realcharge;
	}
	public void setRealcharge(String realcharge) {
		this.realcharge = realcharge;
	}
	public String getChargetype() {
		return chargetype;
	}
	public void setChargetype(String chargetype) {
		this.chargetype = chargetype;
	}
	public String getSerialnum() {
		return serialnum;
	}
	public void setSerialnum(String serialnum) {
		this.serialnum = serialnum;
	}
	public String getChargeuser() {
		return chargeuser;
	}
	public void setChargeuser(String chargeuser) {
		this.chargeuser = chargeuser;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getChargetypestr() {
		return chargetypestr;
	}
	public void setChargetypestr(String chargetypestr) {
		this.chargetypestr = chargetypestr;
	}
	public String getStatusstr() {
		return statusstr;
	}
	public void setStatusstr(String statusstr) {
		this.statusstr = statusstr;
	}
}