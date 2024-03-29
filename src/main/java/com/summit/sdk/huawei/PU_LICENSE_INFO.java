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
public class PU_LICENSE_INFO extends Structure {
	/**
	 * \ufffd\u3de8license\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_LICENSE_TYPE_E
	 */
	public int emLicenseType;
	/**
	 * Licnese\ufffd\u013c\ufffd\u00b7\ufffd\ufffd<br>
	 * C type : CHAR[128]
	 */
	public byte[] FileName = new byte[128];
	/** C type : CHAR[32] */
	public byte[] szReserve = new byte[32];
	public PU_LICENSE_INFO() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("emLicenseType", "FileName", "szReserve");
	}
	/**
	 * @param emLicenseType \ufffd\u3de8license\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_LICENSE_TYPE_E<br>
	 * @param FileName Licnese\ufffd\u013c\ufffd\u00b7\ufffd\ufffd<br>
	 * C type : CHAR[128]<br>
	 * @param szReserve C type : CHAR[32]
	 */
	public PU_LICENSE_INFO(int emLicenseType, byte FileName[], byte szReserve[]) {
		super();
		this.emLicenseType = emLicenseType;
		if ((FileName.length != this.FileName.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.FileName = FileName;
		if ((szReserve.length != this.szReserve.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserve = szReserve;
	}
	public PU_LICENSE_INFO(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_LICENSE_INFO implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_LICENSE_INFO implements Structure.ByValue {
		
	};
}
