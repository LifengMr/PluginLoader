package com.letv.zxing.ex;

public class ParseResultEntity {
	public final static int URI = 1;	//对应ParsedResultType的URI
	public final static int TEXT = 2;	//对应ParsedResultType的TEXT，其余对应类型用到时再补TODO
	
	private String displayResult;  //ParsedResult.getDisplayResult()
	private String text;	//Result.getText()
	private int type;	//ParsedResult.getType() ParsedResultType转换成int类型
	
	public String getDisplayResult() {
		return displayResult;
	}
	public void setDisplayResult(String displayResult) {
		this.displayResult = displayResult;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
