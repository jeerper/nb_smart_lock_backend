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
public class PU_MANUAL_CALIBRATION_CONFIG extends Structure {
	/** \ufffd\uada8\ufffd\ufffd\ufffd\u0538\ufffd\ufffd\ufffd */
	public int iPairNum;
	/** C type : PU_MANUAL_CALIBRATION_PAIR_S[14] */
	public PU_MANUAL_CALIBRATION_PAIR[] stPair = new PU_MANUAL_CALIBRATION_PAIR[14];
	public PU_MANUAL_CALIBRATION_CONFIG() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("iPairNum", "stPair");
	}
	/**
	 * @param iPairNum \ufffd\uada8\ufffd\ufffd\ufffd\u0538\ufffd\ufffd\ufffd<br>
	 * @param stPair C type : PU_MANUAL_CALIBRATION_PAIR_S[14]
	 */
	public PU_MANUAL_CALIBRATION_CONFIG(int iPairNum, PU_MANUAL_CALIBRATION_PAIR stPair[]) {
		super();
		this.iPairNum = iPairNum;
		if ((stPair.length != this.stPair.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.stPair = stPair;
	}
	public PU_MANUAL_CALIBRATION_CONFIG(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_MANUAL_CALIBRATION_CONFIG implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_MANUAL_CALIBRATION_CONFIG implements Structure.ByValue {
		
	};
}
