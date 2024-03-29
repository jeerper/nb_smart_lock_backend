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
public class PU_RECT_LOCATE_PARA extends Structure {
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_DRAW_DIR_E
	 */
	public int enDirection;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\u03e2<br>
	 * C type : PU_AREA_INFO_S
	 */
	public PU_AREA_INFO stRect;
	public PU_RECT_LOCATE_PARA() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("enDirection", "stRect");
	}
	/**
	 * @param enDirection \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_DRAW_DIR_E<br>
	 * @param stRect \ufffd\ufffd\ufffd\ufffd\u03e2<br>
	 * C type : PU_AREA_INFO_S
	 */
	public PU_RECT_LOCATE_PARA(int enDirection, PU_AREA_INFO stRect) {
		super();
		this.enDirection = enDirection;
		this.stRect = stRect;
	}
	public PU_RECT_LOCATE_PARA(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_RECT_LOCATE_PARA implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_RECT_LOCATE_PARA implements Structure.ByValue {
		
	};
}
