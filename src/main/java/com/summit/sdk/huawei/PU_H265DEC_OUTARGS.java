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
public class PU_H265DEC_OUTARGS extends Structure {
	/** \u0368\ufffd\ufffdID */
	public int uiChannelID;
	/** \ufffd\ufffd\u0135\ufffd\ufffd\u05bd\ufffd\ufffd\ufffd */
	public int uiBytsConsumed;
	/** \u02b1\ufffd\ufffd\ufffd */
	public long uiTimeStamp;
	/**
	 * \u05a1\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_H265D_FRAMETYPE_E
	 */
	public int enFrameType;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u05f4\u032c<br>
	 * C type : PU_H265D_DECODESTATUS_E
	 */
	public int enDecodeStatus;
	/** \u037c\ufffd\ufffd\ufffd */
	public int uiDecWidth;
	/** \u037c\ufffd\ufffd\ufffd */
	public int uiDecHeight;
	/** Y\ufffd\ufffd\ufffd\ufffdstride */
	public int uiYStride;
	/** U/V\ufffd\ufffd\ufffd\ufffdstride */
	public int uiUVStride;
	/**
	 * YUV\ufffd\ufffd\u05b7\ufffd\ufffd\ufffd\ufffd\ufffd\u03b4\ufffd\ufffdYUV<br>
	 * C type : UCHAR*[3]
	 */
	public Pointer[] pucOutYUV = new Pointer[3];
	/** \u052d\u02bc\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd */
	public int uiCodingBytesOfCurFrm;
	/**
	 * \ufffd\u00fb\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_H265D_USERDATA_S
	 */
	public PU_H265D_USERDATA stUserData;
	/** \ufffd\ufffd\ufffd\u03b1\ufffd\ufffd\ufffd\u03e2 */
	public int uiAspectRatioIdc;
	/** \ufffd\ufffd\ufffd\u03b1\u023f\ufffd\u0223\ufffd\ufffd\ufffduiAspectRatioIdc \u03aa255\u02b1\ufffd\ufffd\u0427 */
	public int uiSarWidth;
	/** \ufffd\ufffd\ufffd\u03b1\u0238\u07f6\u0223\ufffd\ufffd\ufffduiAspectRatioIdc \u03aa255\u02b1\ufffd\ufffd\u0427 */
	public int uiSarHeight;
	/** \u05a1\ufffd\ufffd\ufffd\ufffd\u03e2 */
	public int uiVpsNumUnitsInTick;
	/** \u05a1\ufffd\ufffd\ufffd\ufffd\u03e2 */
	public int uiVpsTimeScale;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\u01f0\u05a1\ufffd\ufffd\u03e2, \ufffd\ufffd\u04bb\u05a1\ufffd\u0438\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdCU\ufffd\u0138\ufffd\ufffd\ufffd<br>
	 * C type : PU_CU_OUTPUT_INFO_S
	 */
	public PU_CU_OUTPUT_INFO stCuOutInfo;
	/** \u05a1\ufffd\ufffd\ufffd\ufffd\ufffd\u05be\ufffd\ufffd0:\ufffd\u07b4\ud8ec1:\ufffd\u0434\ufffd */
	public int iIsError;
	public PU_H265DEC_OUTARGS() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("uiChannelID", "uiBytsConsumed", "uiTimeStamp", "enFrameType", "enDecodeStatus", "uiDecWidth", "uiDecHeight", "uiYStride", "uiUVStride", "pucOutYUV", "uiCodingBytesOfCurFrm", "stUserData", "uiAspectRatioIdc", "uiSarWidth", "uiSarHeight", "uiVpsNumUnitsInTick", "uiVpsTimeScale", "stCuOutInfo", "iIsError");
	}
	public PU_H265DEC_OUTARGS(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_H265DEC_OUTARGS implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_H265DEC_OUTARGS implements Structure.ByValue {
		
	};
}
