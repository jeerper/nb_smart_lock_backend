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
public class PU_SNMP_TRAP_INFO extends Structure {
	/**
	 * \ufffd\ufffd\ufffd\u0577\ufffdIP<br>
	 * C type : CHAR[16]
	 */
	public byte[] szTrapPeerIp = new byte[16];
	/** \ufffd\ufffd\ufffd\u0577\ufffd\ufffd\u02ff\ufffd */
	public short usTrapPeerPort;
	/**
	 * \ufffd\u6c7e<br>
	 * C type : PU_SNMP_VERSION_TYPE_E
	 */
	public int enSnmpVer;
	/**
	 * \ufffd\ufffd\u0228\ufffd\ufffd\u03e2<br>
	 * C type : PU_SNMP_AUTH_INFO_U
	 */
	public PU_SNMP_AUTH_INFO unSnmmpAuthInfo;
	/**
	 * szReserved[0],\u04e6\ufffd\u00f5\ufffd\ufffd\ufffd,0:\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd1,1:\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd2,\ufffd\u0534\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[32]
	 */
	public byte[] szReserved = new byte[32];
	public PU_SNMP_TRAP_INFO() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("szTrapPeerIp", "usTrapPeerPort", "enSnmpVer", "unSnmmpAuthInfo", "szReserved");
	}
	/**
	 * @param szTrapPeerIp \ufffd\ufffd\ufffd\u0577\ufffdIP<br>
	 * C type : CHAR[16]<br>
	 * @param usTrapPeerPort \ufffd\ufffd\ufffd\u0577\ufffd\ufffd\u02ff\ufffd<br>
	 * @param enSnmpVer \ufffd\u6c7e<br>
	 * C type : PU_SNMP_VERSION_TYPE_E<br>
	 * @param unSnmmpAuthInfo \ufffd\ufffd\u0228\ufffd\ufffd\u03e2<br>
	 * C type : PU_SNMP_AUTH_INFO_U<br>
	 * @param szReserved szReserved[0],\u04e6\ufffd\u00f5\ufffd\ufffd\ufffd,0:\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd1,1:\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd2,\ufffd\u0534\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[32]
	 */
	public PU_SNMP_TRAP_INFO(byte szTrapPeerIp[], short usTrapPeerPort, int enSnmpVer, PU_SNMP_AUTH_INFO unSnmmpAuthInfo, byte szReserved[]) {
		super();
		if ((szTrapPeerIp.length != this.szTrapPeerIp.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szTrapPeerIp = szTrapPeerIp;
		this.usTrapPeerPort = usTrapPeerPort;
		this.enSnmpVer = enSnmpVer;
		this.unSnmmpAuthInfo = unSnmmpAuthInfo;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_SNMP_TRAP_INFO(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_SNMP_TRAP_INFO implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_SNMP_TRAP_INFO implements Structure.ByValue {
		
	};
}
