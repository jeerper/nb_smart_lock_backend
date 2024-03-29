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
public class PU_DIALING_PARA extends Structure {
	/**
	 * \ufffd\ufffd\u0228\ufffd\ufffd\ufffd\u0363\ufffd\u03aa\ufffd\ufffd\ufffd\ufffd\u0228\u02b1\ufffd\u00fb\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03aa\ufffd\ufffd<br>
	 * C type : DIAL_AUTH_TYPE_E
	 */
	public int enAuthType;
	/** C type : CHAR[(32 + 1)] */
	public byte[] szApn = new byte[32 + 1];
	/** C type : CHAR[(32 + 1)] */
	public byte[] szUserName = new byte[32 + 1];
	/** C type : CHAR[(32 + 1)] */
	public byte[] szPassWd = new byte[32 + 1];
	public PU_DIALING_PARA() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("enAuthType", "szApn", "szUserName", "szPassWd");
	}
	/**
	 * @param enAuthType \ufffd\ufffd\u0228\ufffd\ufffd\ufffd\u0363\ufffd\u03aa\ufffd\ufffd\ufffd\ufffd\u0228\u02b1\ufffd\u00fb\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03aa\ufffd\ufffd<br>
	 * C type : DIAL_AUTH_TYPE_E<br>
	 * @param szApn C type : CHAR[(32 + 1)]<br>
	 * @param szUserName C type : CHAR[(32 + 1)]<br>
	 * @param szPassWd C type : CHAR[(32 + 1)]
	 */
	public PU_DIALING_PARA(int enAuthType, byte szApn[], byte szUserName[], byte szPassWd[]) {
		super();
		this.enAuthType = enAuthType;
		if ((szApn.length != this.szApn.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szApn = szApn;
		if ((szUserName.length != this.szUserName.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szUserName = szUserName;
		if ((szPassWd.length != this.szPassWd.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szPassWd = szPassWd;
	}
	public PU_DIALING_PARA(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_DIALING_PARA implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_DIALING_PARA implements Structure.ByValue {
		
	};
}
