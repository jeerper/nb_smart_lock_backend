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
public class PU_FACE_LIB_IMPORT_S extends Structure {
	/** \u0368\ufffd\ufffdID */
	public NativeLong ulChannelId;
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_FACE_PACKET_TYPE_E
	 */
	public int enPacketType;
	/**
	 * \ufffd\ufffd\ufffd<br>
	 * C type : PU_FACE_LIB_S
	 */
	public PU_FACE_LIB_S stFacelib;
	/**
	 * \ufffd\u013c\ufffd\u00b7\ufffd\ufffd\ufffd\ufffd\ufffd\u013c\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[256]
	 */
	public byte[] szPath = new byte[256];
	/**
	 * \ufffd\u013c\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[128]
	 */
	public byte[] szFileName = new byte[128];
	/**
	 * \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdCSV\ufffd\ufffd\ufffd\ufffd\u00b7\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdIVS_PU_ImportFaceLibEx\ufffd\u04ff\ufffd\ufffd\u0438\ufffd\ufffd\u05b6\u03b2\ufffd\u02b9\ufffd\ufffd<br>
	 * C type : CHAR[256]
	 */
	public byte[] szImportResult = new byte[256];
	/** \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdID */
	public NativeLong taskID;
	public PU_FACE_LIB_IMPORT_S() {
		this.setAlignType(ALIGN_NONE);
	}
	/** Conversion Error : sizeof(ULONG) */
	protected List<String > getFieldOrder() {
		return Arrays.asList("ulChannelId", "enPacketType", "stFacelib", "szPath", "szFileName", "szImportResult", "taskID");
	}
	/**
	 * @param ulChannelId \u0368\ufffd\ufffdID<br>
	 * @param enPacketType \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd<br>
	 * C type : PU_FACE_PACKET_TYPE_E<br>
	 * @param stFacelib \ufffd\ufffd\ufffd<br>
	 * C type : PU_FACE_LIB_S<br>
	 * @param szPath \ufffd\u013c\ufffd\u00b7\ufffd\ufffd\ufffd\ufffd\ufffd\u013c\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[256]<br>
	 * @param szFileName \ufffd\u013c\ufffd\ufffd\ufffd<br>
	 * C type : CHAR[128]<br>
	 * @param szImportResult \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdCSV\ufffd\ufffd\ufffd\ufffd\u00b7\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdIVS_PU_ImportFaceLibEx\ufffd\u04ff\ufffd\ufffd\u0438\ufffd\ufffd\u05b6\u03b2\ufffd\u02b9\ufffd\ufffd<br>
	 * C type : CHAR[256]<br>
	 * @param taskID \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdID
	 */
	public PU_FACE_LIB_IMPORT_S(NativeLong ulChannelId, int enPacketType, PU_FACE_LIB_S stFacelib, byte szPath[], byte szFileName[], byte szImportResult[], NativeLong taskID) {
		super();
		this.ulChannelId = ulChannelId;
		this.enPacketType = enPacketType;
		this.stFacelib = stFacelib;
		if ((szPath.length != this.szPath.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szPath = szPath;
		if ((szFileName.length != this.szFileName.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szFileName = szFileName;
		if ((szImportResult.length != this.szImportResult.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.szImportResult = szImportResult;
		this.taskID = taskID;
	}
	public PU_FACE_LIB_IMPORT_S(Pointer peer) {
		super(peer);
	}
	public static abstract class ByReference extends PU_FACE_LIB_IMPORT_S implements Structure.ByReference {
		
	};
	public static abstract class ByValue extends PU_FACE_LIB_IMPORT_S implements Structure.ByValue {
		
	};
}
