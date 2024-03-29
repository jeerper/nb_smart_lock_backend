package com.summit.sdk.huawei;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
/**
 * <i>native declaration : E:\video\HWPuSDK.h</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class PU_TIME extends Structure {
	/** C type : CHAR[5] */
	public byte[] szYear = new byte[5];
	/** C type : CHAR[3] */
	public byte[] szMonth = new byte[3];
	/** C type : CHAR[3] */
	public byte[] szDay = new byte[3];
	/** C type : CHAR[3] */
	public byte[] szHour = new byte[3];
	/** C type : CHAR[3] */
	public byte[] szMinute = new byte[3];
	/** C type : CHAR[3] */
	public byte[] szSecond = new byte[3];
	public PU_TIME() {
		this.setAlignType(ALIGN_NONE);
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("szYear", "szMonth", "szDay", "szHour", "szMinute", "szSecond");
	}
	/**
	 * @param szYear C type : CHAR[5]<br>
	 * @param szMonth C type : CHAR[3]<br>
	 * @param szDay C type : CHAR[3]<br>
	 * @param szHour C type : CHAR[3]<br>
	 * @param szMinute C type : CHAR[3]<br>
	 * @param szSecond C type : CHAR[3]
	 */
	public PU_TIME(byte szYear[], byte szMonth[], byte szDay[], byte szHour[], byte szMinute[], byte szSecond[]) {
		this.setAlignType(ALIGN_NONE);
		if ((szYear.length != this.szYear.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szYear = szYear;
		if ((szMonth.length != this.szMonth.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szMonth = szMonth;
		if ((szDay.length != this.szDay.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szDay = szDay;
		if ((szHour.length != this.szHour.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szHour = szHour;
		if ((szMinute.length != this.szMinute.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szMinute = szMinute;
		if ((szSecond.length != this.szSecond.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szSecond = szSecond;
	}
	public PU_TIME(Pointer peer) {
		super(peer,ALIGN_NONE);
		read();
	}
	public static class ByReference extends PU_TIME implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_TIME implements Structure.ByValue {
		
	};
}
