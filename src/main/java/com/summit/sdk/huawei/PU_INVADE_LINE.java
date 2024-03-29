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
public class PU_INVADE_LINE extends Structure {
	/** \ufffd\ufffd\u2dfd\ufffd\ufffd0\ufffd\ufffd\u02eb\ufffd\ufffd1\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u04a3\ufffd2\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulDirection;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02bc\ufffd\ufffd<br>
	 * C type : PU_INVADE_POINT_S
	 */
	public PU_INVADE_POINT stStartPoint;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\u07f5\ufffd\ufffd\u0575\ufffd<br>
	 * C type : PU_INVADE_POINT_S
	 */
	public PU_INVADE_POINT stEndPoint;
	public PU_INVADE_LINE() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulDirection", "stStartPoint", "stEndPoint");
	}
	/**
	 * @param ulDirection \ufffd\ufffd\u2dfd\ufffd\ufffd0\ufffd\ufffd\u02eb\ufffd\ufffd1\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u04a3\ufffd2\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * @param stStartPoint \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02bc\ufffd\ufffd<br>
	 * C type : PU_INVADE_POINT_S<br>
	 * @param stEndPoint \ufffd\ufffd\ufffd\ufffd\u07f5\ufffd\ufffd\u0575\ufffd<br>
	 * C type : PU_INVADE_POINT_S
	 */
	public PU_INVADE_LINE(NativeLong ulDirection, PU_INVADE_POINT stStartPoint, PU_INVADE_POINT stEndPoint) {
		super();
		this.ulDirection = ulDirection;
		this.stStartPoint = stStartPoint;
		this.stEndPoint = stEndPoint;
	}
	public PU_INVADE_LINE(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_INVADE_LINE implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_INVADE_LINE implements Structure.ByValue {
		
	};
}
