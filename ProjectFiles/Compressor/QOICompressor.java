package ProjectFiles.Compressor;
/**
 * Submitted by: 
 * Student 1: Sofia Naer 	ID# 333815397
 * Student 2: Roi Yehezkel 	ID# 315331959
 */

public abstract class QOICompressor {
	// final keys for the different type of encoding
	final int QOI_OP_INDEX = 0x00; // 00xxxxxx
	final int QOI_OP_DIFF = 0x40; // 01xxxxxx
	final int QOI_OP_LUMA = 0x80; // 10xxxxxx
	final int QOI_OP_RUN = 0xc0; // 11xxxxxx
	final int QOI_OP_RGB = 0xfe; // 11111110
	final int QOI_OP_RGBA = 0xff; // 11111111

	// variables
	protected String path, destination;
	protected int[] pixels;
	protected int width, height;
	protected byte channels;

	public QOICompressor(String path, String destination) {
		this.path = path;
		this.destination = destination;
	}

}
