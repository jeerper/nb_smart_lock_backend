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
public class PU_CHANNEL_STREAM_PROFILESV2 extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChannelID;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_STREAM_MANIPULATE_TYPE_E
	 */
	public int enStreamOptionType;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\u05f2\u0372\ufffd\ufffd\ufffd<br>
	 * C type : PU_STREAM_PROFILES_PARAV2_S
	 */
	public PU_STREAM_PROFILES_PARAV2 stStreamProfilesPara;
	/**
	 * \u0524\ufffd\ufffd<br>
	 * C type : CHAR[32]
	 */
	public byte[] szReserve = new byte[32];
	public PU_CHANNEL_STREAM_PROFILESV2() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelID", "enStreamOptionType", "stStreamProfilesPara", "szReserve");
	}
	/**
	 * @param ulChannelID \u0368\ufffd\ufffdID<br>
	 * @param enStreamOptionType \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_STREAM_MANIPULATE_TYPE_E<br>
	 * @param stStreamProfilesPara \ufffd\ufffd\ufffd\ufffd\ufffd\u05f2\u0372\ufffd\ufffd\ufffd<br>
	 * C type : PU_STREAM_PROFILES_PARAV2_S<br>
	 * @param szReserve \u0524\ufffd\ufffd<br>
	 * C type : CHAR[32]
	 */
	public PU_CHANNEL_STREAM_PROFILESV2(NativeLong ulChannelID, int enStreamOptionType, PU_STREAM_PROFILES_PARAV2 stStreamProfilesPara, byte szReserve[]) {
		super();
		this.ulChannelID = ulChannelID;
		this.enStreamOptionType = enStreamOptionType;
		this.stStreamProfilesPara = stStreamProfilesPara;
		if ((szReserve.length != this.szReserve.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserve = szReserve;
	}
	public PU_CHANNEL_STREAM_PROFILESV2(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_CHANNEL_STREAM_PROFILESV2 implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_CHANNEL_STREAM_PROFILESV2 implements Structure.ByValue {
		
	};
}
