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
public class PU_TD_ALG_PARAMSV2 extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChannelId;
	/** \ufffd\u3de8\u02b9\ufffd\ufffd */
	public boolean bEnable;
	/** C type : CHAR[32] */
	public byte[] szReserve = new byte[32];
	public PU_TD_ALG_PARAMSV2() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "bEnable", "szReserve");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffdID<br>
	 * @param bEnable \ufffd\u3de8\u02b9\ufffd\ufffd<br>
	 * @param szReserve C type : CHAR[32]
	 */
	public PU_TD_ALG_PARAMSV2(NativeLong ulChannelId, boolean bEnable, byte szReserve[]) {
		super();
		this.ulChannelId = ulChannelId;
		this.bEnable = bEnable;
		if ((szReserve.length != this.szReserve.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserve = szReserve;
	}
	public PU_TD_ALG_PARAMSV2(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_TD_ALG_PARAMSV2 implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_TD_ALG_PARAMSV2 implements Structure.ByValue {
		
	};
}
