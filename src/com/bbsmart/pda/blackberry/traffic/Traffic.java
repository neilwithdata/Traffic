package com.bbsmart.pda.blackberry.traffic;

import com.bbsmart.pda.blackberry.traffic.ui.screens.GameScreen;

import net.rim.device.api.ui.UiApplication;

public class Traffic extends UiApplication {
	private static Traffic instance;

	public static Traffic getInstance() {
		return instance;
	}

	public static void main(final String[] args) {
		instance = new Traffic();
		instance.enterEventDispatcher();
	}

	public Traffic() {
		pushScreen(new GameScreen());
	}
}
