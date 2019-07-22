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
public class PU_RL_LIGHTINFOS_PARA extends Structure {
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02bc\ufffd\ufffd\ufffd<br>
	 * C type : PU_RECTANGLE_S
	 */
	public PU_RECTANGLE stRLCropStart;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_RL_LIGHT_TYPE_E
	 */
	public int enType;
	/** \ufffd\ufffd\ufffd\ufffd\u02b9\ufffd\u073f\ufffd\ufffd\ufffd */
	public boolean bEnable;
	/** C type : CHAR[32] */
	public byte[] szReserve = new byte[32];
	public PU_RL_LIGHTINFOS_PARA() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("stRLCropStart", "enType", "bEnable", "szReserve");
	}
	/**
	 * @param stRLCropStart \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02bc\ufffd\ufffd\ufffd<br>
	 * C type : PU_RECTANGLE_S<br>
	 * @param enType \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_RL_LIGHT_TYPE_E<br>
	 * @param bEnable \ufffd\ufffd\ufffd\ufffd\u02b9\ufffd\u073f\ufffd\ufffd\ufffd<br>
	 * @param szReserve C type : CHAR[32]
	 */
	public PU_RL_LIGHTINFOS_PARA(PU_RECTANGLE stRLCropStart, int enType, boolean bEnable, byte szReserve[]) {
		super();
		this.stRLCropStart = stRLCropStart;
		this.enType = enType;
		this.bEnable = bEnable;
		if ((szReserve.length != this.szReserve.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserve = szReserve;
	}
	public PU_RL_LIGHTINFOS_PARA(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_RL_LIGHTINFOS_PARA implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_RL_LIGHTINFOS_PARA implements Structure.ByValue {
		
	};
}
