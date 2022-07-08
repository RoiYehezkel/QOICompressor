package IORunner;

import Compressor.QOIDecoder;
import javax.swing.*;
import java.awt.*;
import Compressor.QOIEncoder;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
//ww  w  .  j av a2s  . com
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {

	public static void main(String[] args) {
//		QOIEncoder encoder = new QOIEncoder("Red_Flowers.bmp", "decoded.txt");
//		encoder.encode();
//		
//		
//		QOIDecoder decoder = new QOIDecoder("decoded.txt", "image.bmp");
//		decoder.decode();

		JFrame gui = new Gui("QOICompressor");
		gui.setVisible(true);

//		String s = "roi.goi";
//		String r = s.substring(0, s.length() - 3) + "bmp";
//		System.out.println(r);

	}

}
