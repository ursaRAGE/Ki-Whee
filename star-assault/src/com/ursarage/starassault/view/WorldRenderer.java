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
import com.ursarage.starassault.model.Block;
import com.ursarage.starassault.model.Bob;
import com.ursarage.starassault.model.Bob.State;
import com.ursarage.starassault.model.World;

public class WorldRenderer {

  private static final float CAMERA_WIDTH = 10f;
  private static final float CAMERA_HEIGHT = 7f;
  private static final float RUNNING_FRAME_DURATION = 0.06f;

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
  private TextureRegion mTextureCurrentBobFrame;

  // Animations
  private Animation mAnimationWalkLeft;
  private Animation mAnimationWalkRight;
  private Animation mAnimationFlying;

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
    setCameraPosition();
    mSpriteBatch.setProjectionMatrix(mCamera.combined);

    mSpriteBatch.begin();
      drawBlocks();
      drawBob();
      drawBarrels();
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
    float cameraPositionX = mWorld.getBob().getPosition().x + mWorld.getBob().getBounds().width / 2;
    float cameraPositionY = mWorld.getBob().getPosition().y + mWorld.getBob().getBounds().height / 2;

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

    // Load flying frames
    TextureRegion[] flyingFrames = new TextureRegion[5];
    for (int i = 0; i < 5; i++) {
      flyingFrames[i] = new TextureRegion(mTextureBobFallLeft);
      //flyingFrames[i].
    }
  }

  private void drawBlocks() {
    for (Object objBlock : mWorld.getDrawableBlocks()) { //(int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)) {
      Block block = (Block) objBlock;
      mSpriteBatch.draw(mTextureBlock,
          block.getPosition().x,
          block.getPosition().y,
          Block.SIZE, Block.SIZE);
    }
  }

  private void drawBarrels() {
    for (Object objBarrel : mWorld.getDrawableBarrels()) { //(int) CAMERA_WIDTH, (int) CAMERA_HEIGHT)) {
      Barrel barrel = (Barrel) objBarrel;
      mSpriteBatch.draw(mTextureBarrel,
          barrel.getPosition().x, barrel.getPosition().y,
          Block.SIZE / 2, Block.SIZE / 2, Block.SIZE, Block.SIZE,
          1.0f, 1.0f, -barrel.getAngle() + 90.0f, true);
    }
  }

  private void drawBob() {
    Bob bob = mWorld.getBob();

    mTextureCurrentBobFrame = bob.isFacingLeft() ? mTextureBobIdleLeft : mTextureBobIdleRight;

    if (bob.getState().equals(State.WALKING)) {
      mTextureCurrentBobFrame = bob.isFacingLeft() ?
          mAnimationWalkLeft.getKeyFrame(bob.getStateTime(), true) :
          mAnimationWalkRight.getKeyFrame(bob.getStateTime(), true);
    }
    else if (bob.getState().equals(State.JUMPING)) {
      if (bob.getVelocity().y > 0)
        mTextureCurrentBobFrame = bob.isFacingLeft() ? mTextureBobJumpLeft : mTextureBobJumpRight;
      else
        mTextureCurrentBobFrame = bob.isFacingLeft() ? mTextureBobFallLeft : mTextureBobFallRight;
    }

    if (bob.getState().equals(State.FLYING)) {
      mSpriteBatch.draw(mTextureBobFallLeft,
          bob.getPosition().x, bob.getPosition().y,
          Bob.SIZE / 2, Bob.SIZE / 2, Bob.SIZE, Bob.SIZE,
          1.0f, 1.0f, -(1000.0f * bob.getStateTime()) + 90.0f, true);
    }
    else {
      mSpriteBatch.draw(mTextureCurrentBobFrame,
          bob.getPosition().x,
          bob.getPosition().y,
          Bob.SIZE, Bob.SIZE);
    }
  }

  private void drawCollisionBlocks() {
    mDebugRenderer.setProjectionMatrix(mCamera.combined);
    mDebugRenderer.begin(ShapeType.FilledRectangle);
    mDebugRenderer.setColor(new Color(1, 1, 1, 1));
    for (Rectangle rect : mWorld.getCollisionRects()) {
      mDebugRenderer.filledRect(rect.x, rect.y, rect.width, rect.height);
    }
    mDebugRenderer.end();
  }

  private void drawDebug() {
    mDebugRenderer.setProjectionMatrix(mCamera.combined);
    mDebugRenderer.begin(ShapeType.Rectangle);

    // Render blocks
    for (Object blockObj : mWorld.getDrawableBlocks()) { //(int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)) {
      Block block = (Block)blockObj;
      Rectangle rect = block.getBounds();
      mDebugRenderer.setColor(new Color(1, 0, 0, 1));
      mDebugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
    }

    // Render barrels
    for (Object barrelObj : mWorld.getDrawableBarrels()) { //(int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)) {
      Barrel barrel = (Barrel)barrelObj;
      Rectangle rect = barrel.getBounds();
      mDebugRenderer.setColor(new Color(0, 0, 1, 1));
      mDebugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
    }

    // Render Bob
    Bob bob = mWorld.getBob();
    Rectangle rect = bob.getBounds();
    mDebugRenderer.setColor(new Color(0, 1, 0, 1));
    mDebugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
    mDebugRenderer.end();

    // Render text
    Matrix4 mat = new Matrix4();
    mat.setToOrtho2D(0, 0, 400, 300);
    mSpriteBatch.setProjectionMatrix(mat);
    mSpriteBatch.begin();
    mFont.draw(mSpriteBatch, String.format("Pos x=%4.2f y=%4.2f",
        bob.getPosition().x, bob.getPosition().y), 10, 260);
    mFont.draw(mSpriteBatch, String.format("Vel x=%4.2f y=%4.2f",
        bob.getVelocity().x, bob.getVelocity().y), 10, 240);
    mFont.draw(mSpriteBatch, String.format("Acc x=%4.2f y=%4.2f",
        bob.getVelocity().x, bob.getAcceleration().y), 10, 220);
    mFont.draw(mSpriteBatch, bob.getStateString(), 10, 200);
    mFont.draw(mSpriteBatch, String.format("CamX %4.2f %4.2f %4.2f",
        CAMERA_WIDTH / 2,
        bob.getPosition().x,
        mWorld.getLevel().getWidth() - CAMERA_WIDTH / 2), 10, 180);
    mFont.draw(mSpriteBatch, String.format("CamY %4.2f %4.2f %4.2f",
        CAMERA_HEIGHT / 2,
        bob.getPosition().y,
        mWorld.getLevel().getHeight() - CAMERA_HEIGHT / 2), 10, 160);
    mFont.draw(mSpriteBatch, String.format("Delta=%6.2f Min=%6.2f Max=%6.2f",
        mCurrentDelta, mMinimumDelta, mMaximumDelta), 10, 140);
    mSpriteBatch.end();
  }
}
