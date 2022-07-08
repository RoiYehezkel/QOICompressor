package IORunner;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
//import java.io.File;
import java.io.File;

public class Gui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	private JFrame frame;
	final String DEFAULT_TEXT = "QOI Compressor by Roi Yehezkel & Sofia Naer.";

	private String path = null;
	private String destination = null;
	private String name_of_file = null;
	private boolean file_selected;

	private JLabel background;
	private JButton compress = new JButton("Compress");
	private JButton decompress = new JButton("Decompress");
	private JButton upload_file = new JButton("Upload file");
	private JTextField upload_text = new JTextField();
	private JButton save_file = new JButton("Save to");
	private JTextField save_text = new JTextField();
	private JLabel console = new JLabel(DEFAULT_TEXT);

	final int WIDTH = 800;
	final int HEIGHT = 450;

	final int COM_BTN_POSX = 250;
	final int COM_BTN_POSY = 280;
	final int COM_BTN_WIDTH = 120;
	final int COM_BTN_HEIGHT = 30;

	final int DECOM_BTN_POSX = 430;
	final int DECOM_BTN_POSY = 280;
	final int DECOM_BTN_WIDTH = 120;
	final int DECOM_BTN_HEIGHT = 30;

	final int UPLOAD_BTN_POSX = 480;
	final int UPLOAD_BTN_POSY = 130;
	final int UPLOAD_BTN_WIDTH = 100;
	final int UPLOAD_BTN_HEIGHT = 25;

	final int UPLOAD_TEXT_POSX = 220;
	final int UPLOAD_TEXT_POSY = 130;
	final int UPLOAD_TEXT_WIDTH = 250;
	final int UPLOAD_TEXT_HEIGHT = 25;

	final int SAVE_BTN_POSX = 480;
	final int SAVE_BTN_POSY = 190;
	final int SAVE_BTN_WIDTH = 100;
	final int SAVE_BTN_HEIGHT = 25;

	final int SAVE_TEXT_POSX = 220;
	final int SAVE_TEXT_POSY = 190;
	final int SAVE_TEXT_WIDTH = 250;
	final int SAVE_TEXT_HEIGHT = 25;

	final int CONSOLE_POSX = 250;
	final int CONSOLE_POSY = 373;
	final int CONSOLE_WIDTH = 500;
	final int CONSOLE_HEIGHT = 50;

	public Gui(String header) {
		background = new JLabel(new ImageIcon("koi.jpg"));
		background.setLayout(null);
		background.setSize(WIDTH, HEIGHT);
		setLabels();
		add(background);
		setSize(WIDTH, HEIGHT + 30);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void setLabels() {
		compress.setLayout(null);
		decompress.setLayout(null);
		upload_file.setLayout(null);
		upload_text.setLayout(null);
		save_file.setLayout(null);
		save_text.setLayout(null);
		console.setLayout(null);

		compress.setBounds(COM_BTN_POSX, COM_BTN_POSY, COM_BTN_WIDTH, COM_BTN_HEIGHT);
		decompress.setBounds(DECOM_BTN_POSX, DECOM_BTN_POSY, DECOM_BTN_WIDTH, DECOM_BTN_HEIGHT);
		upload_file.setBounds(UPLOAD_BTN_POSX, UPLOAD_BTN_POSY, UPLOAD_BTN_WIDTH, UPLOAD_BTN_HEIGHT);
		upload_text.setBounds(UPLOAD_TEXT_POSX, UPLOAD_TEXT_POSY, UPLOAD_TEXT_WIDTH, UPLOAD_TEXT_HEIGHT);
		save_file.setBounds(SAVE_BTN_POSX, SAVE_BTN_POSY, SAVE_BTN_WIDTH, SAVE_BTN_HEIGHT);
		save_text.setBounds(SAVE_TEXT_POSX, SAVE_TEXT_POSY, SAVE_TEXT_WIDTH, SAVE_TEXT_HEIGHT);
		console.setBounds(CONSOLE_POSX, CONSOLE_POSY, CONSOLE_WIDTH, CONSOLE_HEIGHT);

		compress.setFocusable(false);
		compress.addActionListener((new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "click");

			}
		}));

		decompress.setFocusable(false);
		decompress.addActionListener((new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "click");

			}

		}));

		upload_file.setFocusable(false);
		upload_file.addActionListener((new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser file_upload = new JFileChooser();
				FileNameExtensionFilter filter1 = new FileNameExtensionFilter("Bitmap Images(.bmp)", "bmp");
				FileNameExtensionFilter filter2 = new FileNameExtensionFilter("QOI Images(.qoi)", "qoi");
				file_upload.addChoosableFileFilter(filter1);
				file_upload.addChoosableFileFilter(filter2);
				file_upload.setCurrentDirectory(new File("."));
				int res = file_upload.showOpenDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					path = file_upload.getSelectedFile().getAbsolutePath();
					name_of_file = path.substring(path.lastIndexOf("\\") + 1);
					upload_text.setText(file_upload.getSelectedFile().getAbsolutePath());
					destination = file_upload.getCurrentDirectory().getAbsolutePath();
					getFile();

				}

			}
		}));

		save_file.setFocusable(false);
		save_file.setEnabled(false);
		save_file.addActionListener((new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser save_dest = new JFileChooser();
				save_dest.setCurrentDirectory(new File("."));
				if (name_of_file.substring(name_of_file.length() - 3).equals("bmp"))
					save_dest.setSelectedFile(new File(name_of_file.substring(0, name_of_file.length() - 3) + "qoi"));
				else
					save_dest.setSelectedFile(new File(name_of_file.substring(0, name_of_file.length() - 3) + "bmp"));
				int res = save_dest.showSaveDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					destination = save_dest.getCurrentDirectory().getAbsolutePath();
					getFile();
				}
			}
		}));

		upload_text.setEditable(false);
		save_text.setEditable(false);

		console.setForeground(Color.white);

		background.add(compress);
		background.add(decompress);
		background.add(upload_file);
		background.add(upload_text);
		background.add(save_file);
		background.add(save_text);
		background.add(console);
	}

	private void getFile() {
		if (name_of_file.substring(name_of_file.length() - 3).equals("bmp")) {
			destination += "\\" + name_of_file.substring(0, name_of_file.length() - 3) + "qoi";
			save_text.setText(destination);
			console.setText(DEFAULT_TEXT);
			save_file.setEnabled(true);
		} else if (name_of_file.substring(name_of_file.length() - 3).equals("qoi")) {
			destination += "\\" + name_of_file.substring(0, name_of_file.length() - 3) + "bmp";
			save_text.setText(destination);
			console.setText(DEFAULT_TEXT);
			save_file.setEnabled(true);
		} else {
			console.setText("Unsupported format");
			save_text.setText("");
			save_file.setEnabled(false);
		}
	}
}
