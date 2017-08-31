package com.dbkj.account.dto;

import java.util.List;

/**
 * 菜单
 * @author DELL
 *
 */
public class MenuNode {
	
	public MenuNode(){}
	
	private String id;
	private String text;
	private String icon;
	private Boolean isHeader;
	private String url;
	private String targetType;
	private boolean isOpen;
	private int order;
	private List<MenuNode> children;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public Boolean getIsHeader() {
		return isHeader;
	}
	public void setIsHeader(Boolean header) {
		this.isHeader = header;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTargetType() {
		return targetType;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	public boolean getIsOpen() {
		return isOpen;
	}
	public void setIsOpen(boolean open) {
		this.isOpen = open;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public List<MenuNode> getChildren() {
		return children;
	}
	public void setChildren(List<MenuNode> children) {
		this.children = children;
	}
	
	
}
