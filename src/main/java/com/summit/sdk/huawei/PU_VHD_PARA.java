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
public abstract class PU_VHD_PARA extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChannelId;
	/** \ufffd\u01f7\ufffd\u02b9\ufffd\ufffd */
	public boolean bEnable;
	/** \ufffd\u03f4\ufffd\u013f\ufffd\ufffd\u0421\u037c */
	public boolean bSendSnapPicture;
	/** \ufffd\u03f4\ufffd\u022b\ufffd\ufffd */
	public boolean bSendPanorama;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u01f7\ufffd\u02b9\ufffd\ufffd */
	public boolean bLaneLineEnable;
	/**
	 * \u05e5\ufffd\ufffd\ufffd\u3de8\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_VLPR_ALG_MODE_E
	 */
	public int enAlgMode;
	/** \ufffd\ufffd\ufffd\ufffd\u037c\ufffd\ufffd\ufffd\ufffd */
	public int iWidth;
	/** \ufffd\ufffd\ufffd\ufffd\u037c\ufffd\ufffd\u07f6\ufffd */
	public int iHeight;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd */
	public int iLaneNum;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd = \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd+1<br>
	 * C type : PU_IMRS_LINE_S[((4) + 1)]
	 */
	public PU_IMRS_LINE[] stLane = new PU_IMRS_LINE[(4) + 1];
	/**
	 * \ufffd\ufffd\ufffd\u0233\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0363\ufffd\ufffd\ufffd\ufffd6\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd6\ufffd\ufffd\u02216\ufffd\ufffd\ufffd\ufffd\ufffd\u7870\ufffd\ufffd\ufffd\ufffd\ufffd\u3871<br>
	 * C type : CHAR[(24)]
	 */
	public byte[] szPlateLocalZH = new byte[24];
	/** \ufffd\u0639\ufffd\u02b9\ufffd\ufffd */
	public boolean bExposureEnable;
	/** \ufffd\u0639\ufffd\u03f5\ufffd\ufffd */
	public byte ulROIMeterLevel;
	/** Conversion Error : sizeof(BOOL) */
	public PU_VHD_PARA() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "bEnable", "bSendSnapPicture", "bSendPanorama", "bLaneLineEnable", "enAlgMode", "iWidth", "iHeight", "iLaneNum", "stLane", "szPlateLocalZH", "bExposureEnable", "ulROIMeterLevel");
	}
	public PU_VHD_PARA(Pointer peer) {
		super(peer);
	}
	public static abstract class ByReference extends PU_VHD_PARA implements Structure.ByReference {
		
	};
	public static abstract class ByValue extends PU_VHD_PARA implements Structure.ByValue {
		
	};
}
