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
public class PU_STREAM_ATTRIBUTE extends Structure {
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_STREAM_TYPE_E
	 */
	public int enStreamId;
	/**
	 * \ufffd\ufffd\u01b5\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_VIDEO_ATTRIBUTE_S
	 */
	public PU_VIDEO_ATTRIBUTE stVideoAttribute;
	public PU_STREAM_ATTRIBUTE() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("enStreamId", "stVideoAttribute");
	}
	/**
	 * @param enStreamId \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_STREAM_TYPE_E<br>
	 * @param stVideoAttribute \ufffd\ufffd\u01b5\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_VIDEO_ATTRIBUTE_S
	 */
	public PU_STREAM_ATTRIBUTE(int enStreamId, PU_VIDEO_ATTRIBUTE stVideoAttribute) {
		super();
		this.enStreamId = enStreamId;
		this.stVideoAttribute = stVideoAttribute;
	}
	public PU_STREAM_ATTRIBUTE(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_STREAM_ATTRIBUTE implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_STREAM_ATTRIBUTE implements Structure.ByValue {
		
	};
}
