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
public class FACE_FIND_CONDITION extends Structure {
	/**
	 * \ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[(64)]
	 */
	public byte[] szName = new byte[64];
	/**
	 * \ufffd\u0531\ufffd<br>
	 * C type : PU_GENDER_E
	 */
	public int enGender;
	/**
	 * \u02a1\ufffd\ufffd<br>
	 * C type : CHAR[(32)]
	 */
	public byte[] szProvince = new byte[32];
	/**
	 * \ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[(48)]
	 */
	public byte[] szCity = new byte[48];
	/**
	 * \u05a4\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_CARDTYPE_E
	 */
	public int enCardType;
	/**
	 * \u05a4\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[(32)]
	 */
	public byte[] szCardID = new byte[32];
	/**
	 * \ufffd\ufffd\ufffd\u057f\ufffd\u02bc<br>
	 * C type : CHAR[(32)]
	 */
	public byte[] szBirthdayStart = new byte[32];
	/**
	 * \ufffd\ufffd\ufffd\u057d\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[(32)]
	 */
	public byte[] szBirthdayEnd = new byte[32];
	/**
	 * \ufffd\ufffd\ufffd\ufffd\u05b5\u05f4\u032c<br>
	 * C type : PU_FEATURE_STATUS_TYPE_E
	 */
	public int enFeatureStatus;
	public byte[] szReserve = new byte[32-4];
	/** Conversion Error : sizeof(int) */
	public FACE_FIND_CONDITION() {
		this.setAlignType(ALIGN_NONE);
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("szName", "enGender", "szProvince", "szCity", "enCardType", "szCardID", "szBirthdayStart", "szBirthdayEnd", "enFeatureStatus","szReserve");
	}
	/**
	 * @param szName \ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[(64)]<br>
	 * @param enGender \ufffd\u0531\ufffd<br>
	 * C type : PU_GENDER_E<br>
	 * @param szProvince \u02a1\ufffd\ufffd<br>
	 * C type : CHAR[(32)]<br>
	 * @param szCity \ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[(48)]<br>
	 * @param enCardType \u05a4\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_CARDTYPE_E<br>
	 * @param szCardID \u05a4\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[(32)]<br>
	 * @param szBirthdayStart \ufffd\ufffd\ufffd\u057f\ufffd\u02bc<br>
	 * C type : CHAR[(32)]<br>
	 * @param szBirthdayEnd \ufffd\ufffd\ufffd\u057d\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[(32)]<br>
	 * @param enFeatureStatus \ufffd\ufffd\ufffd\ufffd\u05b5\u05f4\u032c<br>
	 * C type : PU_FEATURE_STATUS_TYPE_E
	 */
	public FACE_FIND_CONDITION(byte szName[], int enGender, byte szProvince[], byte szCity[], int enCardType, byte szCardID[], byte szBirthdayStart[], byte szBirthdayEnd[], int enFeatureStatus) {
		super();
		if ((szName.length != this.szName.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szName = szName;
		this.enGender = enGender;
		if ((szProvince.length != this.szProvince.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szProvince = szProvince;
		if ((szCity.length != this.szCity.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szCity = szCity;
		this.enCardType = enCardType;
		if ((szCardID.length != this.szCardID.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szCardID = szCardID;
		if ((szBirthdayStart.length != this.szBirthdayStart.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szBirthdayStart = szBirthdayStart;
		if ((szBirthdayEnd.length != this.szBirthdayEnd.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szBirthdayEnd = szBirthdayEnd;
		this.enFeatureStatus = enFeatureStatus;
	}

	public FACE_FIND_CONDITION(Pointer peer) {
		super(peer);
	}
	public static abstract class ByReference extends FACE_FIND_CONDITION implements Structure.ByReference {
		
	};
	public static abstract class ByValue extends FACE_FIND_CONDITION implements Structure.ByValue {
		
	};
}
