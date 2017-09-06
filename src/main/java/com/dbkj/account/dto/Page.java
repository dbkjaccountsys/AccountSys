package com.dbkj.account.dto;

import java.util.List;

public class Page<T> {
	
	public Page(){
		this.currentPage=1;
		this.pageSize=20;
	}

	private int currentPage;//当前页
	private int pageSize;//每页显示的数据行数
	private int totalCount;//总页数
	private long records;//总数据行数
	private List<T> data;//分页数据
	public long getRecords() {
		return records;
	}
	public void setRecords(long records) {
		this.records = records;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "Page [currentPage=" + currentPage + ", pageSize=" + pageSize + ", totalCount=" + totalCount
				+ ", records=" + records + ", data=" + data + "]";
	}
	
	
}
