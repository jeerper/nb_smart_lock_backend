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
public class PU_DEVICE_PORT_CONFIG extends Structure {
	/** HTTP\ufffd\u02ff\ufffd */
	public short usHTTPPort;
	/** HTTPS\ufffd\u02ff\ufffd */
	public short usHTTPSPort;
	/** RTSP\ufffd\u02ff\ufffd */
	public short usRTSPPort;
	public PU_DEVICE_PORT_CONFIG() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("usHTTPPort", "usHTTPSPort", "usRTSPPort");
	}
	/**
	 * @param usHTTPPort HTTP\ufffd\u02ff\ufffd<br>
	 * @param usHTTPSPort HTTPS\ufffd\u02ff\ufffd<br>
	 * @param usRTSPPort RTSP\ufffd\u02ff\ufffd
	 */
	public PU_DEVICE_PORT_CONFIG(short usHTTPPort, short usHTTPSPort, short usRTSPPort) {
		super();
		this.usHTTPPort = usHTTPPort;
		this.usHTTPSPort = usHTTPSPort;
		this.usRTSPPort = usRTSPPort;
	}
	public PU_DEVICE_PORT_CONFIG(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_DEVICE_PORT_CONFIG implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_DEVICE_PORT_CONFIG implements Structure.ByValue {
		
	};
}
