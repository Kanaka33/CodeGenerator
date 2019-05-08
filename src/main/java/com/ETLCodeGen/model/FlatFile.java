package com.ETLCodeGen.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class FlatFile {

    String fileName;
    String fileType;
    String delimited;
    String delimiters;
    String delimitersInBinary;
    String quoteCharacter;
    String nullCharacter;
    String nullCharType;
    YesNoEnum stageFileReInit;
    YesNoEnum stageFilePersist;
    String repeatable;
    String consecDelimitersAsOne;
    String stripTrailingBlanks;
    String padBytes;
    String skipLeadingBytes;
    String skipTrailingBytes;
    String lineSequential;
    String keepEscapeChar;
    String shiftSensitiveData;
    String escapeCharacter;
    String skipRows;
    String codePage;
    String multidelimiters;
    String rowDelimeter;
    
    @XmlAttribute(name="ROWDELIMITER") 
    public String getRowDelimeter() {
		return rowDelimeter;
	}

	public void setRowDelimeter(String rowDelimeter) {
		this.rowDelimeter = rowDelimeter;
	}

	@XmlAttribute(name="MULTIDELIMITERSASAND")
    public String getMultidelimiters() {
		return multidelimiters;
	}

	public void setMultidelimiters(String multidelimiters) {
		this.multidelimiters = multidelimiters;
	}

	@XmlAttribute(name="FILENAME")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @XmlAttribute(name="FILETYPE")
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @XmlAttribute(name="DELIMITED")
    public String getDelimited() {
        return delimited;
    }

    public void setDelimited(String delimited) {
        this.delimited = delimited;
    }

    @XmlAttribute(name="DELIMITERS")
    public String getDelimiters() {
        return delimiters;
    }

    public void setDelimiters(String delimiters) {
        this.delimiters = delimiters;
    }

    @XmlAttribute(name="DELIMITERS_IN_BINARY")
    public String getDelimitersInBinary() {
        return delimitersInBinary;
    }

    public void setDelimitersInBinary(String delimitersInBinary) {
        this.delimitersInBinary = delimitersInBinary;
    }

    @XmlAttribute(name="QUOTE_CHARACTER")
    public String getQuoteCharacter() {
        return quoteCharacter;
    }

    public void setQuoteCharacter(String quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

    @XmlAttribute(name="NULL_CHARACTER")
    public String getNullCharacter() {
        return nullCharacter;
    }

    public void setNullCharacter(String nullCharacter) {
        this.nullCharacter = nullCharacter;
    }

    @XmlAttribute(name="NULLCHARTYPE")
    public String getNullCharType() {
        return nullCharType;
    }

    public void setNullCharType(String string) {
        this.nullCharType = string;
    }

    @XmlAttribute(name="STAGEFILEREINIT")
    public YesNoEnum getStageFileReInit() {
        return stageFileReInit;
    }

    public void setStageFileReInit(YesNoEnum stageFileReInit) {
        this.stageFileReInit = stageFileReInit;
    }

    @XmlAttribute(name="STAGEFILEPERSIST")
    public YesNoEnum getStageFilePersist() {
        return stageFilePersist;
    }

    public void setStageFilePersist(YesNoEnum stageFilePersist) {
        this.stageFilePersist = stageFilePersist;
    }

    @XmlAttribute(name="REPEATABLE")
    public String getRepeatable() {
        return repeatable;
    }

    public void setRepeatable(String repeatable) {
        this.repeatable = repeatable;
    }

    @XmlAttribute(name="CONSECDELIMITERSASONE")
    public String getConsecDelimitersAsOne() {
        return consecDelimitersAsOne;
    }

    public void setConsecDelimitersAsOne(String consecDelimitersAsOne) {
        this.consecDelimitersAsOne = consecDelimitersAsOne;
    }

    @XmlAttribute(name="STRIPTRAILINGBLANKS")
    public String getStripTrailingBlanks() {
        return stripTrailingBlanks;
    }

    public void setStripTrailingBlanks(String stripTrailingBlanks) {
        this.stripTrailingBlanks = stripTrailingBlanks;
    }

    @XmlAttribute(name="PADBYTES")
    public String getPadBytes() {
        return padBytes;
    }

    public void setPadBytes(String padBytes) {
        this.padBytes = padBytes;
    }

    @XmlAttribute(name="SKIPLEADINGBYTES")
    public String getSkipLeadingBytes() {
        return skipLeadingBytes;
    }

    public void setSkipLeadingBytes(String skipLeadingBytes) {
        this.skipLeadingBytes = skipLeadingBytes;
    }

    @XmlAttribute(name="SKIPTRAILINGBYTES")
    public String getSkipTrailingBytes() {
        return skipTrailingBytes;
    }

    public void setSkipTrailingBytes(String skipTrailingBytes) {
        this.skipTrailingBytes = skipTrailingBytes;
    }

    @XmlAttribute(name="LINESEQUENTIAL")
    public String getLineSequential() {
        return lineSequential;
    }

    public void setLineSequential(String lineSequential) {
        this.lineSequential = lineSequential;
    }

    @XmlAttribute(name="KEEPESCAPECHAR")
    public String getKeepEscapeChar() {
        return keepEscapeChar;
    }

    public void setKeepEscapeChar(String keepEscapeChar) {
        this.keepEscapeChar = keepEscapeChar;
    }

    @XmlAttribute(name="SHIFTSENSITIVEDATA")
    public String getShiftSensitiveData() {
        return shiftSensitiveData;
    }

    public void setShiftSensitiveData(String shiftSensitiveData) {
        this.shiftSensitiveData = shiftSensitiveData;
    }

    @XmlAttribute(name="ESCAPE_CHARACTER")
    public String getEscapeCharacter() {
        return escapeCharacter;
    }

    public void setEscapeCharacter(String escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    @XmlAttribute(name="SKIPROWS")
    public String getSkipRows() {
        return skipRows;
    }

    public void setSkipRows(String skipRows) {
        this.skipRows = skipRows;
    }

    @XmlAttribute(name="CODEPAGE")
    public String getCodePage() {
        return codePage;
    }

    public void setCodePage(String codePage) {
        this.codePage = codePage;
    }
}
