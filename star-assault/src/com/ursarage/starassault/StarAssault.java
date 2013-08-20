package com.ursarage.starassault;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.ursarage.starassault.screens.GameScreen;

public class StarAssault extends Game
{
	@Override
	public void create()
  {
//    TexturePacker2.process("C:\\Users\\Jacob\\AndroidStudioProjects\\StarAssault\\star-assault-android\\assets\\images",
//        "C:\\Users\\Jacob\\AndroidStudioProjects\\StarAssault\\star-assault-android\\assets\\images\\textures", "textures.pack");

    setScreen(new GameScreen());
  }

  @Override
  public void resize(int width, int height)
  {
    // Override this function and do nothing to prevent improper
    // scaling of textures (debug blocks work regardless)
  }
}
