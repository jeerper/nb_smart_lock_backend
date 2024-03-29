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
public class META_RECT_S extends Structure {
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03f6\ufffd\ufffd\ufffd\ufffdx\ufffd\ufffd\ufffd */
	public short usX;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03f6\ufffd\ufffd\ufffd\ufffdy\ufffd\ufffd\ufffd */
	public short usY;
	/** \ufffd\ufffd\ufffd\u03bf\ufffd */
	public short usWidth;
	/** \ufffd\ufffd\ufffd\u03b8\ufffd */
	public short usHeight;
	public META_RECT_S() {
		this.setAlignType(ALIGN_NONE);
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("usX", "usY", "usWidth", "usHeight");
	}
	/**
	 * @param usX \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03f6\ufffd\ufffd\ufffd\ufffdx\ufffd\ufffd\ufffd<br>
	 * @param usY \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03f6\ufffd\ufffd\ufffd\ufffdy\ufffd\ufffd\ufffd<br>
	 * @param usWidth \ufffd\ufffd\ufffd\u03bf\ufffd<br>
	 * @param usHeight \ufffd\ufffd\ufffd\u03b8\ufffd
	 */
	public META_RECT_S(short usX, short usY, short usWidth, short usHeight) {
		super();
		this.usX = usX;
		this.usY = usY;
		this.usWidth = usWidth;
		this.usHeight = usHeight;
	}
	public META_RECT_S(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends META_RECT_S implements Structure.ByReference {
		
	};
	public static class ByValue extends META_RECT_S implements Structure.ByValue {
		
	};
}
