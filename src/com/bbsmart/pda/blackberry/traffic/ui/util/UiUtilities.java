package com.bbsmart.pda.blackberry.traffic.ui.util;

import java.io.InputStream;

import net.rim.device.api.system.EncodedImage;

public class UiUtilities {
	private static final String EXIT_30x21_ICON_NAME = "exit-30x21.png";
	
	public static EncodedImage getExitImage() {
		return getImage(EXIT_30x21_ICON_NAME);
	}
	
	public static EncodedImage getImage(String imageName) {
		InputStream input;
		try {
			input = Class.forName("com.bbsmart.pda.blackberry.traffic.Traffic")
					.getResourceAsStream("/img/" + imageName);
			byte[] data = new byte[input.available()];
			input.read(data);

			return EncodedImage.createEncodedImage(data, 0, data.length);
		} catch (Exception e) {
			return null;
		}
	}
}
