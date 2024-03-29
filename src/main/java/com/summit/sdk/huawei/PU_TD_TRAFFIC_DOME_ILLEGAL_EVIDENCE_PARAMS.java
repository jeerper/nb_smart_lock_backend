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
public class PU_TD_TRAFFIC_DOME_ILLEGAL_EVIDENCE_PARAMS extends Structure {
	/**
	 * \u03a5\u0363\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_TRAFFIC_DOME_ILLEGAL_PARKINK_PARAMS_S
	 */
	public PU_TD_TRAFFIC_DOME_ILLEGAL_PARKINK_PARAMS stIllegalParkingParams;
	/**
	 * \ufffd\ufffd\ufffd\u043c\ufffd\ufffd\u05e5\ufffd\u0139\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_TRAFFIC_DOME_REVERSE_DIRECTION_PARAMS_S
	 */
	public PU_TD_TRAFFIC_DOME_REVERSE_DIRECTION_PARAMS stRevDirectParams;
	/**
	 * \u03a5\ufffd\ufffd\ufffd\ufffd\u0377\u05e5\ufffd\u0139\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_TRAFFIC_DOME_U_TURN_PARAMS_S
	 */
	public PU_TD_TRAFFIC_DOME_U_TURN_PARAMS stUTurnParams;
	/**
	 * \u0479\ufffd\u07fc\ufffd\ufffd\u05e5\ufffd\u0139\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_TRAFFIC_DOME_OVER_LANE_LINE_PARAMS_S
	 */
	public PU_TD_TRAFFIC_DOME_OVER_LANE_LINE_PARAMS stOverLaneLineParams;
	/**
	 * \u03a5\ufffd\ufffd\ufffd\ufffd\ufffd\u05e5\ufffd\u0139\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_TRAFFIC_DOME_CHANGE_LANE_PARAMS_S
	 */
	public PU_TD_TRAFFIC_DOME_CHANGE_LANE_PARAMS stChangeLaneParams;
	/**
	 * \ufffd\ufffd\u057c\ufffd\ufffd\u05e5\ufffd\u0139\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_TRAFFIC_DOME_MOTOR_IN_BICYCLE_LANE_PARAMS_S
	 */
	public PU_TD_TRAFFIC_DOME_MOTOR_IN_BICYCLE_LANE_PARAMS stMiBLaneParams;
	public PU_TD_TRAFFIC_DOME_ILLEGAL_EVIDENCE_PARAMS() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("stIllegalParkingParams", "stRevDirectParams", "stUTurnParams", "stOverLaneLineParams", "stChangeLaneParams", "stMiBLaneParams");
	}
	/**
	 * @param stIllegalParkingParams \u03a5\u0363\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_TRAFFIC_DOME_ILLEGAL_PARKINK_PARAMS_S<br>
	 * @param stRevDirectParams \ufffd\ufffd\ufffd\u043c\ufffd\ufffd\u05e5\ufffd\u0139\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_TRAFFIC_DOME_REVERSE_DIRECTION_PARAMS_S<br>
	 * @param stUTurnParams \u03a5\ufffd\ufffd\ufffd\ufffd\u0377\u05e5\ufffd\u0139\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_TRAFFIC_DOME_U_TURN_PARAMS_S<br>
	 * @param stOverLaneLineParams \u0479\ufffd\u07fc\ufffd\ufffd\u05e5\ufffd\u0139\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_TRAFFIC_DOME_OVER_LANE_LINE_PARAMS_S<br>
	 * @param stChangeLaneParams \u03a5\ufffd\ufffd\ufffd\ufffd\ufffd\u05e5\ufffd\u0139\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_TRAFFIC_DOME_CHANGE_LANE_PARAMS_S<br>
	 * @param stMiBLaneParams \ufffd\ufffd\u057c\ufffd\ufffd\u05e5\ufffd\u0139\ufffd\ufffd\ufffd<br>
	 * C type : PU_TD_TRAFFIC_DOME_MOTOR_IN_BICYCLE_LANE_PARAMS_S
	 */
	public PU_TD_TRAFFIC_DOME_ILLEGAL_EVIDENCE_PARAMS(PU_TD_TRAFFIC_DOME_ILLEGAL_PARKINK_PARAMS stIllegalParkingParams, PU_TD_TRAFFIC_DOME_REVERSE_DIRECTION_PARAMS stRevDirectParams, PU_TD_TRAFFIC_DOME_U_TURN_PARAMS stUTurnParams, PU_TD_TRAFFIC_DOME_OVER_LANE_LINE_PARAMS stOverLaneLineParams, PU_TD_TRAFFIC_DOME_CHANGE_LANE_PARAMS stChangeLaneParams, PU_TD_TRAFFIC_DOME_MOTOR_IN_BICYCLE_LANE_PARAMS stMiBLaneParams) {
		super();
		this.stIllegalParkingParams = stIllegalParkingParams;
		this.stRevDirectParams = stRevDirectParams;
		this.stUTurnParams = stUTurnParams;
		this.stOverLaneLineParams = stOverLaneLineParams;
		this.stChangeLaneParams = stChangeLaneParams;
		this.stMiBLaneParams = stMiBLaneParams;
	}
	public PU_TD_TRAFFIC_DOME_ILLEGAL_EVIDENCE_PARAMS(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_TD_TRAFFIC_DOME_ILLEGAL_EVIDENCE_PARAMS implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_TD_TRAFFIC_DOME_ILLEGAL_EVIDENCE_PARAMS implements Structure.ByValue {
		
	};
}
