package com.ETLCodeGen.model;

import java.util.List;

public class TableDetails {
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<Column> getColumn() {
		return column;
	}
	public void setColumn(List<Column> column) {
		this.column = column;
	}
	String tableName;
	List<Column> column;

}
