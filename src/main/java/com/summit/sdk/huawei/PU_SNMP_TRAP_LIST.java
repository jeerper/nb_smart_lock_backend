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
public class PU_SNMP_TRAP_LIST extends Structure {
	/** Trap\ufffd\ufffd\ufffd\u0576\ufffd\ufffd\ufffd\u013f */
	public NativeLong ulTrapNum;
	/**
	 * Trap\ufffd\ufffd\ufffd\u0576\ufffd\ufffd\ufffd\u03e2<br>
	 * C type : PU_SNMP_TRAP_INFO_S[3]
	 */
	public PU_SNMP_TRAP_INFO[] szTrapInfo = new PU_SNMP_TRAP_INFO[3];
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_SNMP_TRAP_LIST() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulTrapNum", "szTrapInfo", "szReserved");
	}
	/**
	 * @param ulTrapNum Trap\ufffd\ufffd\ufffd\u0576\ufffd\ufffd\ufffd\u013f<br>
	 * @param szTrapInfo Trap\ufffd\ufffd\ufffd\u0576\ufffd\ufffd\ufffd\u03e2<br>
	 * C type : PU_SNMP_TRAP_INFO_S[3]<br>
	 * @param szReserved C type : CHAR[32]
	 */
	public PU_SNMP_TRAP_LIST(NativeLong ulTrapNum, PU_SNMP_TRAP_INFO szTrapInfo[], byte szReserved[]) {
		super();
		this.ulTrapNum = ulTrapNum;
		if ((szTrapInfo.length != this.szTrapInfo.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szTrapInfo = szTrapInfo;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_SNMP_TRAP_LIST(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_SNMP_TRAP_LIST implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_SNMP_TRAP_LIST implements Structure.ByValue {
		
	};
}
