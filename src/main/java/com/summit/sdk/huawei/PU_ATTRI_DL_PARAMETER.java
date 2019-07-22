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
public class PU_ATTRI_DL_PARAMETER extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChannelId;
	/** \ufffd\ufffd\ufffd\u0537\ufffd\ufffd\ufffd\u02b9\ufffd\ufffd */
	public boolean enEnableAttri;
	/**
	 * BOOL     enEnableBodyAttri;    // \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0537\ufffd\ufffd\ufffd\u02b9\ufffd\ufffd<br>
	 * C type : CHAR[32]
	 */
	public byte[] szReserved = new byte[32];
	public PU_ATTRI_DL_PARAMETER() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "enEnableAttri", "szReserved");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffdID<br>
	 * @param enEnableAttri \ufffd\ufffd\ufffd\u0537\ufffd\ufffd\ufffd\u02b9\ufffd\ufffd<br>
	 * @param szReserved BOOL     enEnableBodyAttri;    // \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0537\ufffd\ufffd\ufffd\u02b9\ufffd\ufffd<br>
	 * C type : CHAR[32]
	 */
	public PU_ATTRI_DL_PARAMETER(NativeLong ulChannelId, boolean enEnableAttri, byte szReserved[]) {
		super();
		this.ulChannelId = ulChannelId;
		this.enEnableAttri = enEnableAttri;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_ATTRI_DL_PARAMETER(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_ATTRI_DL_PARAMETER implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_ATTRI_DL_PARAMETER implements Structure.ByValue {
		
	};
}
