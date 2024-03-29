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
public class GUARD_POSITION_PARA_EX extends Structure {
	public NativeLong ulPtzId;
	public boolean bEnable;
	public NativeLong ulWaitTime;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_GUARD_POINT_TYPE_E
	 */
	public int enGuardType;
	/**
	 * \ufffd\ufffd\ufffd\u0632\ufffd\ufffd\ufffd<br>
	 * C type : PU_GUARD_POINT_PARA_U
	 */
	public PU_GUARD_POINT_PARA unGuardPara;
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public GUARD_POSITION_PARA_EX() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulPtzId", "bEnable", "ulWaitTime", "enGuardType", "unGuardPara", "szReserved");
	}
	/**
	 * @param enGuardType \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_GUARD_POINT_TYPE_E<br>
	 * @param unGuardPara \ufffd\ufffd\ufffd\u0632\ufffd\ufffd\ufffd<br>
	 * C type : PU_GUARD_POINT_PARA_U<br>
	 * @param szReserved C type : CHAR[32]
	 */
	public GUARD_POSITION_PARA_EX(NativeLong ulPtzId, boolean bEnable, NativeLong ulWaitTime, int enGuardType, PU_GUARD_POINT_PARA unGuardPara, byte szReserved[]) {
		super();
		this.ulPtzId = ulPtzId;
		this.bEnable = bEnable;
		this.ulWaitTime = ulWaitTime;
		this.enGuardType = enGuardType;
		this.unGuardPara = unGuardPara;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public GUARD_POSITION_PARA_EX(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends GUARD_POSITION_PARA_EX implements Structure.ByReference {
		
	};
	public static class ByValue extends GUARD_POSITION_PARA_EX implements Structure.ByValue {
		
	};
}
