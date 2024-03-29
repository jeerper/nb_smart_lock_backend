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
public class PU_FACE_DETC_PARA extends Structure {
	/** \ufffd\u6faf\ufffd\ufffd\ufffd\u02b1\ufffd\ufffd */
	public NativeLong ulAlarmTime;
	/** \u02b9\ufffd\ufffd */
	public boolean bEnable;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulSensitivity;
	/** \ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03a766-486\ufffd\ufffd\ufffd\u0435\ufffd\u0536\ufffd\ufffd\u0123\u02bd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd70\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd180\ufffd\ufffd\ufffd\u0435\ufffd\ufffd\ufffd\u0123\u02bd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd131\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd273 */
	public NativeLong ulMinDectFace;
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03a766-486\ufffd\ufffd\ufffd\u0435\ufffd\u0536\ufffd\ufffd\u0123\u02bd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd70\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd180\ufffd\ufffd\ufffd\u0435\ufffd\ufffd\ufffd\u0123\u02bd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd131\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd273 */
	public NativeLong ulMaxDectFace;
	/** \ufffd\ufffd\ufffd\u0123\u02bd 0\ufffd\ufffd\u0536\ufffd\ufffd\u0123\u02bd  1\ufffd\ufffd\ufffd\ufffd\u0123\u02bd */
	public NativeLong ulDectMode;
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_FACE_DETC_PARA() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulAlarmTime", "bEnable", "ulSensitivity", "ulMinDectFace", "ulMaxDectFace", "ulDectMode", "szReserved");
	}
	/**
	 * @param ulAlarmTime \ufffd\u6faf\ufffd\ufffd\ufffd\u02b1\ufffd\ufffd<br>
	 * @param bEnable \u02b9\ufffd\ufffd<br>
	 * @param ulSensitivity \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * @param ulMinDectFace \ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03a766-486\ufffd\ufffd\ufffd\u0435\ufffd\u0536\ufffd\ufffd\u0123\u02bd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd70\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd180\ufffd\ufffd\ufffd\u0435\ufffd\ufffd\ufffd\u0123\u02bd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd131\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd273<br>
	 * @param ulMaxDectFace \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03a766-486\ufffd\ufffd\ufffd\u0435\ufffd\u0536\ufffd\ufffd\u0123\u02bd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd70\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd180\ufffd\ufffd\ufffd\u0435\ufffd\ufffd\ufffd\u0123\u02bd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0421\ufffd\ufffd\ufffd\ufffd131\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd273<br>
	 * @param ulDectMode \ufffd\ufffd\ufffd\u0123\u02bd 0\ufffd\ufffd\u0536\ufffd\ufffd\u0123\u02bd  1\ufffd\ufffd\ufffd\ufffd\u0123\u02bd<br>
	 * @param szReserved C type : CHAR[32]
	 */
	public PU_FACE_DETC_PARA(NativeLong ulAlarmTime, boolean bEnable, NativeLong ulSensitivity, NativeLong ulMinDectFace, NativeLong ulMaxDectFace, NativeLong ulDectMode, byte szReserved[]) {
		super();
		this.ulAlarmTime = ulAlarmTime;
		this.bEnable = bEnable;
		this.ulSensitivity = ulSensitivity;
		this.ulMinDectFace = ulMinDectFace;
		this.ulMaxDectFace = ulMaxDectFace;
		this.ulDectMode = ulDectMode;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_FACE_DETC_PARA(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_FACE_DETC_PARA implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_FACE_DETC_PARA implements Structure.ByValue {
		
	};
}
