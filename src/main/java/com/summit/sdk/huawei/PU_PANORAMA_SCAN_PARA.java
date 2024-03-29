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
public class PU_PANORAMA_SCAN_PARA extends Structure {
	/** \ufffd\u0676\ufffd */
	public NativeLong ulSpeed;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\u05a7\ufffd\ufffd\ufffd\ufffd/\ufffd\ufffd<br>
	 * C type : PU_PTZ_DIRECTION_E
	 */
	public int enDirection;
	public PU_PANORAMA_SCAN_PARA() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulSpeed", "enDirection");
	}
	/**
	 * @param ulSpeed \ufffd\u0676\ufffd<br>
	 * @param enDirection \ufffd\ufffd\ufffd\ufffd\u05a7\ufffd\ufffd\ufffd\ufffd/\ufffd\ufffd<br>
	 * C type : PU_PTZ_DIRECTION_E
	 */
	public PU_PANORAMA_SCAN_PARA(NativeLong ulSpeed, int enDirection) {
		super();
		this.ulSpeed = ulSpeed;
		this.enDirection = enDirection;
	}
	public PU_PANORAMA_SCAN_PARA(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_PANORAMA_SCAN_PARA implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_PANORAMA_SCAN_PARA implements Structure.ByValue {
		
	};
}
