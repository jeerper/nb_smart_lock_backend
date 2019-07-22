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
public class PU_ITS_SIGNAL_LIGHT_AREA_PARAM extends Structure {
	/**
	 * \ufffd\u017a\u0175\u01b5\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_LIGHT_DIR_S
	 */
	public PU_ITS_LIGHT_DIR stLightDir;
	/**
	 * \ufffd\u017a\u0175\ufffd\ufffd\ufffd\u026b<br>
	 * C type : PU_ITS_LIGHT_COLOR_S
	 */
	public PU_ITS_LIGHT_COLOR stLightColor;
	/** \ufffd\u01b8\ufffd\ufffd\ufffd */
	public int iLightNum;
	/**
	 * \ufffd\u017a\u0175\u01b0\ufffd\u05f0\ufffd\ufffd\u02bd<br>
	 * C type : PU_ITS_LIGHT_ARRANGE_TYPE_E
	 */
	public int enLightArrangeType;
	/** \ufffd\u01b5\u01b3\ufffd\ufffd\ufffd\u02b1\ufffd\ufffd */
	public int iYellowTime;
	/**
	 * \ufffd\u017a\u0175\ufffd\u03bb\ufffd\u00ff\ufffd<br>
	 * C type : PU_MD_AREA_S
	 */
	public PU_MD_AREA stLightArea;
	/**
	 * szReserve[0]: \ufffd\ufffd\ufffd\u01b5\ufffd\u02b9\ufffd\u0731\ufffd\u05be\ufffd\ufffdszReserve[1]: \u0461\ufffd\ufffd\ufffd\u02b9\ufffd\u0731\ufffd\u05be<br>
	 * C type : CHAR[32]
	 */
	public byte[] szReserve = new byte[32];
	public PU_ITS_SIGNAL_LIGHT_AREA_PARAM() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("stLightDir", "stLightColor", "iLightNum", "enLightArrangeType", "iYellowTime", "stLightArea", "szReserve");
	}
	/**
	 * @param stLightDir \ufffd\u017a\u0175\u01b5\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_LIGHT_DIR_S<br>
	 * @param stLightColor \ufffd\u017a\u0175\ufffd\ufffd\ufffd\u026b<br>
	 * C type : PU_ITS_LIGHT_COLOR_S<br>
	 * @param iLightNum \ufffd\u01b8\ufffd\ufffd\ufffd<br>
	 * @param enLightArrangeType \ufffd\u017a\u0175\u01b0\ufffd\u05f0\ufffd\ufffd\u02bd<br>
	 * C type : PU_ITS_LIGHT_ARRANGE_TYPE_E<br>
	 * @param iYellowTime \ufffd\u01b5\u01b3\ufffd\ufffd\ufffd\u02b1\ufffd\ufffd<br>
	 * @param stLightArea \ufffd\u017a\u0175\ufffd\u03bb\ufffd\u00ff\ufffd<br>
	 * C type : PU_MD_AREA_S<br>
	 * @param szReserve szReserve[0]: \ufffd\ufffd\ufffd\u01b5\ufffd\u02b9\ufffd\u0731\ufffd\u05be\ufffd\ufffdszReserve[1]: \u0461\ufffd\ufffd\ufffd\u02b9\ufffd\u0731\ufffd\u05be<br>
	 * C type : CHAR[32]
	 */
	public PU_ITS_SIGNAL_LIGHT_AREA_PARAM(PU_ITS_LIGHT_DIR stLightDir, PU_ITS_LIGHT_COLOR stLightColor, int iLightNum, int enLightArrangeType, int iYellowTime, PU_MD_AREA stLightArea, byte szReserve[]) {
		super();
		this.stLightDir = stLightDir;
		this.stLightColor = stLightColor;
		this.iLightNum = iLightNum;
		this.enLightArrangeType = enLightArrangeType;
		this.iYellowTime = iYellowTime;
		this.stLightArea = stLightArea;
		if ((szReserve.length != this.szReserve.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserve = szReserve;
	}
	public PU_ITS_SIGNAL_LIGHT_AREA_PARAM(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_ITS_SIGNAL_LIGHT_AREA_PARAM implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_ITS_SIGNAL_LIGHT_AREA_PARAM implements Structure.ByValue {
		
	};
}
