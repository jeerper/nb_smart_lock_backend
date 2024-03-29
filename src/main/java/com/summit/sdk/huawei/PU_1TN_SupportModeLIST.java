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
public class PU_1TN_SupportModeLIST extends Structure {
	/** \u0368\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulChannelId;
	/** \ufffd\ufffd\ufffd\ufffd */
	public NativeLong uSupportModeNum;
	/**
	 * \ufffd\u8c78\u05a7\ufffd\u05b5\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0431\ufffd<br>
	 * C type : PU_1TN_SUPPORT_MODE_E[TN_ITGT_SUPPORT_MODE_MAX]
	 */
	public int[] enSpptModelist = new int[(int)com.summit.sdk.huawei.HWPuSDKLibrary.PU_1TN_SUPPORT_MODE.TN_ITGT_SUPPORT_MODE_MAX];
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_1TN_SupportModeLIST() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "uSupportModeNum", "enSpptModelist", "szReserved");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffd\ufffd\ufffd<br>
	 * @param uSupportModeNum \ufffd\ufffd\ufffd\ufffd<br>
	 * @param enSpptModelist \ufffd\u8c78\u05a7\ufffd\u05b5\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0431\ufffd<br>
	 * C type : PU_1TN_SUPPORT_MODE_E[TN_ITGT_SUPPORT_MODE_MAX]<br>
	 * @param szReserved C type : CHAR[32]
	 */
	public PU_1TN_SupportModeLIST(NativeLong ulChannelId, NativeLong uSupportModeNum, int enSpptModelist[], byte szReserved[]) {
		super();
		this.ulChannelId = ulChannelId;
		this.uSupportModeNum = uSupportModeNum;
		if ((enSpptModelist.length != this.enSpptModelist.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.enSpptModelist = enSpptModelist;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_1TN_SupportModeLIST(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_1TN_SupportModeLIST implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_1TN_SupportModeLIST implements Structure.ByValue {
		
	};
}
