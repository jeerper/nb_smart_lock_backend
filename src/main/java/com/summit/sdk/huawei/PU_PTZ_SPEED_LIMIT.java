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
public class PU_PTZ_SPEED_LIMIT extends Structure {
	/** \u02ee\u01bd\ufffd\u0676\ufffd\ufffd\ufffd\ufffd\ufffd(\ufffd\ufffd\u03bb:\ufffd\ufffd/\ufffd\ufffd) */
	public NativeLong ulPanSpeedLimit;
	/** \ufffd\ufffd\u05b1\ufffd\u0676\ufffd\ufffd\ufffd\ufffd\ufffd(\ufffd\ufffd\u03bb:\ufffd\ufffd/\ufffd\ufffd) */
	public NativeLong ulTiltSpeedLimit;
	public PU_PTZ_SPEED_LIMIT() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulPanSpeedLimit", "ulTiltSpeedLimit");
	}
	/**
	 * @param ulPanSpeedLimit \u02ee\u01bd\ufffd\u0676\ufffd\ufffd\ufffd\ufffd\ufffd(\ufffd\ufffd\u03bb:\ufffd\ufffd/\ufffd\ufffd)<br>
	 * @param ulTiltSpeedLimit \ufffd\ufffd\u05b1\ufffd\u0676\ufffd\ufffd\ufffd\ufffd\ufffd(\ufffd\ufffd\u03bb:\ufffd\ufffd/\ufffd\ufffd)
	 */
	public PU_PTZ_SPEED_LIMIT(NativeLong ulPanSpeedLimit, NativeLong ulTiltSpeedLimit) {
		super();
		this.ulPanSpeedLimit = ulPanSpeedLimit;
		this.ulTiltSpeedLimit = ulTiltSpeedLimit;
	}
	public PU_PTZ_SPEED_LIMIT(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_PTZ_SPEED_LIMIT implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_PTZ_SPEED_LIMIT implements Structure.ByValue {
		
	};
}
