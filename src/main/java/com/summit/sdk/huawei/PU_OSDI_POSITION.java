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
public class PU_OSDI_POSITION extends Structure {
	/** \u0368\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulChnID;
	/** OSDI\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulOSDIIndex;
	/**
	 * OSDI\ufffd\ufffd\ufffd\u03fd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_POINT_S
	 */
	public PU_POINT stOSDITopLeftPos;
	public PU_OSDI_POSITION() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChnID", "ulOSDIIndex", "stOSDITopLeftPos");
	}
	/**
	 * @param ulChnID \u0368\ufffd\ufffd\ufffd\ufffd<br>
	 * @param ulOSDIIndex OSDI\ufffd\ufffd\ufffd\ufffd<br>
	 * @param stOSDITopLeftPos OSDI\ufffd\ufffd\ufffd\u03fd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_POINT_S
	 */
	public PU_OSDI_POSITION(NativeLong ulChnID, NativeLong ulOSDIIndex, PU_POINT stOSDITopLeftPos) {
		super();
		this.ulChnID = ulChnID;
		this.ulOSDIIndex = ulOSDIIndex;
		this.stOSDITopLeftPos = stOSDITopLeftPos;
	}
	public PU_OSDI_POSITION(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_OSDI_POSITION implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_OSDI_POSITION implements Structure.ByValue {
		
	};
}
