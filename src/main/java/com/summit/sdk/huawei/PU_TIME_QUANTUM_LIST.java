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
public class PU_TIME_QUANTUM_LIST extends Structure {
	/** \ufffd\ufffd\u0427\u01f0\ufffd\ufffd\ufffd\ufffd\u02b1\ufffd\ufffd\u03a3\ufffd\u04bb\ufffd\u3dbc\u03aaMAX_TIMEQAUMTUM_NUM \u05b5 */
	public NativeLong ulTimeQuantumNum;
	/** C type : PU_TIME_QUANTUM_INFO_S[8] */
	public PU_TIME_QUANTUM_INFO[] stTimeQuantumInfo = new PU_TIME_QUANTUM_INFO[8];
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_TIME_QUANTUM_LIST() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulTimeQuantumNum", "stTimeQuantumInfo", "szReserved");
	}
	/**
	 * @param ulTimeQuantumNum \ufffd\ufffd\u0427\u01f0\ufffd\ufffd\ufffd\ufffd\u02b1\ufffd\ufffd\u03a3\ufffd\u04bb\ufffd\u3dbc\u03aaMAX_TIMEQAUMTUM_NUM \u05b5<br>
	 * @param stTimeQuantumInfo C type : PU_TIME_QUANTUM_INFO_S[8]<br>
	 * @param szReserved C type : CHAR[32]
	 */
	public PU_TIME_QUANTUM_LIST(NativeLong ulTimeQuantumNum, PU_TIME_QUANTUM_INFO stTimeQuantumInfo[], byte szReserved[]) {
		super();
		this.ulTimeQuantumNum = ulTimeQuantumNum;
		if ((stTimeQuantumInfo.length != this.stTimeQuantumInfo.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.stTimeQuantumInfo = stTimeQuantumInfo;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_TIME_QUANTUM_LIST(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_TIME_QUANTUM_LIST implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_TIME_QUANTUM_LIST implements Structure.ByValue {
		
	};
}
