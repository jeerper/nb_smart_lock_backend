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
public class PU_ITGT_CROWD_DENSITY_DETECT_ALG_PARAM extends Structure {
	/**
	 * \ufffd\u3de8\u02b9\ufffd\ufffd<br>
	 * C type : PU_ENABLE_TYPE_E
	 */
	public int enEnable;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u05b5 */
	public NativeLong fPeopleNumThreshold;
	/** \ufffd\u6faf\ufffd\u03f1\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulAlarmTime;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\u05b5(1-5) */
	public NativeLong ulSensitivity;
	/** C type : CHAR[32] */
	public byte[] szReserve = new byte[32];
	public PU_ITGT_CROWD_DENSITY_DETECT_ALG_PARAM() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("enEnable", "fPeopleNumThreshold", "ulAlarmTime", "ulSensitivity", "szReserve");
	}
	/**
	 * @param enEnable \ufffd\u3de8\u02b9\ufffd\ufffd<br>
	 * C type : PU_ENABLE_TYPE_E<br>
	 * @param fPeopleNumThreshold \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u05b5<br>
	 * @param ulAlarmTime \ufffd\u6faf\ufffd\u03f1\ufffd\ufffd\ufffd\ufffd<br>
	 * @param ulSensitivity \ufffd\ufffd\ufffd\ufffd\ufffd\u05b5(1-5)<br>
	 * @param szReserve C type : CHAR[32]
	 */
	public PU_ITGT_CROWD_DENSITY_DETECT_ALG_PARAM(int enEnable, NativeLong fPeopleNumThreshold, NativeLong ulAlarmTime, NativeLong ulSensitivity, byte szReserve[]) {
		super();
		this.enEnable = enEnable;
		this.fPeopleNumThreshold = fPeopleNumThreshold;
		this.ulAlarmTime = ulAlarmTime;
		this.ulSensitivity = ulSensitivity;
		if ((szReserve.length != this.szReserve.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserve = szReserve;
	}
	public PU_ITGT_CROWD_DENSITY_DETECT_ALG_PARAM(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_ITGT_CROWD_DENSITY_DETECT_ALG_PARAM implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_ITGT_CROWD_DENSITY_DETECT_ALG_PARAM implements Structure.ByValue {
		
	};
}
