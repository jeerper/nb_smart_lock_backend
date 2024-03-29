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
public class PU_PIC_OSD_PARA_V2 extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChnID;
	/**
	 * \ufffd\ufffd\ufffd\ufffdOSD\ufffd\ufffd\u037c\u01ac\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_OVERLAY_PIC_TYPE_E
	 */
	public int enOverlayPicType;
	/**
	 * \u037c\u01ac\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03e2<br>
	 * C type : PU_PIC_OSD_INFO_S
	 */
	public PU_PIC_OSD_INFO_S stPicOSDInfo;
	/**
	 * \u02b1\ufffd\ufffdOSD\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_TIME_OSD_PARA_S
	 */
	public PU_TIME_OSD_PARA stTimeOSDPara;
	/**
	 * \u00b7\ufffd\u06b1\ufffd\u0172\ufffd\ufffd\ufffd<br>
	 * C type : PU_CUSTOM_OSD_PARA_S
	 */
	public PU_CUSTOM_OSD_PARA stRoadIdOSDPara;
	/**
	 * \ufffd\u8c78\ufffd\ufffd\u0172\ufffd\ufffd\ufffd<br>
	 * C type : PU_CUSTOM_OSD_PARA_S
	 */
	public PU_CUSTOM_OSD_PARA stDevIdOSDPara;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\u0172\ufffd\ufffd\ufffd<br>
	 * C type : PU_CUSTOM_OSD_PARA_S
	 */
	public PU_CUSTOM_OSD_PARA stDirIdOSDPara;
	/**
	 * \ufffd\ufffd\ufffd\ufffdOSD\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_CUSTOM_OSD_PARA_S
	 */
	public PU_CUSTOM_OSD_PARA stDirOSDPara;
	/**
	 * \ufffd\u0536\ufffd\ufffd\ufffdOSD\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_CUSTOM_OSD_PARA_S
	 */
	public PU_CUSTOM_OSD_PARA stCustomOSDPara;
	/** \ufffd\ufffd\u02bb\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02b9\ufffd\u0723\ufffd1:\ufffd\ufffd\u02beITS\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u00f7\ufffd\ufffd\ufffdOSD\ufffd\ufffd\ufffd\ufffd 0:\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u00f7\ufffd\ufffd\ufffdOSD\ufffd\ufffd\ufffd\ufffd */
	public boolean bDirOSDParaEn;
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_PIC_OSD_PARA_V2() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChnID", "enOverlayPicType", "stPicOSDInfo", "stTimeOSDPara", "stRoadIdOSDPara", "stDevIdOSDPara", "stDirIdOSDPara", "stDirOSDPara", "stCustomOSDPara", "bDirOSDParaEn", "szReserved");
	}
	public PU_PIC_OSD_PARA_V2(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_PIC_OSD_PARA_V2 implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_PIC_OSD_PARA_V2 implements Structure.ByValue {
		
	};
}
