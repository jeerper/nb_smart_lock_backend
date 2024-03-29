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
public class PU_FACE_OSD_PARA extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChannelId;
	/** \ufffd\u01f7\ufffd\ufffd\ufffd\u02b5\ufffd\ufffdOSD\ufffd\ufffd\ufffd\ufffd */
	public boolean bEnableLiveOSD;
	/**
	 * \ufffd\ufffd\ufffd\ufffdOSD\ufffd\ufffd\u037c\u01ac\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_OVERLAY_PIC_TYPE_E
	 */
	public int enOverlayPicType;
	/**
	 * \u037c\u01ac\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03e2<br>
	 * C type : PU_FACE_OSD_INFO_S
	 */
	public PU_FACE_OSD_INFO stPicOSDInfo;
	/**
	 * \u02b1\ufffd\ufffdOSD\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_TIME_OSD_PARA_S
	 */
	public PU_TIME_OSD_PARA stTimeOSDPara;
	/**
	 * \ufffd\u8c78\ufffd\ufffd\u0172\ufffd\ufffd\ufffd<br>
	 * C type : PU_CUSTOM_OSD_PARA_EX_S
	 */
	public PU_CUSTOM_OSD_PARA_EX stDevIdOSDPara;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\u03bb\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_CUSTOM_OSD_PARA_EX_S
	 */
	public PU_CUSTOM_OSD_PARA_EX stMonitorInfoOSDPara;
	/** C type : CHAR[256] */
	public byte[] szReserved = new byte[256];
	public PU_FACE_OSD_PARA() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "bEnableLiveOSD", "enOverlayPicType", "stPicOSDInfo", "stTimeOSDPara", "stDevIdOSDPara", "stMonitorInfoOSDPara", "szReserved");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffdID<br>
	 * @param bEnableLiveOSD \ufffd\u01f7\ufffd\ufffd\ufffd\u02b5\ufffd\ufffdOSD\ufffd\ufffd\ufffd\ufffd<br>
	 * @param enOverlayPicType \ufffd\ufffd\ufffd\ufffdOSD\ufffd\ufffd\u037c\u01ac\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_OVERLAY_PIC_TYPE_E<br>
	 * @param stPicOSDInfo \u037c\u01ac\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03e2<br>
	 * C type : PU_FACE_OSD_INFO_S<br>
	 * @param stTimeOSDPara \u02b1\ufffd\ufffdOSD\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_TIME_OSD_PARA_S<br>
	 * @param stDevIdOSDPara \ufffd\u8c78\ufffd\ufffd\u0172\ufffd\ufffd\ufffd<br>
	 * C type : PU_CUSTOM_OSD_PARA_EX_S<br>
	 * @param stMonitorInfoOSDPara \ufffd\ufffd\ufffd\ufffd\u03bb\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_CUSTOM_OSD_PARA_EX_S<br>
	 * @param szReserved C type : CHAR[256]
	 */
	public PU_FACE_OSD_PARA(NativeLong ulChannelId, boolean bEnableLiveOSD, int enOverlayPicType, PU_FACE_OSD_INFO stPicOSDInfo, PU_TIME_OSD_PARA stTimeOSDPara, PU_CUSTOM_OSD_PARA_EX stDevIdOSDPara, PU_CUSTOM_OSD_PARA_EX stMonitorInfoOSDPara, byte szReserved[]) {
		super();
		this.ulChannelId = ulChannelId;
		this.bEnableLiveOSD = bEnableLiveOSD;
		this.enOverlayPicType = enOverlayPicType;
		this.stPicOSDInfo = stPicOSDInfo;
		this.stTimeOSDPara = stTimeOSDPara;
		this.stDevIdOSDPara = stDevIdOSDPara;
		this.stMonitorInfoOSDPara = stMonitorInfoOSDPara;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_FACE_OSD_PARA(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_FACE_OSD_PARA implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_FACE_OSD_PARA implements Structure.ByValue {
		
	};
}
