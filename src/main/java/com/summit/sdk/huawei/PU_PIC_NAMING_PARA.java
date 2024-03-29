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
public class PU_PIC_NAMING_PARA extends Structure {
	/**
	 * \ufffd\u05b8\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_SEPARATOR_TYPE_E
	 */
	public int enSeparatorType;
	/** C type : PU_PIC_NAMING_ELEM_S[(20)] */
	public PU_PIC_NAMING_ELEM[] astPicNamingElem = new PU_PIC_NAMING_ELEM[20];
	/**
	 * \u0524\ufffd\ufffd\ufffd\u05b6\ufffd<br>
	 * C type : CHAR[32]
	 */
	public byte[] szReserved = new byte[32];
	public PU_PIC_NAMING_PARA() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("enSeparatorType", "astPicNamingElem", "szReserved");
	}
	/**
	 * @param enSeparatorType \ufffd\u05b8\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_SEPARATOR_TYPE_E<br>
	 * @param astPicNamingElem C type : PU_PIC_NAMING_ELEM_S[(20)]<br>
	 * @param szReserved \u0524\ufffd\ufffd\ufffd\u05b6\ufffd<br>
	 * C type : CHAR[32]
	 */
	public PU_PIC_NAMING_PARA(int enSeparatorType, PU_PIC_NAMING_ELEM astPicNamingElem[], byte szReserved[]) {
		super();
		this.enSeparatorType = enSeparatorType;
		if ((astPicNamingElem.length != this.astPicNamingElem.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.astPicNamingElem = astPicNamingElem;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_PIC_NAMING_PARA(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_PIC_NAMING_PARA implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_PIC_NAMING_PARA implements Structure.ByValue {
		
	};
}
