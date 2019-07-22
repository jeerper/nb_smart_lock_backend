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
public class PU_ITS_EPOLICE_SIGNAL_LIGHT_PARAM extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChannelId;
	/**
	 * \ufffd\ufffd\ufffd\u0335\u01bc\ufffd\ufffd\ufffd\ufffd\ubdfd\u02bd<br>
	 * C type : PU_ITS_LIGHT_DT_TYPE_S
	 */
	public int enLightDtType;
	/** \ufffd\u017a\u0175\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd */
	public int iSignalLightAreaNum;
	/**
	 * \ufffd\u017a\u0175\u01b2\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_SIGNAL_LIGHT_AREA_PARAM_S[4]
	 */
	public PU_ITS_SIGNAL_LIGHT_AREA_PARAM[] astSignalLightList = new PU_ITS_SIGNAL_LIGHT_AREA_PARAM[4];
	/** C type : CHAR[32] */
	public byte[] szReserve = new byte[32];
	public PU_ITS_EPOLICE_SIGNAL_LIGHT_PARAM() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "enLightDtType", "iSignalLightAreaNum", "astSignalLightList", "szReserve");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffdID<br>
	 * @param enLightDtType \ufffd\ufffd\ufffd\u0335\u01bc\ufffd\ufffd\ufffd\ufffd\ubdfd\u02bd<br>
	 * C type : PU_ITS_LIGHT_DT_TYPE_S<br>
	 * @param iSignalLightAreaNum \ufffd\u017a\u0175\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * @param astSignalLightList \ufffd\u017a\u0175\u01b2\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_SIGNAL_LIGHT_AREA_PARAM_S[4]<br>
	 * @param szReserve C type : CHAR[32]
	 */
	public PU_ITS_EPOLICE_SIGNAL_LIGHT_PARAM(NativeLong ulChannelId, int enLightDtType, int iSignalLightAreaNum, PU_ITS_SIGNAL_LIGHT_AREA_PARAM astSignalLightList[], byte szReserve[]) {
		super();
		this.ulChannelId = ulChannelId;
		this.enLightDtType = enLightDtType;
		this.iSignalLightAreaNum = iSignalLightAreaNum;
		if ((astSignalLightList.length != this.astSignalLightList.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.astSignalLightList = astSignalLightList;
		if ((szReserve.length != this.szReserve.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserve = szReserve;
	}
	public PU_ITS_EPOLICE_SIGNAL_LIGHT_PARAM(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_ITS_EPOLICE_SIGNAL_LIGHT_PARAM implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_ITS_EPOLICE_SIGNAL_LIGHT_PARAM implements Structure.ByValue {
		
	};
}
