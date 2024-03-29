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
public class PU_VIDEO_ATTRIBUTE extends Structure {
	/** \ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulBitRate;
	/** I\u05a1\ufffd\ufffd\ufffd */
	public NativeLong ulIFrameInterval;
	/** \u05a1\ufffd\ufffd */
	public NativeLong ulFrameRate;
	/**
	 * \ufffd\ufffd\u01b5\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_ENCODE_TYPE_E
	 */
	public int enVideoEncodeMode;
	/**
	 * \ufffd\u05b1\ufffd\ufffd\ufffd<br>
	 * C type : PU_RESOLUTION_TYPE_E
	 */
	public int enResolution;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_BITRATE_TYPE_E
	 */
	public int enBitRateType;
	/**
	 * \u037c\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_PIC_QUALITY_E
	 */
	public int enPicQuality;
	/**
	 * \u037c\ufffd\ufffd\ufffd\ufffd\u02bd<br>
	 * C type : PU_VIDEO_FORMAT_E
	 */
	public int enVideoFormat;
	/**
	 * \u05a1\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0123\u02bd,\ufffd\ufffdH264\ufffd\ufffd\ufffd\ufffd\u0427<br>
	 * C type : PU_H264_RC_PRI_E
	 */
	public int enRcPriMode;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\u0123\u02bd(\u02b5\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0123\u02bd\ufffd\ufffd\u0427)<br>
	 * C type : PU_VIDEO_MIRROR_MODE_E
	 */
	public int enMirrorMode;
	/**
	 * szReserve[0]\ufffd\ufffd\u02b6\ufffd\ufffd\ufffd\ube34\ufffd\u04f6\ufffd: H264 0 BP,   1 MP,     2 HP<br>
	 * C type : CHAR[32]
	 */
	public byte[] szReserve = new byte[32];
	public PU_VIDEO_ATTRIBUTE() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulBitRate", "ulIFrameInterval", "ulFrameRate", "enVideoEncodeMode", "enResolution", "enBitRateType", "enPicQuality", "enVideoFormat", "enRcPriMode", "enMirrorMode", "szReserve");
	}
	public PU_VIDEO_ATTRIBUTE(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_VIDEO_ATTRIBUTE implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_VIDEO_ATTRIBUTE implements Structure.ByValue {
		
	};
}
