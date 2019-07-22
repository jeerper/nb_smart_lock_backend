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
public class PU_PORT_INFO extends Structure {
	/** \ufffd\ufffd\u05af\ufffd\u02ff\ufffd */
	public NativeLong ulInterLeavedPort;
	/** \ufffd\ufffd\u01b5RTP\ufffd\u02ff\ufffd */
	public NativeLong ulVideoRtpPort;
	/** \ufffd\ufffd\u01b5RTCP\ufffd\u02ff\ufffd */
	public NativeLong ulVideoRtcpPort;
	/** \ufffd\ufffd\u01b5RTP\ufffd\u02ff\ufffd */
	public NativeLong ulAudioRtpPort;
	/** \ufffd\ufffd\u01b5RTCP\ufffd\u02ff\ufffd */
	public NativeLong ulAudioRtcpPort;
	/** \ufffd\ufffd\ufffd\ufffdRTP\ufffd\u02ff\ufffd */
	public NativeLong ulIgtRtpPort;
	/** \ufffd\ufffd\ufffd\ufffdRTCP\ufffd\u02ff\ufffd */
	public NativeLong ulIgtRtcpPort;
	/** C type : CHAR[32] */
	public byte[] szReserved = new byte[32];
	public PU_PORT_INFO() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulInterLeavedPort", "ulVideoRtpPort", "ulVideoRtcpPort", "ulAudioRtpPort", "ulAudioRtcpPort", "ulIgtRtpPort", "ulIgtRtcpPort", "szReserved");
	}
	/**
	 * @param ulInterLeavedPort \ufffd\ufffd\u05af\ufffd\u02ff\ufffd<br>
	 * @param ulVideoRtpPort \ufffd\ufffd\u01b5RTP\ufffd\u02ff\ufffd<br>
	 * @param ulVideoRtcpPort \ufffd\ufffd\u01b5RTCP\ufffd\u02ff\ufffd<br>
	 * @param ulAudioRtpPort \ufffd\ufffd\u01b5RTP\ufffd\u02ff\ufffd<br>
	 * @param ulAudioRtcpPort \ufffd\ufffd\u01b5RTCP\ufffd\u02ff\ufffd<br>
	 * @param ulIgtRtpPort \ufffd\ufffd\ufffd\ufffdRTP\ufffd\u02ff\ufffd<br>
	 * @param ulIgtRtcpPort \ufffd\ufffd\ufffd\ufffdRTCP\ufffd\u02ff\ufffd<br>
	 * @param szReserved C type : CHAR[32]
	 */
	public PU_PORT_INFO(NativeLong ulInterLeavedPort, NativeLong ulVideoRtpPort, NativeLong ulVideoRtcpPort, NativeLong ulAudioRtpPort, NativeLong ulAudioRtcpPort, NativeLong ulIgtRtpPort, NativeLong ulIgtRtcpPort, byte szReserved[]) {
		super();
		this.ulInterLeavedPort = ulInterLeavedPort;
		this.ulVideoRtpPort = ulVideoRtpPort;
		this.ulVideoRtcpPort = ulVideoRtcpPort;
		this.ulAudioRtpPort = ulAudioRtpPort;
		this.ulAudioRtcpPort = ulAudioRtcpPort;
		this.ulIgtRtpPort = ulIgtRtpPort;
		this.ulIgtRtcpPort = ulIgtRtcpPort;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_PORT_INFO(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_PORT_INFO implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_PORT_INFO implements Structure.ByValue {
		
	};
}
