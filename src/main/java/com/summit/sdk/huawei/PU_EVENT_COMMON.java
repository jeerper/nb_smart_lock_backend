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
public class PU_EVENT_COMMON extends Structure {
	/**
	 * \ufffd\u00bc\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_EVENT_TYPE_E
	 */
	public int enEventType;
	/** \u03a8\u04bb\ufffd\ufffd\u02b6\ufffd\ufffdID\ufffd\ufffd */
	public NativeLong ulIdentifyID;
	public NativeLong ulChannelId;
	public PU_EVENT_COMMON() {
		super();
	}
	@Override
	protected List<String > getFieldOrder() {
		return Arrays.asList("enEventType", "ulIdentifyID", "ulChannelId");
	}
	/**
	 * @param enEventType \ufffd\u00bc\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_EVENT_TYPE_E<br>
	 * @param ulIdentifyID \u03a8\u04bb\ufffd\ufffd\u02b6\ufffd\ufffdID\ufffd\ufffd
	 */
	public PU_EVENT_COMMON(int enEventType, NativeLong ulIdentifyID, NativeLong ulChannelId) {
		super();
		this.enEventType = enEventType;
		this.ulIdentifyID = ulIdentifyID;
		this.ulChannelId = ulChannelId;
	}
	public PU_EVENT_COMMON(Pointer peer) {
		super(peer);
		read();
	}
	public static class ByReference extends PU_EVENT_COMMON implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_EVENT_COMMON implements Structure.ByValue {
		
	};
}
