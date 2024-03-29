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
public class PU_GRAPH_INFO extends Structure {
	public double dPointX;
	public double dPointY;
	/** 3d\ufffd\ufffd\u03bb\u02b1\ufffd\u02f4\ufffd\u03aa\ufffd\u06b6\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulWidth;
	public NativeLong ulHeight;
	/** \ufffd\ufffd\ufffd\ufffdRT_NORMAL  = 0, RT_PRIVACY = 1, RT_ROI = 2 */
	public NativeLong ulType;
	public NativeLong ulGraphID;
	public NativeLong lROIQuality;
	public NativeLong ulFrom;
	public NativeLong ulRectangleType;
	public PU_GRAPH_INFO() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("dPointX", "dPointY", "ulWidth", "ulHeight", "ulType", "ulGraphID", "lROIQuality", "ulFrom", "ulRectangleType");
	}
	/**
	 * @param ulWidth 3d\ufffd\ufffd\u03bb\u02b1\ufffd\u02f4\ufffd\u03aa\ufffd\u06b6\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * @param ulType \ufffd\ufffd\ufffd\ufffdRT_NORMAL  = 0, RT_PRIVACY = 1, RT_ROI = 2
	 */
	public PU_GRAPH_INFO(double dPointX, double dPointY, NativeLong ulWidth, NativeLong ulHeight, NativeLong ulType, NativeLong ulGraphID, NativeLong lROIQuality, NativeLong ulFrom, NativeLong ulRectangleType) {
		super();
		this.dPointX = dPointX;
		this.dPointY = dPointY;
		this.ulWidth = ulWidth;
		this.ulHeight = ulHeight;
		this.ulType = ulType;
		this.ulGraphID = ulGraphID;
		this.lROIQuality = lROIQuality;
		this.ulFrom = ulFrom;
		this.ulRectangleType = ulRectangleType;
	}
	public PU_GRAPH_INFO(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_GRAPH_INFO implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_GRAPH_INFO implements Structure.ByValue {
		
	};
}
