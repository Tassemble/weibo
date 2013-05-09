package com.techq.test;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.sun.imageio.plugins.png.PNGImageWriter;



public class TestCode {

	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		//http://login.sina.com.cn/cgi/pin.php?r=64755100&s=0&p=472f6e920f0b4d2529a8a318163da4fd4952
		HttpClient client = new DefaultHttpClient();
		System.out.println(readCheckerCode(client));
		
	}

	private static String readCheckerCode(HttpClient client) throws IOException,
			ClientProtocolException {
		HttpResponse response = client.execute(new HttpGet("http://login.sina.com.cn/cgi/pin.php?r=64755100&s=0&p=472f6e920f0b4d2529a8a318163da4fd4952"));
		InputStream is = response.getEntity().getContent();
		// Read from an input stream
	    Image image = ImageIO.read(is);
		// Use a label to display the image
		JFrame frame = new JFrame();
		JLabel label = new JLabel(new ImageIcon(image));
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		Scanner scanner = new Scanner(System.in);
		String code = scanner.nextLine();
		frame.dispose();
		return code;
	}
}
