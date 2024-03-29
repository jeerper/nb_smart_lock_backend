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
public class PU_FACE_INFO_FIND_S extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChannelId;
	/**
	 * \ufffd\ufffd\ufffd<br>
	 * C type : PU_FACE_LIB_S
	 */
	public PU_FACE_LIB_S stFacelib;
	/**
	 * \ufffd\ufffd\u046f\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_FACE_FIND_CONDITION_S
	 */
	public FACE_FIND_CONDITION stCondition;
	/** \ufffd\ufffd\u046f\ufffd\ufffd\ufffd\ufffd\ufffd\u013f */
	public int uFindNum;
	/** \ufffd\ufffd\u02bc\ufffd\ufffd\u046fID */
	public int uStartIndex;
	/**
	 * \ufffd\ufffd\u046f\ufffd\ufffd\ufffd\u00b7\ufffd\ufffd \ufffd\u8c78\ufffd\ufffd\ufffd\ufffd\u04bb\ufffd\ufffdjson\ufffd\u013c\ufffd\u00b7\ufffd\ufffd/tmp/mmcblk0/facelist.json\ufffd\ufffd\ufffd\ufffd\ufffd\u0536\u053f\u037b\ufffd\ufffd\u02f2\ufffd\ufffd\u027c\ufffd<br>
	 * C type : CHAR[128 + 1]
	 */
	public byte[] szFindResultPath = new byte[128 + 1];
	/** C type : CHAR[32] */
	public byte[] szReserve = new byte[32];
	public PU_FACE_INFO_FIND_S() {
		this.setAlignType(ALIGN_NONE);
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "stFacelib", "stCondition", "uFindNum", "uStartIndex", "szFindResultPath", "szReserve");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffdID<br>
	 * @param stFacelib \ufffd\ufffd\ufffd<br>
	 * C type : PU_FACE_LIB_S<br>
	 * @param stCondition \ufffd\ufffd\u046f\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_FACE_FIND_CONDITION_S<br>
	 * @param uFindNum \ufffd\ufffd\u046f\ufffd\ufffd\ufffd\ufffd\ufffd\u013f<br>
	 * @param uStartIndex \ufffd\ufffd\u02bc\ufffd\ufffd\u046fID<br>
	 * @param szFindResultPath \ufffd\ufffd\u046f\ufffd\ufffd\ufffd\u00b7\ufffd\ufffd \ufffd\u8c78\ufffd\ufffd\ufffd\ufffd\u04bb\ufffd\ufffdjson\ufffd\u013c\ufffd\u00b7\ufffd\ufffd/tmp/mmcblk0/facelist.json\ufffd\ufffd\ufffd\ufffd\ufffd\u0536\u053f\u037b\ufffd\ufffd\u02f2\ufffd\ufffd\u027c\ufffd<br>
	 * C type : CHAR[128 + 1]<br>
	 * @param szReserve C type : CHAR[32]
	 */
	public PU_FACE_INFO_FIND_S(NativeLong ulChannelId, PU_FACE_LIB_S stFacelib, FACE_FIND_CONDITION stCondition, int uFindNum, int uStartIndex, byte szFindResultPath[], byte szReserve[]) {
		super();
		this.ulChannelId = ulChannelId;
		this.stFacelib = stFacelib;
		this.stCondition = stCondition;
		this.uFindNum = uFindNum;
		this.uStartIndex = uStartIndex;
		if ((szFindResultPath.length != this.szFindResultPath.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szFindResultPath = szFindResultPath;
		if ((szReserve.length != this.szReserve.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserve = szReserve;
	}
	public PU_FACE_INFO_FIND_S(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_FACE_INFO_FIND_S implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_FACE_INFO_FIND_S implements Structure.ByValue {
		
	};
}
