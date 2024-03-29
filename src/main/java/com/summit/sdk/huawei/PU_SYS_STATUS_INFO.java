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
public class PU_SYS_STATUS_INFO extends Structure {
	/** CPU\u057c\ufffd\ufffd\ufffd\ufffd */
	public float fCpuOccupancyRate;
	/** \ufffd\u8c78\ufffd\ufffd\ufffd\u06b4\ufffd(KB) */
	public NativeLong ulMemTotal;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\u06b4\ufffd(KB) */
	public NativeLong ulMemFree;
	/** BUFF\ufffd\u06b4\ufffd(KB) */
	public NativeLong ulMemBuffer;
	/** CACHE\ufffd\u06b4\ufffd(KB) */
	public NativeLong ulMemCache;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdbyte */
	public NativeLong ulRecvRate;
	/** \ufffd\ufffd\ufffd\u7de2\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdbyte */
	public NativeLong ulSendRate;
	/** \ufffd\u0739\ufffdFlash\ufffd\ufffd\u0421(KB) */
	public NativeLong ulFlashTotal;
	/** \ufffd\ufffd\ufffd\ufffdFlash\ufffd\ufffd\u0421(KB) */
	public NativeLong ulFlashFree;
	/** C type : CHAR[32] */
	public byte[] szReserve = new byte[32];
	public PU_SYS_STATUS_INFO() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("fCpuOccupancyRate", "ulMemTotal", "ulMemFree", "ulMemBuffer", "ulMemCache", "ulRecvRate", "ulSendRate", "ulFlashTotal", "ulFlashFree", "szReserve");
	}
	public PU_SYS_STATUS_INFO(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_SYS_STATUS_INFO implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_SYS_STATUS_INFO implements Structure.ByValue {
		
	};
}
