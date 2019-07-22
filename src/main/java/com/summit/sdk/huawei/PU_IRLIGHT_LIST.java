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
public class PU_IRLIGHT_LIST extends Structure {
	public NativeLong ulChannelId;
	/** C type : PU_IRLIGHT_MODE_E */
	public int enIRLightMode;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0763\ufffd\ufffd\u05b6\ufffd\u0123\u02bd\u02b1\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_IRLIGHT_PARA_S[4]
	 */
	public PU_IRLIGHT_PARA[] stIRLightMPara = new PU_IRLIGHT_PARA[4];
	/**
	 * szReserved[0] \ufffd\ufffd\ufffd\u073a\ufffd\ufffd\u2fea\ufffd\u0623\ufffd0:\ufffd\ufffd 1:\ufffd\u0631\ufffd<br>
	 * C type : CHAR[32]
	 */
	public byte[] szReserved = new byte[32];
	public PU_IRLIGHT_LIST() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "enIRLightMode", "stIRLightMPara", "szReserved");
	}
	/**
	 * @param enIRLightMode C type : PU_IRLIGHT_MODE_E<br>
	 * @param stIRLightMPara \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0763\ufffd\ufffd\u05b6\ufffd\u0123\u02bd\u02b1\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_IRLIGHT_PARA_S[4]<br>
	 * @param szReserved szReserved[0] \ufffd\ufffd\ufffd\u073a\ufffd\ufffd\u2fea\ufffd\u0623\ufffd0:\ufffd\ufffd 1:\ufffd\u0631\ufffd<br>
	 * C type : CHAR[32]
	 */
	public PU_IRLIGHT_LIST(NativeLong ulChannelId, int enIRLightMode, PU_IRLIGHT_PARA stIRLightMPara[], byte szReserved[]) {
		super();
		this.ulChannelId = ulChannelId;
		this.enIRLightMode = enIRLightMode;
		if ((stIRLightMPara.length != this.stIRLightMPara.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.stIRLightMPara = stIRLightMPara;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_IRLIGHT_LIST(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_IRLIGHT_LIST implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_IRLIGHT_LIST implements Structure.ByValue {
		
	};
}
