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
public class tagITSRegulationCfgAttchPressLine extends Structure {
	/** \u0479\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0223\ufffd0~100ms\ufffd\ufffd */
	public int nSensitivity;
	/** C type : CHAR[32] */
	public byte[] szReserve = new byte[32];
	public tagITSRegulationCfgAttchPressLine() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("nSensitivity", "szReserve");
	}
	/**
	 * @param nSensitivity \u0479\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0223\ufffd0~100ms\ufffd\ufffd<br>
	 * @param szReserve C type : CHAR[32]
	 */
	public tagITSRegulationCfgAttchPressLine(int nSensitivity, byte szReserve[]) {
		super();
		this.nSensitivity = nSensitivity;
		if ((szReserve.length != this.szReserve.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserve = szReserve;
	}
	public tagITSRegulationCfgAttchPressLine(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends tagITSRegulationCfgAttchPressLine implements Structure.ByReference {
		
	};
	public static class ByValue extends tagITSRegulationCfgAttchPressLine implements Structure.ByValue {
		
	};
}
