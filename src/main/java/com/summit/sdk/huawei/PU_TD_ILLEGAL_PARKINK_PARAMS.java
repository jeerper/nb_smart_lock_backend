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
public class PU_TD_ILLEGAL_PARKINK_PARAMS extends Structure {
	/** \u03a5\u0363\ufffd\ufffd\ufffd\u02b9\ufffd\ufffd */
	public boolean bEnable;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : BOOL[(4)]
	 */
	public boolean[] abRelatedLane = new boolean[4];
	/** \ufffd\ufffd\ufffd\ufffd\ufffd(\u013f\u01f0\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u05e5\ufffd\u0139\ufffd\ufffd\ufffd) */
	public NativeLong ulDetectInterval;
	/**
	 * \u05e5\ufffd\u0139\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_ILLEGAL_PARKINK_SNAP_RULE_S
	 */
	public PU_TD_ILLEGAL_PARKINK_SNAP_RULE stSnapRules;
	public PU_TD_ILLEGAL_PARKINK_PARAMS() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("bEnable", "abRelatedLane", "ulDetectInterval", "stSnapRules");
	}
	/**
	 * @param bEnable \u03a5\u0363\ufffd\ufffd\ufffd\u02b9\ufffd\ufffd<br>
	 * @param abRelatedLane \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : BOOL[(4)]<br>
	 * @param ulDetectInterval \ufffd\ufffd\ufffd\ufffd\ufffd(\u013f\u01f0\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u05e5\ufffd\u0139\ufffd\ufffd\ufffd)<br>
	 * @param stSnapRules \u05e5\ufffd\u0139\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_ILLEGAL_PARKINK_SNAP_RULE_S
	 */
	public PU_TD_ILLEGAL_PARKINK_PARAMS(boolean bEnable, boolean abRelatedLane[], NativeLong ulDetectInterval, PU_TD_ILLEGAL_PARKINK_SNAP_RULE stSnapRules) {
		super();
		this.bEnable = bEnable;
		if ((abRelatedLane.length != this.abRelatedLane.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.abRelatedLane = abRelatedLane;
		this.ulDetectInterval = ulDetectInterval;
		this.stSnapRules = stSnapRules;
	}
	public PU_TD_ILLEGAL_PARKINK_PARAMS(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_TD_ILLEGAL_PARKINK_PARAMS implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_TD_ILLEGAL_PARKINK_PARAMS implements Structure.ByValue {
		
	};
}
