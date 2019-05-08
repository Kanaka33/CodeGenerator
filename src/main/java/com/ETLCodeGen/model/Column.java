package com.ETLCodeGen.model;

public class Column {
	String columnName;
	String dataType;
	double length;
	Boolean isNull;
	Boolean isPrimaryKey;
	String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public Boolean getIsNull() {
		return isNull;
	}

	public void setIsNull(Boolean isNull) {
		this.isNull = isNull;
	}

	public Boolean getIsPrimaryKey() {
		return isPrimaryKey;
	}

	public void setIsPrimaryKey(Boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

}
