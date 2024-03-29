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
public class PU_DEC_INFO extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChannelId;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03e2<br>
	 * C type : PU_DECLINK_INFO_S
	 */
	public PU_DECLINK_INFO stDecLinkInfo;
	public PU_DEC_INFO() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "stDecLinkInfo");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffdID<br>
	 * @param stDecLinkInfo \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03e2<br>
	 * C type : PU_DECLINK_INFO_S
	 */
	public PU_DEC_INFO(NativeLong ulChannelId, PU_DECLINK_INFO stDecLinkInfo) {
		super();
		this.ulChannelId = ulChannelId;
		this.stDecLinkInfo = stDecLinkInfo;
	}
	public PU_DEC_INFO(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_DEC_INFO implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_DEC_INFO implements Structure.ByValue {
		
	};
}
