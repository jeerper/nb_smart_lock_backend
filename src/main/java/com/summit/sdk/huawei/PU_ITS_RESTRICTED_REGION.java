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
public class PU_ITS_RESTRICTED_REGION extends Structure {
	/**
	 * \ufffd\ufffd\ufffd\u0633\ufffd\ufffd\ufffd-\u02a1,\ufffd\ufffd\ufffd\ufffdA\ufffd\ufffd\ufffd\ufffdC...<br>
	 * C type : char[(5)]
	 */
	public byte[] aucLocalPlate = new byte[5];
	public PU_ITS_RESTRICTED_REGION() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("aucLocalPlate");
	}
	/**
	 * @param aucLocalPlate \ufffd\ufffd\ufffd\u0633\ufffd\ufffd\ufffd-\u02a1,\ufffd\ufffd\ufffd\ufffdA\ufffd\ufffd\ufffd\ufffdC...<br>
	 * C type : char[(5)]
	 */
	public PU_ITS_RESTRICTED_REGION(byte aucLocalPlate[]) {
		super();
		if ((aucLocalPlate.length != this.aucLocalPlate.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.aucLocalPlate = aucLocalPlate;
	}
	public PU_ITS_RESTRICTED_REGION(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_ITS_RESTRICTED_REGION implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_ITS_RESTRICTED_REGION implements Structure.ByValue {
		
	};
}
