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
public class PU_TD_MD_AREA extends Structure {
	public NativeLong ulX1;
	public NativeLong ulY1;
	public NativeLong ulX2;
	public NativeLong ulY2;
	public PU_TD_MD_AREA() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulX1", "ulY1", "ulX2", "ulY2");
	}
	public PU_TD_MD_AREA(NativeLong ulX1, NativeLong ulY1, NativeLong ulX2, NativeLong ulY2) {
		super();
		this.ulX1 = ulX1;
		this.ulY1 = ulY1;
		this.ulX2 = ulX2;
		this.ulY2 = ulY2;
	}
	public PU_TD_MD_AREA(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_TD_MD_AREA implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_TD_MD_AREA implements Structure.ByValue {
		
	};
}
