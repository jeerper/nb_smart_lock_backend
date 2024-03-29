package com.summit.sdk.huawei;
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
public class PU_FLICKER_MODE extends Structure {
	/**
	 * \ufffd\ufffd\ufffd\ufffd\u01b5\ufffd\ufffd<br>
	 * C type : PU_VIDEO_FLICKER_MODE_E
	 */
	public int enFlicker;
	public PU_FLICKER_MODE() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("enFlicker");
	}
	/**
	 * @param enFlicker \ufffd\ufffd\ufffd\ufffd\u01b5\ufffd\ufffd<br>
	 * C type : PU_VIDEO_FLICKER_MODE_E
	 */
	public PU_FLICKER_MODE(int enFlicker) {
		super();
		this.enFlicker = enFlicker;
	}
	public PU_FLICKER_MODE(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_FLICKER_MODE implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_FLICKER_MODE implements Structure.ByValue {
		
	};
}
