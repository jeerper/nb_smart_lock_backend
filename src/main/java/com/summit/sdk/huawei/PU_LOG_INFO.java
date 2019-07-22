package com.summit.sdk.huawei;
import com.sun.jna.NativeLong;
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
public class PU_LOG_INFO extends Structure {
	public NativeLong ulTotalNum;
	public NativeLong ulBeginIndex;
	public NativeLong ulEndIndex;
	/** C type : CHAR[100][256] */
	public byte[] szLogInfoList = new byte[((100) * (256))];
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_LOG_INFO() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulTotalNum", "ulBeginIndex", "ulEndIndex", "szLogInfoList", "szReserved");
	}
	/**
	 * @param szLogInfoList C type : CHAR[100][256]<br>
	 * @param szReserved C type : CHAR[32]
	 */
	public PU_LOG_INFO(NativeLong ulTotalNum, NativeLong ulBeginIndex, NativeLong ulEndIndex, byte szLogInfoList[], byte szReserved[]) {
		super();
		this.ulTotalNum = ulTotalNum;
		this.ulBeginIndex = ulBeginIndex;
		this.ulEndIndex = ulEndIndex;
		if ((szLogInfoList.length != this.szLogInfoList.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szLogInfoList = szLogInfoList;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_LOG_INFO(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_LOG_INFO implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_LOG_INFO implements Structure.ByValue {
		
	};
}
