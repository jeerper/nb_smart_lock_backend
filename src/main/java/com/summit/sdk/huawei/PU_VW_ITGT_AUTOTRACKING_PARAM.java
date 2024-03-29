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
public class PU_VW_ITGT_AUTOTRACKING_PARAM extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChannelId;
	/** \ufffd\u3de8\u02b9\ufffd\ufffd */
	public boolean bEnable;
	/** \ufffd\u3de8\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulSensitivity;
	/** \ufffd\u6faf\ufffd\u03f1\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulAlarmTime;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02b1\ufffd\ufffd */
	public NativeLong ulMaxTraceTime;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u01bb\ufffd<br>
	 * C type : PU_ALARM_TIME_LIST_S
	 */
	public PU_ALARM_TIME_PARA_LIST stAlarmTimeList;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_IGT_AERADTC_LIST_S
	 */
	public PU_IGT_AERADTC_LIST szAreaList;
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_VW_ITGT_AUTOTRACKING_PARAM() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "bEnable", "ulSensitivity", "ulAlarmTime", "ulMaxTraceTime", "stAlarmTimeList", "szAreaList", "szReserved");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffdID<br>
	 * @param bEnable \ufffd\u3de8\u02b9\ufffd\ufffd<br>
	 * @param ulSensitivity \ufffd\u3de8\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * @param ulAlarmTime \ufffd\u6faf\ufffd\u03f1\ufffd\ufffd\ufffd\ufffd<br>
	 * @param ulMaxTraceTime \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02b1\ufffd\ufffd<br>
	 * @param stAlarmTimeList \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u01bb\ufffd<br>
	 * C type : PU_ALARM_TIME_LIST_S<br>
	 * @param szAreaList \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_IGT_AERADTC_LIST_S<br>
	 * @param szReserved C type : CHAR[32]
	 */
	public PU_VW_ITGT_AUTOTRACKING_PARAM(NativeLong ulChannelId, boolean bEnable, NativeLong ulSensitivity, NativeLong ulAlarmTime, NativeLong ulMaxTraceTime, PU_ALARM_TIME_PARA_LIST stAlarmTimeList, PU_IGT_AERADTC_LIST szAreaList, byte szReserved[]) {
		super();
		this.ulChannelId = ulChannelId;
		this.bEnable = bEnable;
		this.ulSensitivity = ulSensitivity;
		this.ulAlarmTime = ulAlarmTime;
		this.ulMaxTraceTime = ulMaxTraceTime;
		this.stAlarmTimeList = stAlarmTimeList;
		this.szAreaList = szAreaList;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_VW_ITGT_AUTOTRACKING_PARAM(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_VW_ITGT_AUTOTRACKING_PARAM implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_VW_ITGT_AUTOTRACKING_PARAM implements Structure.ByValue {
		
	};
}
