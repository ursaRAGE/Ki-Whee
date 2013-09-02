package com.ursarage.starassault.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.ursarage.starassault.model.Barrel;
import com.ursarage.starassault.model.Bat;
import com.ursarage.starassault.model.Block;
import com.ursarage.starassault.model.Kiwi;
import com.ursarage.starassault.model.Kiwi.State;
import com.ursarage.starassault.model.World;

public class WorldRenderer {

  private static final float CAMERA_WIDTH = 10f;
  private static final float CAMERA_HEIGHT = 7f;
  private static final float RUNNING_FRAME_DURATION = 0.06f;
  private static final float BAT_FRAME_DURATION = 0.1f;

  private World mWorld;
  private OrthographicCamera mCamera;

  // Items only used for debugging
  ShapeRenderer mDebugRenderer = new ShapeRenderer();
  float mCurrentDelta = 0.0f;
  float mMinimumDelta = 99999999.0f;
  float mMaximumDelta = -99999999.0f;

  // Textures
  private TextureRegion mTextureBobIdleLeft;
  private TextureRegion mTextureBobIdleRight;
  private TextureRegion mTextureBobJumpLeft;
  private TextureRegion mTextureBobJumpRight;
  private TextureRegion mTextureBobFallLeft;
  private TextureRegion mTextureBobFallRight;
  private TextureRegion mTextureBlock;
  private TextureRegion mTextureBarrel;
  private TextureRegion mTextureGameOver;

  // Animations
  private Animation mAnimationWalkLeft;
  private Animation mAnimationWalkRight;
  private Animation mAnimationBat;

  private SpriteBatch mSpriteBatch;
  private BitmapFont mFont;
  private boolean mDebugEnabled = false;

  public WorldRenderer(World world, boolean debug) {
    mWorld = world;
    mCamera = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
    mCamera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT);
    mCamera.position.set(CAMERA_WIDTH / 2f, CAMERA_HEIGHT / 2f, 0);
    mCamera.update();
    mDebugEnabled = debug;
    mSpriteBatch = new SpriteBatch();
    mFont = new BitmapFont();
    loadTextures();
  }

  public void render(float delta) {

    // Have camera follow Kiwi (unless he's dead)
    if (!mWorld.getKiwi().getState().equals(State.DEAD))
      setCameraPosition();

    mSpriteBatch.setProjectionMatrix(mCamera.combined);

    mSpriteBatch.begin();
      drawBlocks();
      drawKiwi();
      drawBarrels();
      drawBats();
      if (mWorld.getKiwi().getState().equals(State.DEAD))
        drawGameOverMessage();
    mSpriteBatch.end();

    if (mDebugEnabled) {
      mCurrentDelta = delta;
      if (delta < mMinimumDelta) mMinimumDelta = delta;
      if (delta > mMaximumDelta) mMaximumDelta = delta;
      drawCollisionBlocks();
      drawDebug();
    }
  }

  public void setDebug(boolean debug) {
    mDebugEnabled = debug;
  }

  public boolean getDebug() {
    return mDebugEnabled;
  }

  private void setCameraPosition() {
    float cameraPositionX = mWorld.getKiwi().getPosition().x + mWorld.getKiwi().getBounds().width / 2;
    float cameraPositionY = mWorld.getKiwi().getPosition().y + mWorld.getKiwi().getBounds().height / 2;

    if (cameraPositionX < CAMERA_WIDTH / 2)
      cameraPositionX = CAMERA_WIDTH / 2;
    if (cameraPositionX > mWorld.getLevel().getWidth() - CAMERA_WIDTH / 2)
      cameraPositionX = mWorld.getLevel().getWidth() - CAMERA_WIDTH / 2;
    if (cameraPositionY < CAMERA_HEIGHT / 2)
      cameraPositionY = CAMERA_HEIGHT / 2;
    if (cameraPositionY > mWorld.getLevel().getHeight() - CAMERA_HEIGHT / 2)
      cameraPositionY = mWorld.getLevel().getHeight() - CAMERA_HEIGHT / 2;

    mCamera.position.set(cameraPositionX, cameraPositionY, 0);
    mCamera.update();
  }

  private void loadTextures() {
    TextureAtlas atlas = new TextureAtlas(
        Gdx.files.internal("images/textures/textures.pack"));

    // Load static textures
    mTextureBobIdleLeft = atlas.findRegion("bob-01");
    mTextureBobIdleRight = new TextureRegion(mTextureBobIdleLeft);
    mTextureBobIdleRight.flip(true, false);
    mTextureBobJumpLeft = atlas.findRegion("bob-up");
    mTextureBobJumpRight = new TextureRegion(mTextureBobJumpLeft);
    mTextureBobJumpRight.flip(true, false);
    mTextureBobFallLeft = atlas.findRegion("bob-down");
    mTextureBobFallRight = new TextureRegion(mTextureBobFallLeft);
    mTextureBobFallRight.flip(true, false);
    mTextureBlock = atlas.findRegion("block");
    mTextureBarrel = atlas.findRegion("barrel");
    mTextureGameOver = atlas.findRegion("gameover");

    // Load walking left frames
    TextureRegion[] walkLeftFrames = new TextureRegion[5];
    for (int i = 0; i < 5; i++) {
      walkLeftFrames[i] = atlas.findRegion("bob-0" + (i + 2));
    }
    mAnimationWalkLeft = new Animation(RUNNING_FRAME_DURATION, walkLeftFrames);

    // Load walking right frames
    TextureRegion[] walkRightFrames = new TextureRegion[5];
    for (int i = 0; i < 5; i++) {
      walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
      walkRightFrames[i].flip(true, false);
    }
    mAnimationWalkRight = new Animation(RUNNING_FRAME_DURATION, walkRightFrames);

    // Load bat frames
    TextureRegion[] batFrames = new TextureRegion[4];
    batFrames[0] = atlas.findRegion("bat-01");
    batFrames[1] = atlas.findRegion("bat-02");
    batFrames[2] = new TextureRegion(batFrames[0]);
    batFrames[3] = atlas.findRegion("bat-03");
    mAnimationBat = new Animation(BAT_FRAME_DURATION, batFrames);
  }

  private void drawBlocks() {
    for (Object objBlock : mWorld.getDrawableBlocks()) {
      Block block = (Block) objBlock;
      mSpriteBatch.draw(mTextureBlock,
          block.getPosition().x,
          block.getPosition().y,
          Block.SIZE, Block.SIZE);
    }
  }

  private void drawBarrels() {
    for (Object objBarrel : mWorld.getDrawableBarrels()) {
      Barrel barrel = (Barrel) objBarrel;

      float rotationAngle = 0.0f;
      if (mWorld.getKiwi().getBarrel() == barrel) {
        rotationAngle = barrel.getRotationAngle() *
            (barrel.getDelay() - mWorld.getKiwi().getDelayTimeRemaining()) /
            barrel.getDelay();
      }

      mSpriteBatch.draw(mTextureBarrel,
          barrel.getPosition().x, barrel.getPosition().y,
          Block.SIZE / 2, Block.SIZE / 2, Block.SIZE, Block.SIZE,
          1.0f, 1.0f, -barrel.getStartingAngle() + 90.0f - rotationAngle, true);
    }
  }

  private void drawBats() {
    TextureRegion textureCurrentFrame = mAnimationBat.getKeyFrame(mWorld.getKiwi().getStateTime(), true);
    for (Object objBlock : mWorld.getDrawableBats()) {
      Bat bat = (Bat) objBlock;
      mSpriteBatch.draw(textureCurrentFrame,
          bat.getPosition().x,
          bat.getPosition().y,
          Bat.SIZE, Bat.SIZE);
    }
  }

  private void drawKiwi() {
    Kiwi kiwi = mWorld.getKiwi();

    TextureRegion textureCurrentFrame = kiwi.isFacingLeft() ? mTextureBobIdleLeft : mTextureBobIdleRight;

    if (kiwi.getState().equals(State.WALKING)) {
      textureCurrentFrame = kiwi.isFacingLeft() ?
          mAnimationWalkLeft.getKeyFrame(kiwi.getStateTime(), true) :
          mAnimationWalkRight.getKeyFrame(kiwi.getStateTime(), true);
    }
    else if (kiwi.getState().equals(State.JUMPING)) {
      if (kiwi.getVelocity().y > 0)
        textureCurrentFrame = kiwi.isFacingLeft() ? mTextureBobJumpLeft : mTextureBobJumpRight;
      else
        textureCurrentFrame = kiwi.isFacingLeft() ? mTextureBobFallLeft : mTextureBobFallRight;
    }

    if (kiwi.getState().equals(State.FLYING)) {
      mSpriteBatch.draw(mTextureBobFallLeft,
          kiwi.getPosition().x, kiwi.getPosition().y,
          Kiwi.SIZE / 2, Kiwi.SIZE / 2, Kiwi.SIZE, Kiwi.SIZE,
          1.0f, 1.0f, -(1000.0f * kiwi.getStateTime()) + 90.0f, true);
    }
    else {
      mSpriteBatch.draw(textureCurrentFrame,
          kiwi.getPosition().x,
          kiwi.getPosition().y,
          Kiwi.SIZE, Kiwi.SIZE);
    }
  }

  private void drawGameOverMessage() {
    // How do we get this message to be pixel perfect?
    // Should these be constant as other similar items?
    float messageWidth = mTextureGameOver.getRegionWidth() / 50;
    float messageHeight = mTextureGameOver.getRegionHeight() / 50;
    mSpriteBatch.draw(mTextureGameOver,
        mCamera.position.x - messageWidth / 2,
        mCamera.position.y - messageHeight / 2,
        messageWidth, messageHeight);
  }

  private void drawCollisionBlocks() {
    mDebugRenderer.setProjectionMatrix(mCamera.combined);
    mDebugRenderer.begin(ShapeType.FilledRectangle);
    mDebugRenderer.setColor(new Color(1, 1, 1, 1));
    for (Rectangle rect : mWorld.getCollisionRectangles()) {
      mDebugRenderer.filledRect(rect.x, rect.y, rect.width, rect.height);
    }
    mDebugRenderer.end();
  }

  private void drawDebug() {
    mDebugRenderer.setProjectionMatrix(mCamera.combined);
    mDebugRenderer.begin(ShapeType.Rectangle);

    // Render blocks
    for (Object blockObj : mWorld.getDrawableBlocks()) {
      Block block = (Block)blockObj;
      Rectangle rect = block.getBounds();
      mDebugRenderer.setColor(new Color(1, 0, 0, 1));
      mDebugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
    }

    // Render barrels
    for (Object barrelObj : mWorld.getDrawableBarrels()) {
      Barrel barrel = (Barrel)barrelObj;
      Rectangle rect = barrel.getBounds();
      mDebugRenderer.setColor(new Color(0, 0, 1, 1));
      mDebugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
    }

    // Render bats
    for (Object batObj : mWorld.getDrawableBats()) {
      Bat bat = (Bat)batObj;
      Rectangle rect = bat.getBounds();
      mDebugRenderer.setColor(new Color(1, 0, 0, 1));
      mDebugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
    }

    // Render Kiwi
    Kiwi kiwi = mWorld.getKiwi();
    Rectangle rect = kiwi.getBounds();
    mDebugRenderer.setColor(new Color(0, 1, 0, 1));
    mDebugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
    mDebugRenderer.end();

    // Render text
    Matrix4 mat = new Matrix4();
    mat.setToOrtho2D(0, 0, 400, 300);
    mSpriteBatch.setProjectionMatrix(mat);
    mSpriteBatch.begin();
    mFont.draw(mSpriteBatch, String.format("Pos x=%4.2f y=%4.2f",
        kiwi.getPosition().x, kiwi.getPosition().y), 10, 260);
    mFont.draw(mSpriteBatch, String.format("Vel x=%4.2f y=%4.2f",
        kiwi.getVelocity().x, kiwi.getVelocity().y), 10, 240);
    mFont.draw(mSpriteBatch, String.format("Acc x=%4.2f y=%4.2f",
        kiwi.getVelocity().x, kiwi.getAcceleration().y), 10, 220);
    mFont.draw(mSpriteBatch, kiwi.getStateString(), 10, 200);
    mFont.draw(mSpriteBatch, String.format("CamX %4.2f %4.2f %4.2f",
        CAMERA_WIDTH / 2,
        kiwi.getPosition().x,
        mWorld.getLevel().getWidth() - CAMERA_WIDTH / 2), 10, 180);
    mFont.draw(mSpriteBatch, String.format("CamY %4.2f %4.2f %4.2f",
        CAMERA_HEIGHT / 2,
        kiwi.getPosition().y,
        mWorld.getLevel().getHeight() - CAMERA_HEIGHT / 2), 10, 160);
    mFont.draw(mSpriteBatch, String.format("Delta=%6.2f Min=%6.2f Max=%6.2f",
        mCurrentDelta, mMinimumDelta, mMaximumDelta), 10, 140);
    mFont.draw(mSpriteBatch, String.format("Delay=%6.2f",
        kiwi.getDelayTimeRemaining()), 10, 120);
    mSpriteBatch.end();
  }
}
