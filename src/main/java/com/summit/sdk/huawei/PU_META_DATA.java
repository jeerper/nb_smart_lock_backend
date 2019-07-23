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
public class PU_META_DATA extends Structure {
	public short usCapacity;
	public short usValidNumber;
	/** C type : PU_UserData* */
	public com.summit.sdk.huawei.PU_UserData.ByReference pstMetaUserData;
	public PU_META_DATA() {
		this.setAlignType(ALIGN_NONE);
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("usCapacity", "usValidNumber", "pstMetaUserData");
	}
	/** @param pstMetaUserData C type : PU_UserData* */
	public PU_META_DATA(short usCapacity, short usValidNumber, com.summit.sdk.huawei.PU_UserData.ByReference pstMetaUserData) {
		this.setAlignType(ALIGN_NONE);
		this.usCapacity = usCapacity;
		this.usValidNumber = usValidNumber;
		this.pstMetaUserData = pstMetaUserData;
	}
	public PU_META_DATA(Pointer peer) {
		super(peer,ALIGN_NONE);
	}
	public static class ByReference extends PU_META_DATA implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_META_DATA implements Structure.ByValue {
		
	};
}
