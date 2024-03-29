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
public class PU_VA_LINK_AT extends Structure {
	/** \u0368\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulChannelId;
	/** \ufffd\u3de8\u02b9\ufffd\ufffd */
	public boolean bEnable;
	/**
	 * \ufffd\ufffd\u04aa\ufffd\ufffd\ufffd\ufffd\ufffd\u0536\ufffd\ufffd\ufffd\ufffd\u0675\u0138\u6faf<br>
	 * C type : PU_ALARM_TYPE_E
	 */
	public int enAlarmType;
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_VA_LINK_AT() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "bEnable", "enAlarmType", "szReserved");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffd\ufffd\ufffd<br>
	 * @param bEnable \ufffd\u3de8\u02b9\ufffd\ufffd<br>
	 * @param enAlarmType \ufffd\ufffd\u04aa\ufffd\ufffd\ufffd\ufffd\ufffd\u0536\ufffd\ufffd\ufffd\ufffd\u0675\u0138\u6faf<br>
	 * C type : PU_ALARM_TYPE_E<br>
	 * @param szReserved C type : CHAR[32]
	 */
	public PU_VA_LINK_AT(NativeLong ulChannelId, boolean bEnable, int enAlarmType, byte szReserved[]) {
		super();
		this.ulChannelId = ulChannelId;
		this.bEnable = bEnable;
		this.enAlarmType = enAlarmType;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_VA_LINK_AT(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_VA_LINK_AT implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_VA_LINK_AT implements Structure.ByValue {
		
	};
}
