package com.ursarage.starassault.controller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.ursarage.starassault.model.Barrel;
import com.ursarage.starassault.model.Bat;
import com.ursarage.starassault.model.Block;
import com.ursarage.starassault.model.Kiwi;
import com.ursarage.starassault.model.Kiwi.State;
import com.ursarage.starassault.model.World;

import java.util.HashMap;
import java.util.Map;

public class KiwiController {

  enum Keys {
    LEFT, RIGHT, JUMP
  }

  private static final long LONG_JUMP_PRESS = 150l;
  private static final float ACCELERATION = 20f;
  private static final float GRAVITY = -20f;
  private static final float MAX_JUMP_SPEED = 7f;
  private static final float DAMP = 0.90f;
  private static final float MAX_VEL = 4f;

  private World mWorld;
  private Kiwi mKiwi;
  private long mJumpPressedTime;
  private boolean mJumpingPressed = false;
  private boolean mGrounded = false;
  public boolean mReset = true;

  // This is the rectangle pool used in collision detection
  // Good to avoid instantiation each frame
  private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
    @Override
    protected Rectangle newObject() {
      return new Rectangle();
    }
  };

  static Map<Keys, Boolean> mKeys =
      new HashMap<KiwiController.Keys, Boolean>();

  static {
    mKeys.put(Keys.LEFT, false);
    mKeys.put(Keys.RIGHT, false);
    mKeys.put(Keys.JUMP, false);
  }

  private Array<Block> mCollidableBlockArray = new Array<Block>();

  public KiwiController(World world) {
    mWorld = world;
    mKiwi = world.getKiwi();
  }

  // Key presses and touches

  public void leftPressed() {
    mKeys.put(Keys.LEFT, true);
  }

  public void rightPressed() {
    mKeys.put(Keys.RIGHT, true);
  }

  public void jumpPressed() {
    mKeys.put(Keys.JUMP, true);
  }

  public void leftReleased() {
    mKeys.put(Keys.LEFT, false);
  }

  public void rightReleased() {
    mKeys.put(Keys.RIGHT, false);
  }

  public void jumpReleased() {
    mKeys.put(Keys.JUMP, false);
    mJumpingPressed = false;
  }

  // The main update method
  public void update(float delta) {

    processInput();

    if (mGrounded && mKiwi.getState().equals(State.JUMPING))
      mKiwi.setState(State.IDLE);

    if (!mKiwi.getState().equals(State.FLYING) && !mKiwi.getState().equals(State.WAITING)) {
      mKiwi.getAcceleration().y = GRAVITY;
      mKiwi.getAcceleration().mul(delta);
      mKiwi.getVelocity().add(mKiwi.getAcceleration().x, mKiwi.getAcceleration().y);
    }

    if (!mKiwi.getState().equals(State.FLYING) && !mKiwi.getState().equals(State.WAITING))
      mKiwi.getVelocity().mul(delta);

    if (!mKiwi.getState().equals(State.WAITING)) {
      checkCollisions(delta);

      // Move Kiwi (unless waiting in barrel)
      if (!mKiwi.getState().equals(State.WAITING))
        mKiwi.getPosition().add(mKiwi.getVelocity());

      mKiwi.getBounds().x = mKiwi.getPosition().x;
      mKiwi.getBounds().y = mKiwi.getPosition().y;

      if (!mKiwi.getState().equals(State.FLYING) && !mKiwi.getState().equals(State.WAITING))
        mKiwi.getVelocity().mul(1 / delta);
    }

    if (!mKiwi.getState().equals(State.FLYING) && !mKiwi.getState().equals(State.WAITING)) {
      mKiwi.getVelocity().x *= DAMP;
      mKiwi.setBarrel(null);
    }

    if (mKiwi.getVelocity().x > MAX_VEL)
      mKiwi.getVelocity().x = MAX_VEL;

    if (mKiwi.getVelocity().x < -MAX_VEL)
      mKiwi.getVelocity().x = -MAX_VEL;

    if (mReset) {
      mKiwi.getPosition().x = mWorld.getLevel().getStartingPosition().x;
      mKiwi.getPosition().y = mWorld.getLevel().getStartingPosition().y;
      mKiwi.getVelocity().x = 0;
      mKiwi.getVelocity().y = 0;
      mKiwi.getAcceleration().x = 0;
      mKiwi.getAcceleration().y = 0;
      mKiwi.setState(State.IDLE);
      mReset = false;
    }

    if (mKiwi.getState().equals(State.WAITING) && mKiwi.getDelayTimeRemaining() > 0.0f) {
      mKiwi.setDelayTimeRemaining(mKiwi.getDelayTimeRemaining() - delta);
    }

    if (!mKiwi.getState().equals(State.WAITING))
      mKiwi.update(delta);
  }

  private void checkCollisions(float delta) {

    if (mKiwi.getState().equals(State.DEAD))
      return;

    Rectangle kiwiRect = rectPool.obtain();
    kiwiRect.set(mKiwi.getBounds().x, mKiwi.getBounds().y, mKiwi.getBounds().width, mKiwi.getBounds().height);

    int startX, endX;
    int startY = (int) mKiwi.getBounds().y;
    int endY = (int)(mKiwi.getBounds().y + mKiwi.getBounds().height);

    if (mKiwi.getVelocity().x < 0)
      startX = endX = (int)Math.floor(mKiwi.getBounds().x + mKiwi.getVelocity().x);
    else
      startX = endX = (int)Math.floor(mKiwi.getBounds().x + mKiwi.getBounds().width + mKiwi.getVelocity().x);

    populateCollidableBlocks(startX, startY, endX, endY);

    kiwiRect.x += mKiwi.getVelocity().x;
    mWorld.getCollisionRectangles().clear();

    for (Barrel barrel : mWorld.getDrawableBarrels()) {
      if (barrel == null || barrel == mKiwi.getBarrel())
        continue;

      if (barrel.getBounds().contains(kiwiRect)) {
        mKiwi.getPosition().x = barrel.getPosition().x + barrel.getBounds().getWidth() / 2 - mKiwi.getBounds().getWidth() / 2;
        mKiwi.getPosition().y = barrel.getPosition().y + barrel.getBounds().getHeight() / 2 - mKiwi.getBounds().getHeight() / 2;
        mKiwi.getVelocity().x = barrel.getVelocity().x;
        mKiwi.getVelocity().y = barrel.getVelocity().y;
        mKiwi.setBarrel(barrel);

        mWorld.getCollisionRectangles().add(barrel.getBounds());
        break;
      }
    }

    for (Bat bat : mWorld.getDrawableBats()) {
      if (bat == null)
        continue;

      if (kiwiRect.contains(bat.getPosition().x + bat.getBounds().width / 2,
                           bat.getPosition().y + bat.getBounds().height / 2)) {
        mKiwi.getVelocity().x = 0.0f;
        mKiwi.getVelocity().y = 0.05f;
        mKiwi.setState(State.DEAD);
        mWorld.getCollisionRectangles().add(bat.getBounds());
        break;
      }
    }

    for (Block block : mCollidableBlockArray) {
      if (block == null)
        continue;

      if (kiwiRect.overlaps(block.getBounds())) {
        if (mKiwi.getState().equals(State.FLYING))
          mKiwi.setState(State.JUMPING);
        if (mKiwi.getVelocity().x < 0)
          mKiwi.getPosition().x = block.getPosition().x + block.getBounds().width;
        else if (mKiwi.getVelocity().x > 0)
          mKiwi.getPosition().x = block.getPosition().x - mKiwi.getBounds().width - 0.0001f;
        mKiwi.getVelocity().x = 0;
        mWorld.getCollisionRectangles().add(block.getBounds());
        break;
      }
    }

    kiwiRect.x = mKiwi.getPosition().x;
    startX = (int) mKiwi.getBounds().x;
    endX = (int)(mKiwi.getBounds().x + mKiwi.getBounds().width);
    if (mKiwi.getVelocity().y < 0)
      startY = endY = (int)Math.floor(mKiwi.getBounds().y + mKiwi.getVelocity().y);
    else
      startY = endY = (int)Math.floor(mKiwi.getBounds().y + mKiwi.getBounds().height + mKiwi.getVelocity().y);

    populateCollidableBlocks(startX, startY, endX, endY);

    kiwiRect.y += mKiwi.getVelocity().y;

    for (Block block : mCollidableBlockArray) {
      if (block == null)
        continue;

      if (kiwiRect.overlaps(block.getBounds())) {
        if (mKiwi.getState().equals(State.FLYING))
          mKiwi.setState(State.JUMPING);
        if (mKiwi.getVelocity().y < 0) {
          mKiwi.getPosition().y = block.getPosition().y + block.getBounds().height;
          mGrounded = true;
        }
        else if (mKiwi.getVelocity().y > 0) {
          mKiwi.getPosition().y = block.getPosition().y - mKiwi.getBounds().height - 0.0001f;
        }
        mKiwi.getVelocity().y = 0;
        mWorld.getCollisionRectangles().add(block.getBounds());
        break;
      }
    }
  }

  private void populateCollidableBlocks(int startX, int startY, int endX, int endY) {
    mCollidableBlockArray.clear();

    for (int x = startX; x <= endX; x++) {
      for (int y = startY; y <= endY; y++) {
        if (x >= 0 && x < mWorld.getLevel().getWidth() &&
            y >= 0 && y < mWorld.getLevel().getHeight()) {
          mCollidableBlockArray.add(mWorld.getLevel().getBlock(x, y));
        }
      }
    }
  }

  // Change Kiwi's state and parameters based on input controls
  private void processInput() {

    // Ignore any input if Kiwi is dead
    if (mKiwi.getState().equals(State.DEAD))
      return;

    // Ignore any movement or jumping input while Kiwi is flying
    // through the air due to being shot out of a barrel
    if (mKiwi.getState().equals(State.FLYING))
      return;

    // Only accept jumping input if Kiwi is waiting inside a
    // barrel waiting to be shot
    if (mKiwi.getState().equals(State.WAITING)) {
      if (mKeys.get(Keys.JUMP) && !mKiwi.getBarrel().isAutomatic())
        mKiwi.setState(State.FLYING);
      return;
    }

    if (mKeys.get(Keys.JUMP)) {
      if (!mKiwi.getState().equals(State.JUMPING)) {
        mJumpingPressed = true;
        mJumpPressedTime = System.currentTimeMillis();
        mKiwi.setState(State.JUMPING);
        mKiwi.getVelocity().y = MAX_JUMP_SPEED;
        mGrounded = false;
      }
    }
    else {
      if (mJumpingPressed && ((System.currentTimeMillis() - mJumpPressedTime) >= LONG_JUMP_PRESS)) {
        mJumpingPressed = false;
      }
      else {
        if (mJumpingPressed)
          mKiwi.getVelocity().y = MAX_JUMP_SPEED;
      }
    }

    if (mKeys.get(Keys.LEFT)) {
      mKiwi.setFacingLeft(true);

      if (!mKiwi.getState().equals(State.JUMPING))
        mKiwi.setState(State.WALKING);

      mKiwi.getAcceleration().x = -ACCELERATION;
    }
    else if (mKeys.get(Keys.RIGHT)) {
      mKiwi.setFacingLeft(false);

      if (!mKiwi.getState().equals(State.JUMPING))
        mKiwi.setState(State.WALKING);

      mKiwi.getAcceleration().x = ACCELERATION;
    }
    else {
      if (!mKiwi.getState().equals(State.JUMPING))
        mKiwi.setState(State.IDLE);

      mKiwi.getAcceleration().x = 0;
    }
  }
}
