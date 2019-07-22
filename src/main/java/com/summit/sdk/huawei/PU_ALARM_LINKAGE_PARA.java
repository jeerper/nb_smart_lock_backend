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
public class PU_ALARM_LINKAGE_PARA extends Structure {
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0377\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulChannelNum;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulDoNum;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0377\ufffd\ufffd\u03e2\ufffd\u0431\ufffd<br>
	 * C type : PU_ALARM_LINKAGE_CAMERA_INFO[4]
	 */
	public PU_ALARM_LINKAGE_CAMERA_INFO[] stCameraList = new PU_ALARM_LINKAGE_CAMERA_INFO[4];
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdID<br>
	 * C type : ULONG[4]
	 */
	public NativeLong[] szDoIdList = new NativeLong[4];
	/**
	 * szReserved[0]\ufffd\ufffd\u02be\ufffd\u01f7\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02bc\ufffd\ufffd\ufffd\ufffd\ufffd 0:\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd 1:\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[32]
	 */
	public byte[] szReserved = new byte[32];
	public PU_ALARM_LINKAGE_PARA() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelNum", "ulDoNum", "stCameraList", "szDoIdList", "szReserved");
	}
	/**
	 * @param ulChannelNum \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0377\ufffd\ufffd\ufffd\ufffd<br>
	 * @param ulDoNum \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * @param stCameraList \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0377\ufffd\ufffd\u03e2\ufffd\u0431\ufffd<br>
	 * C type : PU_ALARM_LINKAGE_CAMERA_INFO[4]<br>
	 * @param szDoIdList \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdID<br>
	 * C type : ULONG[4]<br>
	 * @param szReserved szReserved[0]\ufffd\ufffd\u02be\ufffd\u01f7\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02bc\ufffd\ufffd\ufffd\ufffd\ufffd 0:\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd 1:\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[32]
	 */
	public PU_ALARM_LINKAGE_PARA(NativeLong ulChannelNum, NativeLong ulDoNum, PU_ALARM_LINKAGE_CAMERA_INFO stCameraList[], NativeLong szDoIdList[], byte szReserved[]) {
		super();
		this.ulChannelNum = ulChannelNum;
		this.ulDoNum = ulDoNum;
		if ((stCameraList.length != this.stCameraList.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.stCameraList = stCameraList;
		if ((szDoIdList.length != this.szDoIdList.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szDoIdList = szDoIdList;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_ALARM_LINKAGE_PARA(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_ALARM_LINKAGE_PARA implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_ALARM_LINKAGE_PARA implements Structure.ByValue {
		
	};
}
