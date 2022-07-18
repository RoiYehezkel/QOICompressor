package ProjectFiles.Compressor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.imageio.ImageIO;

public class QOIDecoder extends QOICompressor {
	public QOIDecoder(String path, String destination) {
		super(path, destination);
	}

	public void decode() {

		try {
			File file_in = new File(path);
			FileInputStream input = new FileInputStream(file_in);
			ObjectInputStream readFile = new ObjectInputStream(input);
			readDataOfImage(readFile);
			int index = 0;
			while (true) {
				int x = readFile.read();
				if (x != -1) // -1 is EOF
				{
					// RGB
					if (channels == 3) {
						if (x == QOI_OP_RGB)
							index = decodeByPixel(index, readFile);
						else {
							// separate x to xx000000 of key and 00xxxxxx of data
							int key = Byte.toUnsignedInt((byte) (x & 0xc0));
							int data = (byte) (x & 0x3f);
							if (key == QOI_OP_RUN)
								index = decodeByRun(index, data + 1);
							else if (key == QOI_OP_INDEX)
								index = decodeByIndex(index, data);
							else if (key == QOI_OP_DIFF)
								index = decodeByDiffOneByte(index, data);
							else if (key == QOI_OP_LUMA)
								index = decodeByDiffTwoBytes(index, data, readFile);
							// illegal header
							else
								throw new QOIException("The header isn't exist");
						}
					}
					// RGBA
					// in this section we can't decode by difference
					else if (channels == 4) {
						if (x == QOI_OP_RGBA)
							index = decodeByPixel(index, readFile);
						else {
							// separate x to xx000000 of key and 00xxxxxx of data
							int key = Byte.toUnsignedInt((byte) (x & 0xc0));
							int data = (byte) (x & 0x3f);
							if (key == QOI_OP_RUN)
								index = decodeByRun(index, data + 1);
							else if (key == QOI_OP_INDEX)
								index = decodeByIndex(index, data);
							// illegal header
							else
								throw new QOIException("The header isn't exist");
						}
					} else
						throw new QOIException("Number of channels is illegal");
				} else
					break;
			}
			// convert the pixel array to image to save after decoding the image
			BufferedImage pixelImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			pixelImage.setRGB(0, 0, width, height, pixels, 0, width);
			File imageFile = new File(this.destination);
			ImageIO.write(pixelImage, "bmp", imageFile);
			readFile.close();
			input.close();
			System.out.println("The file has decoded");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (QOIException e) {
			System.err.println(e.getMessage());
		}
	}

	// read technical data of the image from encoded file
	private void readDataOfImage(ObjectInputStream in) throws IOException {
		this.width = in.readInt();
		this.height = in.readInt();
		this.pixels = new int[width * height];
		this.channels = in.readByte();
	}

	// decode by pixel QOI_OP_RGBA/QOI_OP_RGB
	private int decodeByPixel(int index, ObjectInputStream in) throws IOException {
		if (this.channels == 3) {
			int r = in.read();
			int g = in.read();
			int b = in.read();
			int pixel = 0xff000000 | r << 16 | g << 8 | b;
			this.pixels[index++] = pixel;
		} else if (this.channels == 4) {
			int pixel = in.readInt();
			this.pixels[index++] = pixel;
		}
		return index;
	}

	// decode by runLength QOI_OP_RUN
	private int decodeByRun(int index, int count) {
		int pixel = this.pixels[index - 1];
		for (int i = index; i < index + count; i++) {
			pixels[i] = pixel;
		}
		return index + count;
	}

	// decode by runLength QOI_OP_INDEX
	private int decodeByIndex(int index, int data) {
		pixels[index] = pixels[index - data];
		index++;
		return index;
	}

	// decode by small difference QOI_OP_DIFF
	private int decodeByDiffOneByte(int index, int diff) {
		int diffR = ((diff & 0x30) >> 4) - 2; // 00110000
		int diffG = ((diff & 0x0c) >> 2) - 2; // 00001100
		int diffB = (diff & 0x03) - 2; // 00000011
		int prevPixel = pixels[index - 1];
		int prevR = (prevPixel & 0x00ff0000) >> 16;
		int prevG = (prevPixel & 0x0000ff00) >> 8;
		int prevB = (prevPixel & 0x000000ff);
		int r = prevR + diffR;
		int g = prevG + diffG;
		int b = prevB + diffB;
		int pixel = 0xff000000 | r << 16 | g << 8 | b;
		this.pixels[index++] = pixel;
		return index;
	}

	// decode 2 bytes by big difference QOI_OP_LUMA
	private int decodeByDiffTwoBytes(int index, int highByte, ObjectInputStream in) throws IOException {
		byte lowByte = in.readByte();
		int diffR = ((lowByte & 0xf0) >> 4) - 8; // 11110000
		int diffG = (highByte & 0x3f) - 32; // 00111111
		int diffB = (lowByte & 0x0f) - 8; // 00001111
		int prevPixel = pixels[index - 1];
		int prevR = (prevPixel & 0x00ff0000) >> 16;
		int prevG = (prevPixel & 0x0000ff00) >> 8;
		int prevB = (prevPixel & 0x000000ff);
		int r = prevR + diffR + diffG;
		int g = prevG + diffG;
		int b = prevB + diffB + diffG;
		int pixel = 0xff000000 | r << 16 | g << 8 | b;
		this.pixels[index++] = pixel;
		return index;
	}
}
