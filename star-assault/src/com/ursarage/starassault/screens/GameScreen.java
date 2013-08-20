package com.ursarage.starassault.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.ursarage.starassault.controller.BobController;
import com.ursarage.starassault.view.WorldRenderer;
import com.ursarage.starassault.model.World;

public class GameScreen implements Screen, InputProcessor {

  private World mWorld;
  private WorldRenderer mRenderer;
  private BobController mController;
  private int mWidth;
  private int mHeight;

  @Override
  public void render(float delta) {

    // Possible solution for character falling through floor.  I'm ignoring
    // any render updates greater than 0.1 seconds.  The side effect is
    // stuttering game play when machine is slow.  Would need to determine
    // if 0.1 seconds is good threshold to prevent falling through objects.
    if (delta >= 0.1)
      return;

    Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

    mController.update(delta);
    mRenderer.render(delta);
  }

  @Override
  public void resize(int width, int height) {
    mWidth = width;
    mHeight = height;
  }

  @Override
  public void show() {
    mWorld = new World();
    mRenderer = new WorldRenderer(mWorld, false);
    mController = new BobController(mWorld);
    Gdx.input.setInputProcessor(this);
  }

  @Override
  public void hide() {
    Gdx.input.setInputProcessor(null);
  }

  @Override
  public void pause() {
    // TODO Auto-generated method stub
  }

  @Override
  public void resume() {
    // TODO Auto-generated method stub
  }

  @Override
  public void dispose() {
    Gdx.input.setInputProcessor(null);
  }

  @Override
  public boolean keyDown(int keyCode) {
    if (keyCode == Keys.LEFT)
      mController.leftPressed();
    if (keyCode == Keys.RIGHT)
      mController.rightPressed();
    if (keyCode == Keys.SPACE)
      mController.jumpPressed();
    if (keyCode == Keys.R)
      mController.mReset = true;
    if (keyCode == Keys.D)
      mRenderer.setDebug(!mRenderer.getDebug());

    return true;
  }

  @Override
  public boolean keyUp(int keyCode) {
    if (keyCode == Keys.LEFT)
      mController.leftReleased();
    if (keyCode == Keys.RIGHT)
      mController.rightReleased();
    if (keyCode == Keys.SPACE)
      mController.jumpReleased();
    return true;
  }

  @Override
  public boolean keyTyped(char c) {
    return false;
  }

  @Override
  public boolean touchDown(int x, int y, int pointer, int button) {
    if (x < mWidth / 2 && y > mHeight / 2)
      mController.leftPressed();
    if (x > mWidth / 2 && y > mHeight / 2)
      mController.rightPressed();
    return true;
  }

  @Override
  public boolean touchUp(int x, int y, int pointer, int button) {
    if (x < mWidth / 2 && y > mHeight / 2)
      mController.leftReleased();
    if (x > mWidth / 2 && y > mHeight / 2)
      mController.rightReleased();
    return true;
  }

  @Override
  public boolean touchDragged(int i, int i2, int i3) {
    return false;
  }

  @Override
  public boolean mouseMoved(int i, int i2) {
    return false;
  }

  @Override
  public boolean scrolled(int i) {
    return false;
  }
}
