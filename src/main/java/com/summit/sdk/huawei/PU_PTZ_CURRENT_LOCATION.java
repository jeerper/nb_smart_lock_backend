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
public class PU_PTZ_CURRENT_LOCATION extends Structure {
	/** \u02ee\u01bd\ufffd\u01f6\ufffd(0~359) */
	public NativeLong lPTZHorDegree;
	/** \ufffd\ufffd\u05b1\ufffd\u01f6\ufffd(-10~90) */
	public NativeLong lPTZVerDegree;
	/** \ufffd\ufffd\u0377\ufffd\u4c76\ufffd\ufffd\ufffd\ufffd\ufffd(1~30\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u05b1\u4c76\ufffd\ufffd\u03aa0\ufffd\ufffd\u0377\ufffd\u4c76\u012c\ufffd\ufffd\u03aa\ufffd\ufffd\ufffd\u05b530) */
	public NativeLong ulLenMultiple;
	/** \ufffd\ufffd\u0377\ufffd\u4c76\u0421\ufffd\ufffd\ufffd(0~99\ufffd\ufffd\u05a7\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd\u04bb\u03bb\ufffd\ufffd\ufffd\u05a3\ufffd */
	public NativeLong ulDotLenMultiple;
	/** \ufffd\ufffd\ufffd\u05b1\u4c76\ufffd\ufffd\ufffd\ufffd\ufffd(0~12\ufffd\ufffd\ufffd\ufffd\u0377\ufffd\u4c76\ufffd\ufd7d\ufffd\ufffd\ufffd\u05b5\ufffd\ufffd\ufffd\ufffd\ufffd\u00e3\ufffd0\ufffd\ufffd\u02be\ufffd\ufffd\ufffd\ufffd\ufffd\u00e3\ufffd\ufffd\u0731\u4c76\ufffd\ufffd\u03aa\ufffd\ufffd\u02f9\ufffd\u03f5) */
	public NativeLong ulZoomRatio;
	/** \ufffd\ufffd\ufffd\u05b1\u4c76\u0421\ufffd\ufffd\ufffd(0~99\ufffd\ufffd\u05a7\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd\u04bb\u03bb\ufffd\ufffd\ufffd\u05a3\ufffd */
	public NativeLong ulDotZoomRatio;
	/** \u02ee\u01bd\ufffd\u01f6\ufffd\u0421\ufffd\ufffd\ufffd(0~99\ufffd\ufffd\u05a7\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd\u04bb\u03bb\ufffd\ufffd\ufffd\u05a3\ufffd */
	public NativeLong lPTZDotHorDegree;
	/** \ufffd\ufffd\u05b1\ufffd\u01f6\ufffd\u0421\ufffd\ufffd\ufffd(-99~99\ufffd\ufffd\u05a7\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd\u04bb\u03bb\ufffd\ufffd\ufffd\u05a3\ufffd */
	public NativeLong lPTZDotVerDegree;
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_PTZ_CURRENT_LOCATION() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("lPTZHorDegree", "lPTZVerDegree", "ulLenMultiple", "ulDotLenMultiple", "ulZoomRatio", "ulDotZoomRatio", "lPTZDotHorDegree", "lPTZDotVerDegree", "szReserved");
	}
	/**
	 * @param lPTZHorDegree \u02ee\u01bd\ufffd\u01f6\ufffd(0~359)<br>
	 * @param lPTZVerDegree \ufffd\ufffd\u05b1\ufffd\u01f6\ufffd(-10~90)<br>
	 * @param ulLenMultiple \ufffd\ufffd\u0377\ufffd\u4c76\ufffd\ufffd\ufffd\ufffd\ufffd(1~30\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u05b1\u4c76\ufffd\ufffd\u03aa0\ufffd\ufffd\u0377\ufffd\u4c76\u012c\ufffd\ufffd\u03aa\ufffd\ufffd\ufffd\u05b530)<br>
	 * @param ulDotLenMultiple \ufffd\ufffd\u0377\ufffd\u4c76\u0421\ufffd\ufffd\ufffd(0~99\ufffd\ufffd\u05a7\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd\u04bb\u03bb\ufffd\ufffd\ufffd\u05a3\ufffd<br>
	 * @param ulZoomRatio \ufffd\ufffd\ufffd\u05b1\u4c76\ufffd\ufffd\ufffd\ufffd\ufffd(0~12\ufffd\ufffd\ufffd\ufffd\u0377\ufffd\u4c76\ufffd\ufd7d\ufffd\ufffd\ufffd\u05b5\ufffd\ufffd\ufffd\ufffd\ufffd\u00e3\ufffd0\ufffd\ufffd\u02be\ufffd\ufffd\ufffd\ufffd\ufffd\u00e3\ufffd\ufffd\u0731\u4c76\ufffd\ufffd\u03aa\ufffd\ufffd\u02f9\ufffd\u03f5)<br>
	 * @param ulDotZoomRatio \ufffd\ufffd\ufffd\u05b1\u4c76\u0421\ufffd\ufffd\ufffd(0~99\ufffd\ufffd\u05a7\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd\u04bb\u03bb\ufffd\ufffd\ufffd\u05a3\ufffd<br>
	 * @param lPTZDotHorDegree \u02ee\u01bd\ufffd\u01f6\ufffd\u0421\ufffd\ufffd\ufffd(0~99\ufffd\ufffd\u05a7\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd\u04bb\u03bb\ufffd\ufffd\ufffd\u05a3\ufffd<br>
	 * @param lPTZDotVerDegree \ufffd\ufffd\u05b1\ufffd\u01f6\ufffd\u0421\ufffd\ufffd\ufffd(-99~99\ufffd\ufffd\u05a7\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd\u04bb\u03bb\ufffd\ufffd\ufffd\u05a3\ufffd<br>
	 * @param szReserved C type : CHAR[32]
	 */
	public PU_PTZ_CURRENT_LOCATION(NativeLong lPTZHorDegree, NativeLong lPTZVerDegree, NativeLong ulLenMultiple, NativeLong ulDotLenMultiple, NativeLong ulZoomRatio, NativeLong ulDotZoomRatio, NativeLong lPTZDotHorDegree, NativeLong lPTZDotVerDegree, byte szReserved[]) {
		super();
		this.lPTZHorDegree = lPTZHorDegree;
		this.lPTZVerDegree = lPTZVerDegree;
		this.ulLenMultiple = ulLenMultiple;
		this.ulDotLenMultiple = ulDotLenMultiple;
		this.ulZoomRatio = ulZoomRatio;
		this.ulDotZoomRatio = ulDotZoomRatio;
		this.lPTZDotHorDegree = lPTZDotHorDegree;
		this.lPTZDotVerDegree = lPTZDotVerDegree;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_PTZ_CURRENT_LOCATION(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_PTZ_CURRENT_LOCATION implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_PTZ_CURRENT_LOCATION implements Structure.ByValue {
		
	};
}
