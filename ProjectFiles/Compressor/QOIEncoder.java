package ProjectFiles.Compressor;

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
			readDataFromImage(image, save);
			encodePixel(save, 0);
			int count = 0;
			for (int i = 1; i < pixels.length; i++) {
				if (pixels[i] == pixels[i - 1] && count < 62) {
					count++;
					continue;
				} else if (count != 0) {
					encodeByRun(save, count);
					count = 0;
				}
				if (searchByIndex(i) != -1)
					encodeByIndex(save, i);
				else if (channels == 3) {
					if (differenceOneByte(pixels[i], pixels[i - 1]))
						encodeDiffOneByte(save, pixels[i], pixels[i - 1]);
					else if (differenceTwoBytes(pixels[i], pixels[i - 1]))
						encodeDiffTwoBytes(save, pixels[i], pixels[i - 1]);
					else
						encodePixel(save, i);
				} else
					encodePixel(save, i);
			}
			if (count != 0) {
				int runLength = QOI_OP_RUN | (count - 1);
				save.write(runLength);
			}
			save.close();
			output.close();
			System.out.println("The file has encoded");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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

	private byte isAlpha(int[] pixels) {
		int num = pixels[0] & 0xff000000;
		for (int i = 0; i < pixels.length; i++)
			if ((pixels[i] & 0xff000000) != num)
				return 4;
		return 3;
	}

	private int searchByIndex(int index) {
		for (int i = index - 1; i > 0 && (index - i) < 64; i--) {
			if (pixels[index] == pixels[i])
				return index - i;
		}
		return -1;
	}

	private boolean differenceOneByte(int pixel, int prevPixel) {
		int diffR = ((pixel & 0x00ff0000) >> 16) - ((prevPixel & 0x00ff0000) >> 16);
		int diffG = ((pixel & 0x0000ff00) >> 8) - ((prevPixel & 0x0000ff00) >> 8);
		int diffB = ((pixel & 0x000000ff)) - ((prevPixel & 0x000000ff));
		if (diffR >= -2 && diffR <= 1 && diffG >= -2 && diffG <= 1 && diffB >= -2 && diffB <= 1)
			return true;
		return false;
	}

	private boolean differenceTwoBytes(int pixel, int prevPixel) {
		int diffR = ((pixel & 0x00ff0000) >> 16) - ((prevPixel & 0x00ff0000) >> 16);
		int diffG = ((pixel & 0x0000ff00) >> 8) - ((prevPixel & 0x0000ff00) >> 8);
		int diffB = ((pixel & 0x000000ff)) - ((prevPixel & 0x000000ff));
		if (diffG >= -32 && diffG <= 31 && (diffR - diffG) >= -8 && (diffR - diffG) <= 7 && (diffB - diffG) >= -8
				&& (diffB - diffG) <= 7)
			return true;
		return false;
	}

	private void encodePixel(ObjectOutputStream out, int index) throws IOException {
		if (this.channels == 4) {
			out.writeByte(QOI_OP_RGBA);
			out.writeInt(pixels[index]);
		} else {
			int r = (pixels[index] & 0x00ff0000) >> 16;
			int g = (pixels[index] & 0x0000ff00) >> 8;
			int b = (pixels[index] & 0x000000ff);
			out.writeByte(QOI_OP_RGB);
			out.writeByte(r);
			out.writeByte(g);
			out.writeByte(b);
		}
	}

	private void encodeByRun(ObjectOutputStream out, int count) throws IOException {
		int runLength = QOI_OP_RUN | (count - 1);
		out.write(runLength);
	}

	private void encodeByIndex(ObjectOutputStream out, int index) throws IOException {
		int byIndex = QOI_OP_INDEX | searchByIndex(index);
		out.write(byIndex);
	}

	private void encodeDiffOneByte(ObjectOutputStream out, int pixel, int prevPixel) throws IOException {
		int diffR = ((pixel & 0x00ff0000) >> 16) - ((prevPixel & 0x00ff0000) >> 16);
		int diffG = ((pixel & 0x0000ff00) >> 8) - ((prevPixel & 0x0000ff00) >> 8);
		int diffB = ((pixel & 0x000000ff)) - ((prevPixel & 0x000000ff));
		int diff = QOI_OP_DIFF | (diffR + 2) << 4 | (diffG + 2) << 2 | (diffB + 2);
		out.write(diff);
	}

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
