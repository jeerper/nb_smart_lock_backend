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
public class PU_SLAVE_DEVICE_ENABLE extends Structure {
	/** \ufffd\ufffd\ufffd\u8c78ID */
	public NativeLong ulSlaveID;
	/** \u02b9\ufffd\u0731\ufffd\u05be\u03bb */
	public boolean bEnable;
	public PU_SLAVE_DEVICE_ENABLE() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulSlaveID", "bEnable");
	}
	/**
	 * @param ulSlaveID \ufffd\ufffd\ufffd\u8c78ID<br>
	 * @param bEnable \u02b9\ufffd\u0731\ufffd\u05be\u03bb
	 */
	public PU_SLAVE_DEVICE_ENABLE(NativeLong ulSlaveID, boolean bEnable) {
		super();
		this.ulSlaveID = ulSlaveID;
		this.bEnable = bEnable;
	}
	public PU_SLAVE_DEVICE_ENABLE(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_SLAVE_DEVICE_ENABLE implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_SLAVE_DEVICE_ENABLE implements Structure.ByValue {
		
	};
}
