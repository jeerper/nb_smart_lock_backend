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
public class PU_ROI_INFO extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChannelId;
	/**
	 * \ufffd\ufffd\ufffd\ufffdID<br>
	 * C type : PU_STREAM_TYPE_E
	 */
	public int enStreamId;
	/** \u02b9\ufffd\ufffd */
	public NativeLong lEnable;
	/** \ufffd\ufffd\u0427\ufffd\ufffdROI\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u013f */
	public NativeLong ulROINum;
	/**
	 * ROI\ufffd\ufffd\ufffd\u03b2\ufffd\ufffd\ufffd<br>
	 * C type : PU_ROI_AREA_INFO_S[8]
	 */
	public PU_ROI_AREA_INFO[] stROIAreaInfo = new PU_ROI_AREA_INFO[8];
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_ROI_INFO() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "enStreamId", "lEnable", "ulROINum", "stROIAreaInfo", "szReserved");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffdID<br>
	 * @param enStreamId \ufffd\ufffd\ufffd\ufffdID<br>
	 * C type : PU_STREAM_TYPE_E<br>
	 * @param lEnable \u02b9\ufffd\ufffd<br>
	 * @param ulROINum \ufffd\ufffd\u0427\ufffd\ufffdROI\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u013f<br>
	 * @param stROIAreaInfo ROI\ufffd\ufffd\ufffd\u03b2\ufffd\ufffd\ufffd<br>
	 * C type : PU_ROI_AREA_INFO_S[8]<br>
	 * @param szReserved C type : CHAR[32]
	 */
	public PU_ROI_INFO(NativeLong ulChannelId, int enStreamId, NativeLong lEnable, NativeLong ulROINum, PU_ROI_AREA_INFO stROIAreaInfo[], byte szReserved[]) {
		super();
		this.ulChannelId = ulChannelId;
		this.enStreamId = enStreamId;
		this.lEnable = lEnable;
		this.ulROINum = ulROINum;
		if ((stROIAreaInfo.length != this.stROIAreaInfo.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.stROIAreaInfo = stROIAreaInfo;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_ROI_INFO(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_ROI_INFO implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_ROI_INFO implements Structure.ByValue {
		
	};
}
