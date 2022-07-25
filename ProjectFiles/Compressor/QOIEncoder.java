package ProjectFiles.Compressor;
/**
 * Submitted by: 
 * Student 1: Sofia Naer 	ID# 333815397
 * Student 2: Roi Yehezkel 	ID# 315331959
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.imageio.ImageIO;

public class QOIEncoder extends QOICompressor {
	public QOIEncoder(String path, String destination) {
		super(path, destination);
	}

	public void encode() {
		try {
			BufferedImage image = ImageIO.read(new File(this.path));
			FileOutputStream output = new FileOutputStream(this.destination);
			ObjectOutputStream save = new ObjectOutputStream(output);
			readDataFromImage(image, save); // read technical data from image
			encodePixel(save, 0); // encode the first pixel by QOI_OP_RGB/QOI_OP_RGBA
			int count = 0;
			// loop on the array of the pixel only one time, after that the data is encoded
			for (int i = 1; i < pixels.length; i++) {
				// QOI_OP_RUN Detected
				if (pixels[i] == pixels[i - 1] && count < 62) {
					count++;
					continue;
				}
				// QOI_OP_RUN ended and we save the counter
				else if (count != 0) {
					encodeByRun(save, count);
					count = 0;
				}
				// search for index in the previous cells to save as QOI_OP_INDEX
				if (searchByIndex(i) != -1)
					encodeByIndex(save, i);
				// this section is for RGB
				else if (channels == 3) {
					// search for small difference to encode as QOI_OP_DIFF
					if (differenceOneByte(pixels[i], pixels[i - 1]))
						encodeDiffOneByte(save, pixels[i], pixels[i - 1]);
					// search for big difference to encode as QOI_OP_LUMA
					else if (differenceTwoBytes(pixels[i], pixels[i - 1]))
						encodeDiffTwoBytes(save, pixels[i], pixels[i - 1]);
					// if we didn't to encode the data by the previous encode method we write the
					// pixel RGB
					else
						encodePixel(save, i);
				}
				// this section is for RGBA
				// in this section we can't encode the data by difference so we write the pixel
				// RGBA
				else
					encodePixel(save, i);
			}
			// after the loop we check if we still have data in QOI_OP_RUN to encode
			if (count != 0) {
				int runLength = QOI_OP_RUN | (count - 1);
				save.write(runLength);
			}
			save.close();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// read technical data from image
	private void readDataFromImage(BufferedImage image, ObjectOutputStream out) throws IOException {
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		this.channels = isAlpha(pixels);
		out.writeInt(width);
		out.writeInt(height);
		out.writeByte(channels);
	}

	// check if the image is RGB or RGBA
	private byte isAlpha(int[] pixels) {
		int num = 0xff000000;
		for (int i = 0; i < pixels.length; i++)
			if ((pixels[i] & 0xff000000) != num)
				return 4;
		return 3;
	}

	// search for index in the previous 64 cells and return the index if found
	private int searchByIndex(int index) {
		for (int i = index - 1; i > 0 && (index - i) < 64; i--) {
			if (pixels[index] == pixels[i])
				return index - i;
		}
		return -1;
	}

	// calculate the difference between the pixel and the previous for small
	// difference
	// difference(QOI_OP_DIFF)
	private boolean differenceOneByte(int pixel, int prevPixel) {
		int diffR = ((pixel & 0x00ff0000) >> 16) - ((prevPixel & 0x00ff0000) >> 16);
		int diffG = ((pixel & 0x0000ff00) >> 8) - ((prevPixel & 0x0000ff00) >> 8);
		int diffB = ((pixel & 0x000000ff)) - ((prevPixel & 0x000000ff));
		if (diffR >= -2 && diffR <= 1 && diffG >= -2 && diffG <= 1 && diffB >= -2 && diffB <= 1)
			return true;
		return false;
	}

	// calculate the difference between the pixel and the previous for big
	// difference
	// difference(QOI_OP_LUMA)
	private boolean differenceTwoBytes(int pixel, int prevPixel) {
		int diffR = ((pixel & 0x00ff0000) >> 16) - ((prevPixel & 0x00ff0000) >> 16);
		int diffG = ((pixel & 0x0000ff00) >> 8) - ((prevPixel & 0x0000ff00) >> 8);
		int diffB = ((pixel & 0x000000ff)) - ((prevPixel & 0x000000ff));
		if (diffG >= -32 && diffG <= 31 && (diffR - diffG) >= -8 && (diffR - diffG) <= 7 && (diffB - diffG) >= -8
				&& (diffB - diffG) <= 7)
			return true;
		return false;
	}

	// write the pixel separate by cases RGB and RGBA
	private void encodePixel(ObjectOutputStream out, int index) throws IOException {
		// RGBA
		if (this.channels == 4) {
			out.writeByte(QOI_OP_RGBA);
			out.writeInt(pixels[index]);
		}
		// RGB
		else {
			int r = (pixels[index] & 0x00ff0000) >> 16;
			int g = (pixels[index] & 0x0000ff00) >> 8;
			int b = (pixels[index] & 0x000000ff);
			out.writeByte(QOI_OP_RGB);
			out.writeByte(r);
			out.writeByte(g);
			out.writeByte(b);
		}
	}

	// save by runLength(QOI_OP_RUN)
	private void encodeByRun(ObjectOutputStream out, int count) throws IOException {
		int runLength = QOI_OP_RUN | (count - 1);
		out.write(runLength);
	}

	// save by index(QOI_OP_INDEX)
	private void encodeByIndex(ObjectOutputStream out, int index) throws IOException {
		int byIndex = QOI_OP_INDEX | searchByIndex(index);
		out.write(byIndex);
	}

	// save one byte by small difference(QOI_OP_DIFF)
	private void encodeDiffOneByte(ObjectOutputStream out, int pixel, int prevPixel) throws IOException {
		int diffR = ((pixel & 0x00ff0000) >> 16) - ((prevPixel & 0x00ff0000) >> 16);
		int diffG = ((pixel & 0x0000ff00) >> 8) - ((prevPixel & 0x0000ff00) >> 8);
		int diffB = ((pixel & 0x000000ff)) - ((prevPixel & 0x000000ff));
		int diff = QOI_OP_DIFF | (diffR + 2) << 4 | (diffG + 2) << 2 | (diffB + 2);
		out.write(diff);
	}

	// save two bytes by big difference(QOI_OP_LUMA)
	private void encodeDiffTwoBytes(ObjectOutputStream out, int pixel, int prevPixel) throws IOException {
		int diffR = ((pixel & 0x00ff0000) >> 16) - ((prevPixel & 0x00ff0000) >> 16);
		int diffG = ((pixel & 0x0000ff00) >> 8) - ((prevPixel & 0x0000ff00) >> 8);
		int diffB = ((pixel & 0x000000ff)) - ((prevPixel & 0x000000ff));
		int high = QOI_OP_LUMA | (diffG + 32);
		int low = (diffR - diffG + 8) << 4 | (diffB - diffG + 8);
		out.write(high);
		out.write(low);
	}
}
