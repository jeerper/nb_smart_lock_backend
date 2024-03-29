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
public class tagRoadPropertyUnit extends Structure {
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02bb\ufffd\ufffd\ufffd\ufffd: \ufffd\ufffd\ufffd\ufffd\u0225\ufffd\ufffd, \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_CAR_DRV_DIR_E
	 */
	public int enLaneDirection;
	/**
	 * \ufffd\ufffd\u05be\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02bb\ufffd\ufffd\ufffd\ufffd: \ufffd\ufffd\ufffd\ufffd\u05ea\ufffd\ufffd\u05b1\ufffd\u0435\ufffd<br>
	 * C type : PU_ITS_ROAD_DRV_DIR_E
	 */
	public int enSignDirection;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : ITS_ROAD_LINE_TYPE_E
	 */
	public int enLineType;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0363\ufffd\ufffd\ufffd\ufffd\u06b5\ufffd\u0377\u03a5\ufffd\ufffd\ufffd\u0436\ufffd<br>
	 * C type : ITS_ROAD_SIDE_SCENE_E
	 */
	public int enLaneSideScene;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_IMRS_LINE_S
	 */
	public PU_IMRS_LINE stLine;
	public tagRoadPropertyUnit() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("enLaneDirection", "enSignDirection", "enLineType", "enLaneSideScene", "stLine");
	}
	/**
	 * @param enLaneDirection \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02bb\ufffd\ufffd\ufffd\ufffd: \ufffd\ufffd\ufffd\ufffd\u0225\ufffd\ufffd, \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_ITS_CAR_DRV_DIR_E<br>
	 * @param enSignDirection \ufffd\ufffd\u05be\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02bb\ufffd\ufffd\ufffd\ufffd: \ufffd\ufffd\ufffd\ufffd\u05ea\ufffd\ufffd\u05b1\ufffd\u0435\ufffd<br>
	 * C type : PU_ITS_ROAD_DRV_DIR_E<br>
	 * @param enLineType \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : ITS_ROAD_LINE_TYPE_E<br>
	 * @param enLaneSideScene \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0363\ufffd\ufffd\ufffd\ufffd\u06b5\ufffd\u0377\u03a5\ufffd\ufffd\ufffd\u0436\ufffd<br>
	 * C type : ITS_ROAD_SIDE_SCENE_E<br>
	 * @param stLine \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_IMRS_LINE_S
	 */
	public tagRoadPropertyUnit(int enLaneDirection, int enSignDirection, int enLineType, int enLaneSideScene, PU_IMRS_LINE stLine) {
		super();
		this.enLaneDirection = enLaneDirection;
		this.enSignDirection = enSignDirection;
		this.enLineType = enLineType;
		this.enLaneSideScene = enLaneSideScene;
		this.stLine = stLine;
	}
	public tagRoadPropertyUnit(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends tagRoadPropertyUnit implements Structure.ByReference {
		
	};
	public static class ByValue extends tagRoadPropertyUnit implements Structure.ByValue {
		
	};
}
