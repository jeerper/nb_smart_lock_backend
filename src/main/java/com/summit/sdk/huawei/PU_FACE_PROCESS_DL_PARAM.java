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
public class PU_FACE_PROCESS_DL_PARAM extends Structure {
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u037c */
	public boolean bSendFaceImg;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u037c */
	public boolean bSendBodyImg;
	/** \ufffd\ufffd\ufffd\ufffd\u022b\ufffd\ufffd\u037c */
	public boolean bSendPanorama;
	/** \u037c\ufffd\ufffd\ufffd\ufffd\u01ff */
	public boolean bFaceEnhancement;
	/** \ufffd\ufffd\ufffd\ufffd\u037c\u05e5\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd(1-10) */
	public NativeLong ulFaceImgQuality;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\u037c\ufffd\ufffd\ufffd\ufffd(1-10) */
	public NativeLong ulBodyImgQuality;
	/** \u022b\ufffd\ufffd\u037c\u05e5\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd(1-10) */
	public NativeLong ulPanoramaImgQuality;
	/** \ufffd\ufffd\ufffd\u0375\ufffd\ufffd\ufffd\ufffd\ufffd\u0421\u037c\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulSendFaceNum;
	/** \ufffd\ufffd\ufffd\u0375\ufffd\ufffd\ufffd\ufffd\ufffd\u0421\u037c\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulSendBodyNum;
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_FACE_PROCESS_DL_PARAM() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("bSendFaceImg", "bSendBodyImg", "bSendPanorama", "bFaceEnhancement", "ulFaceImgQuality", "ulBodyImgQuality", "ulPanoramaImgQuality", "ulSendFaceNum", "ulSendBodyNum", "szReserved");
	}
	public PU_FACE_PROCESS_DL_PARAM(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_FACE_PROCESS_DL_PARAM implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_FACE_PROCESS_DL_PARAM implements Structure.ByValue {
		
	};
}
