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
public class PU_LIGHT_DETECTOR_INFO extends Structure {
	/**
	 * @see PU_EXTRA_DEVICE_TYPE_E<br>
	 * \ufffd\ufffd\ufffd\ufffd\u042d\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_EXTRA_DEVICE_TYPE_E
	 */
	public int enDeviceProtelType;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u01f7\ufffd\u02b9\ufffd\ufffd */
	public NativeLong uiLightRoadEnable;
	/** \ufffd\ufffd\ufffd\u8e32\ufffd\u01f5\u0133\ufffd\ufffd\ufffd\ufffd\ufffd\u02b9\ufffd\ufffdbitmap 0\u03bb\ufffd\ufffd1\ufffd\ufffd\u02be1\ufffd\ufffd\ufffd\ufffd\u02b9\ufffd\u0723\ufffd\ufffd\u0534\ufffd\ufffd\ufffd\ufffd\ufffd */
	public NativeLong uiLightLaneId;
	/**
	 * @see PU_ITS_SERIAL_PORT_E<br>
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02b9\ufffd\u00f5\u0134\ufffd\ufffd\u06ba\ufffd<br>
	 * C type : PU_ITS_SERIAL_PORT_E
	 */
	public int enLightSerialPortId;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_RS_PARA_S
	 */
	public PU_ITS_RS_PARA_S stLightDevSerialCfg;
	/**
	 * \ufffd\ufffd\u00b7\ufffd\ufffdTL-01-8\ufffd\ufffd\ufffd\u0335\ufffd\ufffd\u017a\u017c\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_XLW_DETECTOR_INFO_S
	 */
	public PU_ITS_XLW_DETECTOR_INFO stCfgLightDetector;
	/** C type : CHAR[32] */
	public byte[] szReserve = new byte[32];
	public PU_LIGHT_DETECTOR_INFO() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("enDeviceProtelType", "uiLightRoadEnable", "uiLightLaneId", "enLightSerialPortId", "stLightDevSerialCfg", "stCfgLightDetector", "szReserve");
	}
	/**
	 * @param enDeviceProtelType @see PU_EXTRA_DEVICE_TYPE_E<br>
	 * \ufffd\ufffd\ufffd\ufffd\u042d\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_EXTRA_DEVICE_TYPE_E<br>
	 * @param uiLightRoadEnable \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u01f7\ufffd\u02b9\ufffd\ufffd<br>
	 * @param uiLightLaneId \ufffd\ufffd\ufffd\u8e32\ufffd\u01f5\u0133\ufffd\ufffd\ufffd\ufffd\ufffd\u02b9\ufffd\ufffdbitmap 0\u03bb\ufffd\ufffd1\ufffd\ufffd\u02be1\ufffd\ufffd\ufffd\ufffd\u02b9\ufffd\u0723\ufffd\ufffd\u0534\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * @param enLightSerialPortId @see PU_ITS_SERIAL_PORT_E<br>
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02b9\ufffd\u00f5\u0134\ufffd\ufffd\u06ba\ufffd<br>
	 * C type : PU_ITS_SERIAL_PORT_E<br>
	 * @param stLightDevSerialCfg \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_RS_PARA_S<br>
	 * @param stCfgLightDetector \ufffd\ufffd\u00b7\ufffd\ufffdTL-01-8\ufffd\ufffd\ufffd\u0335\ufffd\ufffd\u017a\u017c\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_XLW_DETECTOR_INFO_S<br>
	 * @param szReserve C type : CHAR[32]
	 */
	public PU_LIGHT_DETECTOR_INFO(int enDeviceProtelType, NativeLong uiLightRoadEnable, NativeLong uiLightLaneId, int enLightSerialPortId, PU_ITS_RS_PARA_S stLightDevSerialCfg, PU_ITS_XLW_DETECTOR_INFO stCfgLightDetector, byte szReserve[]) {
		super();
		this.enDeviceProtelType = enDeviceProtelType;
		this.uiLightRoadEnable = uiLightRoadEnable;
		this.uiLightLaneId = uiLightLaneId;
		this.enLightSerialPortId = enLightSerialPortId;
		this.stLightDevSerialCfg = stLightDevSerialCfg;
		this.stCfgLightDetector = stCfgLightDetector;
		if ((szReserve.length != this.szReserve.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserve = szReserve;
	}
	public PU_LIGHT_DETECTOR_INFO(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_LIGHT_DETECTOR_INFO implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_LIGHT_DETECTOR_INFO implements Structure.ByValue {
		
	};
}
