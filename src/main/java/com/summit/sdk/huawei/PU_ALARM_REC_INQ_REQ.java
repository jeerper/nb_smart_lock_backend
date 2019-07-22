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
public class PU_ALARM_REC_INQ_REQ extends Structure {
	/** \u0368\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulChnID;
	/**
	 * \ufffd\u6faf\ufffd\ufffd\ufffd\ufffd 0\ufffd\ufffd\u02be\ufffd\ufffd\ufffd\u0438\u6faf<br>
	 * C type : PU_ALARM_TYPE_E
	 */
	public int enAlarmType;
	/**
	 * \ufffd\ufffd\u02bc\u02b1\ufffd\ufffd(UTC\u02b1\ufffd\ufffd)<br>
	 * C type : PU_TIME_S
	 */
	public PU_TIME stBeginTime;
	/**
	 * \ufffd\ufffd\u05b9\u02b1\ufffd\ufffd(UTC\u02b1\ufffd\ufffd)<br>
	 * C type : PU_TIME_S
	 */
	public PU_TIME stEndTime;
	/** \ufffd\ufffd\u02bc\ufffd\ufffd\ufffd\ufffd(\ufffd\ufffd1\ufffd\ufffd\u02bc) */
	public NativeLong ulBeginIndex;
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_ALARM_REC_INQ_REQ() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChnID", "enAlarmType", "stBeginTime", "stEndTime", "ulBeginIndex", "szReserved");
	}
	/**
	 * @param ulChnID \u0368\ufffd\ufffd\ufffd\ufffd<br>
	 * @param enAlarmType \ufffd\u6faf\ufffd\ufffd\ufffd\ufffd 0\ufffd\ufffd\u02be\ufffd\ufffd\ufffd\u0438\u6faf<br>
	 * C type : PU_ALARM_TYPE_E<br>
	 * @param stBeginTime \ufffd\ufffd\u02bc\u02b1\ufffd\ufffd(UTC\u02b1\ufffd\ufffd)<br>
	 * C type : PU_TIME_S<br>
	 * @param stEndTime \ufffd\ufffd\u05b9\u02b1\ufffd\ufffd(UTC\u02b1\ufffd\ufffd)<br>
	 * C type : PU_TIME_S<br>
	 * @param ulBeginIndex \ufffd\ufffd\u02bc\ufffd\ufffd\ufffd\ufffd(\ufffd\ufffd1\ufffd\ufffd\u02bc)<br>
	 * @param szReserved C type : CHAR[32]
	 */
	public PU_ALARM_REC_INQ_REQ(NativeLong ulChnID, int enAlarmType, PU_TIME stBeginTime, PU_TIME stEndTime, NativeLong ulBeginIndex, byte szReserved[]) {
		super();
		this.ulChnID = ulChnID;
		this.enAlarmType = enAlarmType;
		this.stBeginTime = stBeginTime;
		this.stEndTime = stEndTime;
		this.ulBeginIndex = ulBeginIndex;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_ALARM_REC_INQ_REQ(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_ALARM_REC_INQ_REQ implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_ALARM_REC_INQ_REQ implements Structure.ByValue {
		
	};
}
