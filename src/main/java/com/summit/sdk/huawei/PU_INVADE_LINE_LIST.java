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
public class PU_INVADE_LINE_LIST extends Structure {
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0427\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulLineNum;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_INVADE_LINE_S[5]
	 */
	public PU_INVADE_LINE[] stLine = new PU_INVADE_LINE[5];
	public PU_INVADE_LINE_LIST() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulLineNum", "stLine");
	}
	/**
	 * @param ulLineNum \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0427\ufffd\ufffd\ufffd\ufffd<br>
	 * @param stLine \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_INVADE_LINE_S[5]
	 */
	public PU_INVADE_LINE_LIST(NativeLong ulLineNum, PU_INVADE_LINE stLine[]) {
		super();
		this.ulLineNum = ulLineNum;
		if ((stLine.length != this.stLine.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.stLine = stLine;
	}
	public PU_INVADE_LINE_LIST(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_INVADE_LINE_LIST implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_INVADE_LINE_LIST implements Structure.ByValue {
		
	};
}
