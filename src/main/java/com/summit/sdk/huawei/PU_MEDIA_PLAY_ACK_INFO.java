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
public class PU_MEDIA_PLAY_ACK_INFO extends Structure {
	/** \u0368\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulChannelId;
	/** \u00fd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03a8\u04bb\ufffd\ufffd\u05be */
	public NativeLong ulSessionId;
	/** \ufffd\ufffd\u01b5\ufffd\ufffd\ufffd\ufffd(\ufffd\u063a\ufffd)\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulVideoPayload;
	/** \ufffd\ufffd\u01b5\ufffd\ufffd\ufffd\ufffd(\ufffd\u063a\ufffd)\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulAudioPayload;
	/** \u052a\ufffd\ufffd\u0778\ufffd\ufffd\ufffd(\ufffd\u063a\ufffd)\ufffd\ufffd\ufffd\ufffd */
	public NativeLong ulIgtPayload;
	/**
	 * \ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[32]
	 */
	public byte[] szReserved = new byte[32];
	public PU_MEDIA_PLAY_ACK_INFO() {
		super();
	}
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "ulSessionId", "ulVideoPayload", "ulAudioPayload", "ulIgtPayload", "szReserved");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffd\ufffd\ufffd<br>
	 * @param ulSessionId \u00fd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03a8\u04bb\ufffd\ufffd\u05be<br>
	 * @param ulVideoPayload \ufffd\ufffd\u01b5\ufffd\ufffd\ufffd\ufffd(\ufffd\u063a\ufffd)\ufffd\ufffd\ufffd\ufffd<br>
	 * @param ulAudioPayload \ufffd\ufffd\u01b5\ufffd\ufffd\ufffd\ufffd(\ufffd\u063a\ufffd)\ufffd\ufffd\ufffd\ufffd<br>
	 * @param ulIgtPayload \u052a\ufffd\ufffd\u0778\ufffd\ufffd\ufffd(\ufffd\u063a\ufffd)\ufffd\ufffd\ufffd\ufffd<br>
	 * @param szReserved \ufffd\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[32]
	 */
	public PU_MEDIA_PLAY_ACK_INFO(NativeLong ulChannelId, NativeLong ulSessionId, NativeLong ulVideoPayload, NativeLong ulAudioPayload, NativeLong ulIgtPayload, byte szReserved[]) {
		super();
		this.ulChannelId = ulChannelId;
		this.ulSessionId = ulSessionId;
		this.ulVideoPayload = ulVideoPayload;
		this.ulAudioPayload = ulAudioPayload;
		this.ulIgtPayload = ulIgtPayload;
		if ((szReserved.length != this.szReserved.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szReserved = szReserved;
	}
	public PU_MEDIA_PLAY_ACK_INFO(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends PU_MEDIA_PLAY_ACK_INFO implements Structure.ByReference {
		
	};
	public static class ByValue extends PU_MEDIA_PLAY_ACK_INFO implements Structure.ByValue {
		
	};
}
