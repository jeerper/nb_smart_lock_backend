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
public class PU_DEC_CHANNEL_ABILITY extends Structure {
	/**
	 * \u04f3\ufffd\ufffd\ufffd\u03f5<br>
	 * C type : PU_DEC_CHANNEL_PORT_MAP_S
	 */
	public PU_DEC_CHANNEL_PORT_MAP stChannelPortMap;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u01b5\ufffd\ufffd\u02bd<br>
	 * C type : PU_ENCODE_TYPE_E[4]
	 */
	public int[] enVideoDecFormats = new int[4];
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u01b5\ufffd\ufffd\u02bd<br>
	 * C type : PU_ENCODE_TYPE_E[4]
	 */
	public int[] enAudioDecFormats = new int[4];
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u05b1\ufffd\ufffd\ufffd<br>
	 * C type : PU_RESOLUTION_TYPE_E
	 */
	public int enMaxDecResolution;
	/**
	 * \ufffd\ufffd\ufffd\u05a1\ufffd\ufffd  :30\u05a1|| 60 \u05a1<br>
	 * C type : PU_FRAME_RATE_E
	 */
	public int enMaxFrameRate;
	/** 0: no support 1: support */
	public boolean bSupportDecryption;
	/** 0: no support 1: support */
	public boolean bSupportWaterMark;
	public PU_DEC_CHANNEL_ABILITY() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("stChannelPortMap", "enVideoDecFormats", "enAudioDecFormats", "enMaxDecResolution", "enMaxFrameRate", "bSupportDecryption", "bSupportWaterMark");
	}
	/**
	 * @param stChannelPortMap \u04f3\ufffd\ufffd\ufffd\u03f5<br>
	 * C type : PU_DEC_CHANNEL_PORT_MAP_S<br>
	 * @param enVideoDecFormats \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u01b5\ufffd\ufffd\u02bd<br>
	 * C type : PU_ENCODE_TYPE_E[4]<br>
	 * @param enAudioDecFormats \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u01b5\ufffd\ufffd\u02bd<br>
	 * C type : PU_ENCODE_TYPE_E[4]<br>
	 * @param enMaxDecResolution \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u05b1\ufffd\ufffd\ufffd<br>
	 * C type : PU_RESOLUTION_TYPE_E<br>
	 * @param enMaxFrameRate \ufffd\ufffd\ufffd\u05a1\ufffd\ufffd  :30\u05a1|| 60 \u05a1<br>
	 * C type : PU_FRAME_RATE_E<br>
	 * @param bSupportDecryption 0: no support 1: support<br>
	 * @param bSupportWaterMark 0: no support 1: support
	 */
	public PU_DEC_CHANNEL_ABILITY(PU_DEC_CHANNEL_PORT_MAP stChannelPortMap, int enVideoDecFormats[], int enAudioDecFormats[], int enMaxDecResolution, int enMaxFrameRate, boolean bSupportDecryption, boolean bSupportWaterMark) {
		super();
		this.stChannelPortMap = stChannelPortMap;
		if ((enVideoDecFormats.length != this.enVideoDecFormats.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.enVideoDecFormats = enVideoDecFormats;
		if ((enAudioDecFormats.length != this.enAudioDecFormats.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.enAudioDecFormats = enAudioDecFormats;
		this.enMaxDecResolution = enMaxDecResolution;
		this.enMaxFrameRate = enMaxFrameRate;
		this.bSupportDecryption = bSupportDecryption;
		this.bSupportWaterMark = bSupportWaterMark;
	}
	public PU_DEC_CHANNEL_ABILITY(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_DEC_CHANNEL_ABILITY implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_DEC_CHANNEL_ABILITY implements Structure.ByValue {
		
	};
}
