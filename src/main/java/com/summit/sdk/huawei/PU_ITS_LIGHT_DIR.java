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
public class PU_ITS_LIGHT_DIR extends Structure {
	/** \ufffd\ufffd\u05ea */
	public boolean bLeft;
	/** \u05b1\ufffd\ufffd */
	public boolean bStraight;
	/** \ufffd\ufffd\u05ea */
	public boolean bRight;
	public PU_ITS_LIGHT_DIR() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("bLeft", "bStraight", "bRight");
	}
	/**
	 * @param bLeft \ufffd\ufffd\u05ea<br>
	 * @param bStraight \u05b1\ufffd\ufffd<br>
	 * @param bRight \ufffd\ufffd\u05ea
	 */
	public PU_ITS_LIGHT_DIR(boolean bLeft, boolean bStraight, boolean bRight) {
		super();
		this.bLeft = bLeft;
		this.bStraight = bStraight;
		this.bRight = bRight;
	}
	public PU_ITS_LIGHT_DIR(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_ITS_LIGHT_DIR implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_ITS_LIGHT_DIR implements Structure.ByValue {
		
	};
}
