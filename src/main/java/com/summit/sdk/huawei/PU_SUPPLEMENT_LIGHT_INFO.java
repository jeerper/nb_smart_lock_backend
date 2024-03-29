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
public class PU_SUPPLEMENT_LIGHT_INFO extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChannelId;
	public NativeLong ulActiveNum;
	/** C type : PU_SUPPLEMENT_LIGHT_LAMP_INFO_S[(8)] */
	public PU_SUPPLEMENT_LIGHT_LAMP_INFO[] stLampInfo = new PU_SUPPLEMENT_LIGHT_LAMP_INFO[8];
	/**
	 * szReserved[0]\ufffd\ufffd\u02be\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd PU_SUPPLEMENTLAMP_TYPE_E<br>
	 * C type : CHAR[32]
	 */
	public byte[] szReserved = new byte[32];
	public PU_SUPPLEMENT_LIGHT_INFO() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "ulActiveNum", "stLampInfo", "szReserved");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffdID<br>
	 * @param stLampInfo C type : PU_SUPPLEMENT_LIGHT_LAMP_INFO_S[(8)]<br>
	 * @param szReserved szReserved[0]\ufffd\ufffd\u02be\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd PU_SUPPLEMENTLAMP_TYPE_E<br>
	 * C type : CHAR[32]
	 */
	public PU_SUPPLEMENT_LIGHT_INFO(NativeLong ulChannelId, NativeLong ulActiveNum, PU_SUPPLEMENT_LIGHT_LAMP_INFO stLampInfo[], byte szReserved[]) {
		super();
		this.ulChannelId = ulChannelId;
		this.ulActiveNum = ulActiveNum;
		if ((stLampInfo.length != this.stLampInfo.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.stLampInfo = stLampInfo;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_SUPPLEMENT_LIGHT_INFO(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_SUPPLEMENT_LIGHT_INFO implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_SUPPLEMENT_LIGHT_INFO implements Structure.ByValue {
		
	};
}
