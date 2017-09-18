package com.dbkj.account.dto;

import java.util.List;

public class AuthNode {

	private Long id;
	private String name;
	private List<AuthNode> children;
	private transient AuthNode parent;
	private boolean checked;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<AuthNode> getChildren() {
		return children;
	}
	public void setChildren(List<AuthNode> children) {
		this.children = children;
	}
	
	public AuthNode getParent() {
		return parent;
	}
	public void setParent(AuthNode parent) {
		this.parent = parent;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	@Override
	public String toString() {
		return "AuthDto [id=" + id + ", name=" + name + ", children=" + children + ", checked=" + checked + "]";
	}
	
}
