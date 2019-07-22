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
public class PU_TD_TOUR_TRACK_POINT_PARAMS extends Structure {
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd, \ufffd\ubce1\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0133\ufffd\ufffd\ufffd\u0461\ufffd\ufffdID \u04bb\ufffd\ufffd */
	public NativeLong ulIndex;
	/** \ufffd\ufffd\ufffd\ufffd\u0363\ufffd\ufffd\u02b1\ufffd\ufffd(\ufffd\ufffd\ufffd\ufffd) */
	public NativeLong ulStayDruation;
	public PU_TD_TOUR_TRACK_POINT_PARAMS() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulIndex", "ulStayDruation");
	}
	/**
	 * @param ulIndex \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd, \ufffd\ubce1\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0133\ufffd\ufffd\ufffd\u0461\ufffd\ufffdID \u04bb\ufffd\ufffd<br>
	 * @param ulStayDruation \ufffd\ufffd\ufffd\ufffd\u0363\ufffd\ufffd\u02b1\ufffd\ufffd(\ufffd\ufffd\ufffd\ufffd)
	 */
	public PU_TD_TOUR_TRACK_POINT_PARAMS(NativeLong ulIndex, NativeLong ulStayDruation) {
		super();
		this.ulIndex = ulIndex;
		this.ulStayDruation = ulStayDruation;
	}
	public PU_TD_TOUR_TRACK_POINT_PARAMS(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_TD_TOUR_TRACK_POINT_PARAMS implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_TD_TOUR_TRACK_POINT_PARAMS implements Structure.ByValue {
		
	};
}
