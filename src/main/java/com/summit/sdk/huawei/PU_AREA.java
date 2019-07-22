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
public class PU_AREA extends Structure {
	public NativeLong ulIndex;
	/** C type : PU_ENABLE_TYPE_E */
	public int enEnable;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03b6\ufffd\ufffd\ufffd\ufffd\ufffd\u013f */
	public NativeLong uPointNum;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_VIM_POINT_ST[10]
	 */
	public PU_VIM_POINT[] stPoint = new PU_VIM_POINT[10];
	/**
	 * \ufffd\ufffd\u0328\u03bb\ufffd\ufffd\ufffd\ufffd\u03e2<br>
	 * C type : LOCATION_INFO_S
	 */
	public LOCATION_INFO stLocation;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[(32 + 4)]
	 */
	public byte[] stAreaName = new byte[32 + 4];
	public PU_AREA() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulIndex", "enEnable", "uPointNum", "stPoint", "stLocation", "stAreaName");
	}
	/**
	 * @param enEnable C type : PU_ENABLE_TYPE_E<br>
	 * @param uPointNum \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03b6\ufffd\ufffd\ufffd\ufffd\ufffd\u013f<br>
	 * @param stPoint \ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_VIM_POINT_ST[10]<br>
	 * @param stLocation \ufffd\ufffd\u0328\u03bb\ufffd\ufffd\ufffd\ufffd\u03e2<br>
	 * C type : LOCATION_INFO_S<br>
	 * @param stAreaName \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[(32 + 4)]
	 */
	public PU_AREA(NativeLong ulIndex, int enEnable, NativeLong uPointNum, PU_VIM_POINT stPoint[], LOCATION_INFO stLocation, byte stAreaName[]) {
		super();
		this.ulIndex = ulIndex;
		this.enEnable = enEnable;
		this.uPointNum = uPointNum;
		if ((stPoint.length != this.stPoint.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.stPoint = stPoint;
		this.stLocation = stLocation;
		if ((stAreaName.length != this.stAreaName.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.stAreaName = stAreaName;
	}
	public PU_AREA(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_AREA implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_AREA implements Structure.ByValue {
		
	};
}
