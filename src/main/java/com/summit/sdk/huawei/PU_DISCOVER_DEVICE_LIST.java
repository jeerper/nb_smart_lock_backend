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
public class PU_DISCOVER_DEVICE_LIST extends Structure {
	public NativeLong ulDeviceNum;
	/** C type : PU_DISCOVER_DEVICE_INFO_S[(1000)] */
	public PU_DISCOVER_DEVICE_INFO[] stDeviceInfo = new PU_DISCOVER_DEVICE_INFO[1000];
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_DISCOVER_DEVICE_LIST() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulDeviceNum", "stDeviceInfo", "szReserved");
	}
	/**
	 * @param stDeviceInfo C type : PU_DISCOVER_DEVICE_INFO_S[(1000)]<br>
	 * @param szReserved C type : CHAR[32]
	 */
	public PU_DISCOVER_DEVICE_LIST(NativeLong ulDeviceNum, PU_DISCOVER_DEVICE_INFO stDeviceInfo[], byte szReserved[]) {
		super();
		this.ulDeviceNum = ulDeviceNum;
		if ((stDeviceInfo.length != this.stDeviceInfo.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.stDeviceInfo = stDeviceInfo;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_DISCOVER_DEVICE_LIST(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_DISCOVER_DEVICE_LIST implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_DISCOVER_DEVICE_LIST implements Structure.ByValue {
		
	};
}
