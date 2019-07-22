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
public class PU_AEMETERING_MODE extends Structure {
	/**
	 * \ufffd\ufffd\ufffd\ufffd(\ufffd\u0631\u0561\ufffd\ufffd\ufffd)<br>
	 * C type : PU_COMMONMODE_SWITCH_E
	 */
	public int enAEMeterSwitch;
	/**
	 * \ufffd\ufffd\ufffd\u0123\u02bd(\u05a7\ufffd\ufffd\ufffd\ufffd\ufffd\u0132\ufffd\u2862\u01bd\ufffd\ufffd\ufffd\u2862\ufffd\u00fb\ufffd\ufffd\u0536\ufffd\ufffd\ufffd)<br>
	 * C type : PU_VIDEO_AEMETERING_MODE_E
	 */
	public int enAEMetering;
	/**
	 * \ufffd\u0639\ufffd\ufffd\ufffd\ufffd\ufffd\u0228\ufffd\ufffd,Range:[0, 0xF]<br>
	 * C type : UCHAR[15][17]
	 */
	public byte[] aucAreaWeight = new byte[((15) * (17))];
	/** C type : CHAR[32] */
	public byte[] szReserve = new byte[32];
	public PU_AEMETERING_MODE() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("enAEMeterSwitch", "enAEMetering", "aucAreaWeight", "szReserve");
	}
	/**
	 * @param enAEMeterSwitch \ufffd\ufffd\ufffd\ufffd(\ufffd\u0631\u0561\ufffd\ufffd\ufffd)<br>
	 * C type : PU_COMMONMODE_SWITCH_E<br>
	 * @param enAEMetering \ufffd\ufffd\ufffd\u0123\u02bd(\u05a7\ufffd\ufffd\ufffd\ufffd\ufffd\u0132\ufffd\u2862\u01bd\ufffd\ufffd\ufffd\u2862\ufffd\u00fb\ufffd\ufffd\u0536\ufffd\ufffd\ufffd)<br>
	 * C type : PU_VIDEO_AEMETERING_MODE_E<br>
	 * @param aucAreaWeight \ufffd\u0639\ufffd\ufffd\ufffd\ufffd\ufffd\u0228\ufffd\ufffd,Range:[0, 0xF]<br>
	 * C type : UCHAR[15][17]<br>
	 * @param szReserve C type : CHAR[32]
	 */
	public PU_AEMETERING_MODE(int enAEMeterSwitch, int enAEMetering, byte aucAreaWeight[], byte szReserve[]) {
		super();
		this.enAEMeterSwitch = enAEMeterSwitch;
		this.enAEMetering = enAEMetering;
		if ((aucAreaWeight.length != this.aucAreaWeight.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.aucAreaWeight = aucAreaWeight;
		if ((szReserve.length != this.szReserve.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserve = szReserve;
	}
	public PU_AEMETERING_MODE(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_AEMETERING_MODE implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_AEMETERING_MODE implements Structure.ByValue {
		
	};
}
