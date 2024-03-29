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
public class PU_CUSTOM_OSD_PARA_EX extends Structure {
	/**
	 * OSD\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[(176 + 1)]
	 */
	public byte[] acOSDContent = new byte[176 + 1];
	public PU_CUSTOM_OSD_PARA_EX() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("acOSDContent");
	}
	/**
	 * @param acOSDContent OSD\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[(176 + 1)]
	 */
	public PU_CUSTOM_OSD_PARA_EX(byte acOSDContent[]) {
		super();
		if ((acOSDContent.length != this.acOSDContent.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.acOSDContent = acOSDContent;
	}
	public PU_CUSTOM_OSD_PARA_EX(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_CUSTOM_OSD_PARA_EX implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_CUSTOM_OSD_PARA_EX implements Structure.ByValue {
		
	};
}
