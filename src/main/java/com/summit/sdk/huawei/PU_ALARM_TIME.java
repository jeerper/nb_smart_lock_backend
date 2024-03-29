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
public class PU_ALARM_TIME extends Structure {
	/** \u046d\ufffd\ufffd\ufffd\ufffd\u02bd\ufffd\ufffd0\ufffd\ufffd\ufffd\ufffd\u046d\ufffd\ufffd\ufffd\ufffd1\ufffd\ufffd\u00ff\ufffd\ufffd\u046d\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulCycleType;
	/** 0\ufffd\ufffd\u00ff\ufffd\uc8ec1-7\ufffd\ufffd\ufffd\ufffd\ufffd\u04bb\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulPeriod;
	/**
	 * \ufffd\ufffd\u02bc\u02b1\ufffd\ufffd<br>
	 * C type : PU_TIME_S
	 */
	public PU_TIME stStart;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\u02b1\ufffd\ufffd<br>
	 * C type : PU_TIME_S
	 */
	public PU_TIME stEnd;
	public PU_ALARM_TIME() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulCycleType", "ulPeriod", "stStart", "stEnd");
	}
	/**
	 * @param ulCycleType \u046d\ufffd\ufffd\ufffd\ufffd\u02bd\ufffd\ufffd0\ufffd\ufffd\ufffd\ufffd\u046d\ufffd\ufffd\ufffd\ufffd1\ufffd\ufffd\u00ff\ufffd\ufffd\u046d\ufffd\ufffd\ufffd\ufffd<br>
	 * @param ulPeriod 0\ufffd\ufffd\u00ff\ufffd\uc8ec1-7\ufffd\ufffd\ufffd\ufffd\ufffd\u04bb\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * @param stStart \ufffd\ufffd\u02bc\u02b1\ufffd\ufffd<br>
	 * C type : PU_TIME_S<br>
	 * @param stEnd \ufffd\ufffd\ufffd\ufffd\u02b1\ufffd\ufffd<br>
	 * C type : PU_TIME_S
	 */
	public PU_ALARM_TIME(NativeLong ulCycleType, NativeLong ulPeriod, PU_TIME stStart, PU_TIME stEnd) {
		super();
		this.ulCycleType = ulCycleType;
		this.ulPeriod = ulPeriod;
		this.stStart = stStart;
		this.stEnd = stEnd;
	}
	public PU_ALARM_TIME(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_ALARM_TIME implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_ALARM_TIME implements Structure.ByValue {
		
	};
}
