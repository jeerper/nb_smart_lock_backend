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
public class PU_LANE_ATTR_PARA extends Structure {
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd/\ufffd\ufffd\u037e<br>
	 * C type : PU_ITS_ROAD_PURPOSE_E
	 */
	public int enLanePurpose;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_CAR_DRV_DIR_E
	 */
	public int enLaneDirection;
	public PU_LANE_ATTR_PARA() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("enLanePurpose", "enLaneDirection");
	}
	/**
	 * @param enLanePurpose \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd/\ufffd\ufffd\u037e<br>
	 * C type : PU_ITS_ROAD_PURPOSE_E<br>
	 * @param enLaneDirection \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_CAR_DRV_DIR_E
	 */
	public PU_LANE_ATTR_PARA(int enLanePurpose, int enLaneDirection) {
		super();
		this.enLanePurpose = enLanePurpose;
		this.enLaneDirection = enLaneDirection;
	}
	public PU_LANE_ATTR_PARA(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_LANE_ATTR_PARA implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_LANE_ATTR_PARA implements Structure.ByValue {
		
	};
}
