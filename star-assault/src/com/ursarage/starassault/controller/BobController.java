package com.ursarage.starassault.controller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.ursarage.starassault.model.Barrel;
import com.ursarage.starassault.model.Block;
import com.ursarage.starassault.model.Bob;
import com.ursarage.starassault.model.Bob.State;
import com.ursarage.starassault.model.World;

import java.util.HashMap;
import java.util.Map;

public class BobController {

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
  private Bob mBob;
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
      new HashMap<BobController.Keys, Boolean>();

  static {
    mKeys.put(Keys.LEFT, false);
    mKeys.put(Keys.RIGHT, false);
    mKeys.put(Keys.JUMP, false);
  }

  private Array<Block> mCollidableBlockArray = new Array<Block>();

  public BobController(World world) {
    mWorld = world;
    mBob = world.getBob();
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

    if (mGrounded && mBob.getState().equals(State.JUMPING))
      mBob.setState(State.IDLE);

    if (!mBob.getState().equals(State.FLYING) && !mBob.getState().equals(State.WAITING)) {
      mBob.getAcceleration().y = GRAVITY;
      mBob.getAcceleration().mul(delta);
      mBob.getVelocity().add(mBob.getAcceleration().x, mBob.getAcceleration().y);
    }

    checkCollisions(delta);

    if (!mBob.getState().equals(State.FLYING) && !mBob.getState().equals(State.WAITING)) {
      mBob.getVelocity().x *= DAMP;
      mBob.setBarrel(null);
    }

    if (mBob.getVelocity().x > MAX_VEL)
      mBob.getVelocity().x = MAX_VEL;

    if (mBob.getVelocity().x < -MAX_VEL)
      mBob.getVelocity().x = -MAX_VEL;

    if (mReset) {
      mBob.getPosition().x = mWorld.getLevel().getStartingPosition().x;
      mBob.getPosition().y = mWorld.getLevel().getStartingPosition().y;
      mBob.getVelocity().x = 0;
      mBob.getVelocity().y = 0;
      mBob.getAcceleration().x = 0;
      mBob.getAcceleration().y = 0;
      mBob.setState(State.IDLE);
      mReset = false;
    }

    if (mBob.getState().equals(State.WAITING) && mBob.getDelayTimeRemaining() > 0.0f) {
      mBob.setDelayTimeRemaining(mBob.getDelayTimeRemaining() - delta);
    }

    if (!mBob.getState().equals(State.WAITING))
      mBob.update(delta);
  }

  private void checkCollisions(float delta) {

    if (mBob.getState().equals(State.WAITING))
      return;

    if (!mBob.getState().equals(State.FLYING))
      mBob.getVelocity().mul(delta);

    Rectangle bobRect = rectPool.obtain();
    bobRect.set(mBob.getBounds().x, mBob.getBounds().y, mBob.getBounds().width, mBob.getBounds().height);

    int startX, endX;
    int startY = (int)mBob.getBounds().y;
    int endY = (int)(mBob.getBounds().y + mBob.getBounds().height);

    if (mBob.getVelocity().x < 0)
      startX = endX = (int)Math.floor(mBob.getBounds().x + mBob.getVelocity().x);
    else
      startX = endX = (int)Math.floor(mBob.getBounds().x + mBob.getBounds().width + mBob.getVelocity().x);

    populateCollidableBlocks(startX, startY, endX, endY);

    bobRect.x += mBob.getVelocity().x;
    mWorld.getCollisionRectangles().clear();

    for (Barrel barrel : mWorld.getDrawableBarrels()) {
      if (barrel == null || barrel == mBob.getBarrel())
        continue;

      if (barrel.getBounds().contains(bobRect)) {
        mBob.getPosition().x = barrel.getPosition().x + barrel.getBounds().getWidth() / 2 - mBob.getBounds().getWidth() / 2;
        mBob.getPosition().y = barrel.getPosition().y + barrel.getBounds().getHeight() / 2 - mBob.getBounds().getHeight() / 2;
        mBob.getVelocity().x = barrel.getVelocity().x;
        mBob.getVelocity().y = barrel.getVelocity().y;
        mBob.setBarrel(barrel);

        mWorld.getCollisionRectangles().add(barrel.getBounds());
        break;
      }
    }

    for (Block block : mCollidableBlockArray) {
      if (block == null)
        continue;

      if (bobRect.overlaps(block.getBounds())) {
        if (mBob.getState().equals(State.FLYING))
          mBob.setState(State.JUMPING);
        if (mBob.getVelocity().x < 0)
          mBob.getPosition().x = block.getPosition().x + block.getBounds().width;
        else if (mBob.getVelocity().x > 0)
          mBob.getPosition().x = block.getPosition().x - mBob.getBounds().width - 0.0001f;
        mBob.getVelocity().x = 0;
        mWorld.getCollisionRectangles().add(block.getBounds());
        break;
      }
    }

    bobRect.x = mBob.getPosition().x;
    startX = (int)mBob.getBounds().x;
    endX = (int)(mBob.getBounds().x + mBob.getBounds().width);
    if (mBob.getVelocity().y < 0)
      startY = endY = (int)Math.floor(mBob.getBounds().y + mBob.getVelocity().y);
    else
      startY = endY = (int)Math.floor(mBob.getBounds().y + mBob.getBounds().height + mBob.getVelocity().y);

    populateCollidableBlocks(startX, startY, endX, endY);

    bobRect.y += mBob.getVelocity().y;

    for (Block block : mCollidableBlockArray) {
      if (block == null)
        continue;

      if (bobRect.overlaps(block.getBounds())) {
        if (mBob.getState().equals(State.FLYING))
          mBob.setState(State.JUMPING);
        if (mBob.getVelocity().y < 0) {
          mBob.getPosition().y = block.getPosition().y + block.getBounds().height;
          mGrounded = true;
        }
        else if (mBob.getVelocity().y > 0) {
          mBob.getPosition().y = block.getPosition().y - mBob.getBounds().height - 0.0001f;
        }
        mBob.getVelocity().y = 0;
        mWorld.getCollisionRectangles().add(block.getBounds());
        break;
      }
    }

    bobRect.y = mBob.getPosition().y;

    // Move Bob (unless waiting in barrel)
    if (!mBob.getState().equals(State.WAITING))
      mBob.getPosition().add(mBob.getVelocity());

    mBob.getBounds().x = mBob.getPosition().x;
    mBob.getBounds().y = mBob.getPosition().y;

    if (!mBob.getState().equals(State.FLYING) && !mBob.getState().equals(State.WAITING))
      mBob.getVelocity().mul(1 / delta);
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

  // Change Bob's state and parameters based on input controls
  private void processInput() {

    // Ignore any movement or jumping input while Bob is flying
    // through the air due to being shot out of a barrel
    if (mBob.getState().equals(State.FLYING))
      return;

    // Only accept jumping input if Bob is waiting inside a
    // barrel waiting to be shot
    if (mBob.getState().equals(State.WAITING)) {
      if (mKeys.get(Keys.JUMP) && !mBob.getBarrel().isAutomatic())
        mBob.setState(State.FLYING);
      return;
    }

    if (mKeys.get(Keys.JUMP)) {
      if (!mBob.getState().equals(State.JUMPING)) {
        mJumpingPressed = true;
        mJumpPressedTime = System.currentTimeMillis();
        mBob.setState(State.JUMPING);
        mBob.getVelocity().y = MAX_JUMP_SPEED;
        mGrounded = false;
      }
    }
    else {
      if (mJumpingPressed && ((System.currentTimeMillis() - mJumpPressedTime) >= LONG_JUMP_PRESS)) {
        mJumpingPressed = false;
      }
      else {
        if (mJumpingPressed)
          mBob.getVelocity().y = MAX_JUMP_SPEED;
      }
    }

    if (mKeys.get(Keys.LEFT)) {
      mBob.setFacingLeft(true);

      if (!mBob.getState().equals(State.JUMPING))
        mBob.setState(State.WALKING);

      mBob.getAcceleration().x = -ACCELERATION;
    }
    else if (mKeys.get(Keys.RIGHT)) {
      mBob.setFacingLeft(false);

      if (!mBob.getState().equals(State.JUMPING))
        mBob.setState(State.WALKING);

      mBob.getAcceleration().x = ACCELERATION;
    }
    else {
      if (!mBob.getState().equals(State.JUMPING))
        mBob.setState(State.IDLE);

      mBob.getAcceleration().x = 0;
    }
  }
}
