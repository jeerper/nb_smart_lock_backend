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
public class PU_IO_DETECTOR_INFO extends Structure {
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u01f7\ufffd\u02b9\ufffd\ufffd */
	public boolean uiIOEnable;
	/** \ufffd\ufffd\ufffd\u8e32\ufffd\u01f5\u0133\ufffd\ufffd\ufffd\ufffd\ufffd\u02b9\ufffd\ufffdbitmap 0\u03bb\ufffd\ufffd1\ufffd\ufffd\u02be1\ufffd\ufffd\ufffd\ufffd\u02b9\ufffd\u0723\ufffd\ufffd\u0534\ufffd\ufffd\ufffd\ufffd\ufffd */
	public NativeLong uiIOLaneId;
	/**
	 * \ufffd\ufffd\u0226\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_IO_DETECTOR_INFO_S
	 */
	public PU_ITS_IO_DETECTOR_INFO stCfgIODetector;
	/** C type : CHAR[32] */
	public byte[] szReserve = new byte[32];
	public PU_IO_DETECTOR_INFO() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("uiIOEnable", "uiIOLaneId", "stCfgIODetector", "szReserve");
	}
	/**
	 * @param uiIOEnable \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u01f7\ufffd\u02b9\ufffd\ufffd<br>
	 * @param uiIOLaneId \ufffd\ufffd\ufffd\u8e32\ufffd\u01f5\u0133\ufffd\ufffd\ufffd\ufffd\ufffd\u02b9\ufffd\ufffdbitmap 0\u03bb\ufffd\ufffd1\ufffd\ufffd\u02be1\ufffd\ufffd\ufffd\ufffd\u02b9\ufffd\u0723\ufffd\ufffd\u0534\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * @param stCfgIODetector \ufffd\ufffd\u0226\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_IO_DETECTOR_INFO_S<br>
	 * @param szReserve C type : CHAR[32]
	 */
	public PU_IO_DETECTOR_INFO(boolean uiIOEnable, NativeLong uiIOLaneId, PU_ITS_IO_DETECTOR_INFO stCfgIODetector, byte szReserve[]) {
		super();
		this.uiIOEnable = uiIOEnable;
		this.uiIOLaneId = uiIOLaneId;
		this.stCfgIODetector = stCfgIODetector;
		if ((szReserve.length != this.szReserve.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserve = szReserve;
	}
	public PU_IO_DETECTOR_INFO(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_IO_DETECTOR_INFO implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_IO_DETECTOR_INFO implements Structure.ByValue {
		
	};
}
