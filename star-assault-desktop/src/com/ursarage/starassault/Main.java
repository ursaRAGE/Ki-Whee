package com.ursarage.starassault;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ursarage.starassault.utilities.TexturePacker;

public class Main
{
	public static void main(String[] args)
  {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "star-assault";
		cfg.useGL20 = true;
		cfg.width = 480;
		cfg.height = 320;

		new LwjglApplication(new StarAssault(), cfg);
	}
}
