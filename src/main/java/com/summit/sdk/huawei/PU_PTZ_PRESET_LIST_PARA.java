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
public class PU_PTZ_PRESET_LIST_PARA extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChannelId;
	/** \u0524\ufffd\ufffd\u03bb\ufffd\ufffd\u02bc\ufffd\ufffd\ufffd */
	public NativeLong ulBeginIndex;
	/** \u0524\ufffd\ufffd\u03bb\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulEndIndex;
	/** \ufffd\ufffd\ufffd\u0635\ufffd\u0524\ufffd\ufffd\u03bb\u02b5\ufffd\u02b8\ufffd\ufffd\ufffd */
	public NativeLong ulTotalNum;
	/**
	 * \u0524\ufffd\ufffd\u03bb\ufffd\ufffd\u03e2<br>
	 * C type : PU_PTZ_PRESET_INFO_S[10]
	 */
	public PU_PTZ_PRESET_INFO[] stPtzPresetInfo = new PU_PTZ_PRESET_INFO[10];
	public PU_PTZ_PRESET_LIST_PARA() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "ulBeginIndex", "ulEndIndex", "ulTotalNum", "stPtzPresetInfo");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffdID<br>
	 * @param ulBeginIndex \u0524\ufffd\ufffd\u03bb\ufffd\ufffd\u02bc\ufffd\ufffd\ufffd<br>
	 * @param ulEndIndex \u0524\ufffd\ufffd\u03bb\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * @param ulTotalNum \ufffd\ufffd\ufffd\u0635\ufffd\u0524\ufffd\ufffd\u03bb\u02b5\ufffd\u02b8\ufffd\ufffd\ufffd<br>
	 * @param stPtzPresetInfo \u0524\ufffd\ufffd\u03bb\ufffd\ufffd\u03e2<br>
	 * C type : PU_PTZ_PRESET_INFO_S[10]
	 */
	public PU_PTZ_PRESET_LIST_PARA(NativeLong ulChannelId, NativeLong ulBeginIndex, NativeLong ulEndIndex, NativeLong ulTotalNum, PU_PTZ_PRESET_INFO stPtzPresetInfo[]) {
		super();
		this.ulChannelId = ulChannelId;
		this.ulBeginIndex = ulBeginIndex;
		this.ulEndIndex = ulEndIndex;
		this.ulTotalNum = ulTotalNum;
		if ((stPtzPresetInfo.length != this.stPtzPresetInfo.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.stPtzPresetInfo = stPtzPresetInfo;
	}
	public PU_PTZ_PRESET_LIST_PARA(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_PTZ_PRESET_LIST_PARA implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_PTZ_PRESET_LIST_PARA implements Structure.ByValue {
		
	};
}
