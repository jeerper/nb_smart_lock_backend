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
public class PU_SNAPSHOT_INQUIRE extends Structure {
	public NativeLong ulChnID;
	/** \ufffd\ufffd\u01f0\ufffd\ufffd\u02bc\ufffd\ufffd\u00bc\ufffd\ufffd\ufffd\ufffd(>=1) */
	public NativeLong ulBeginIndex;
	/** \ufffd\ufffd\u02bc\u02b1\ufffd\ufffd(time_t/localTime=GMT+\u02b1\ufffd\ufffd) */
	public NativeLong ulBeginTime;
	/** \ufffd\ufffd\ufffd\ufffd\u02b1\ufffd\ufffd */
	public NativeLong ulEndTime;
	/**
	 * \u05e5\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_SNAPSHOT_TYPE_E
	 */
	public int enSnapShotType;
	/**
	 * szReserved[0]\ufffd\ufffd\u04b3\ufffd\ufffd\u046fflag<br>
	 * C type : CHAR[32]
	 */
	public byte[] szReserved = new byte[32];
	public PU_SNAPSHOT_INQUIRE() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChnID", "ulBeginIndex", "ulBeginTime", "ulEndTime", "enSnapShotType", "szReserved");
	}
	/**
	 * @param ulBeginIndex \ufffd\ufffd\u01f0\ufffd\ufffd\u02bc\ufffd\ufffd\u00bc\ufffd\ufffd\ufffd\ufffd(>=1)<br>
	 * @param ulBeginTime \ufffd\ufffd\u02bc\u02b1\ufffd\ufffd(time_t/localTime=GMT+\u02b1\ufffd\ufffd)<br>
	 * @param ulEndTime \ufffd\ufffd\ufffd\ufffd\u02b1\ufffd\ufffd<br>
	 * @param enSnapShotType \u05e5\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_SNAPSHOT_TYPE_E<br>
	 * @param szReserved szReserved[0]\ufffd\ufffd\u04b3\ufffd\ufffd\u046fflag<br>
	 * C type : CHAR[32]
	 */
	public PU_SNAPSHOT_INQUIRE(NativeLong ulChnID, NativeLong ulBeginIndex, NativeLong ulBeginTime, NativeLong ulEndTime, int enSnapShotType, byte szReserved[]) {
		super();
		this.ulChnID = ulChnID;
		this.ulBeginIndex = ulBeginIndex;
		this.ulBeginTime = ulBeginTime;
		this.ulEndTime = ulEndTime;
		this.enSnapShotType = enSnapShotType;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_SNAPSHOT_INQUIRE(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_SNAPSHOT_INQUIRE implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_SNAPSHOT_INQUIRE implements Structure.ByValue {
		
	};
}
