package com.ursarage.starassault.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bob {

  public enum State {
    IDLE, WALKING, JUMPING, FLYING, WAITING
  }

  public static final float SIZE = 0.5f; // half a unit

  Vector2 mPosition = new Vector2();
  Vector2 mVelocity = new Vector2();
  Vector2 mAcceleration = new Vector2();
  Rectangle mBounds = new Rectangle();
  State	mState = State.IDLE;
  boolean	mFacingLeft = true;
  float mStateTime = 0;
  Barrel mBarrel = null;

  public Bob(Vector2 position) {
    mPosition = position;
    mBounds.x = position.x;
    mBounds.y = position.y;
    mBounds.height = SIZE;
    mBounds.width = SIZE;
  }

  public Vector2 getPosition() {
    return mPosition;
  }

  public Vector2 getVelocity() {
    return mVelocity;
  }

  public Vector2 getAcceleration() {
    return mAcceleration;
  }

  public Rectangle getBounds() {
    return mBounds;
  }

  public void setFacingLeft(boolean facingLeft) {
    mFacingLeft = facingLeft;
  }

  public boolean isFacingLeft() {
    return mFacingLeft;
  }

  public float getStateTime() {
    return mStateTime;
  }

  public void setState(State newState) {
    mState = newState;
  }

  public State getState() {
    return mState;
  }

  public String getStateString() {
    switch (mState) {
      case IDLE: return "IDLE";
      case WALKING: return "WALKING";
      case JUMPING: return "JUMPING";
      case FLYING: return "FLYING";
      case WAITING: return "WAITING";
    }
    return "UNKNOWN";
  }

  public void setBarrel(Barrel barrel) {
    mBarrel = barrel;
  }

  public Barrel getBarrel() {
    return mBarrel;
  }

  public void update(float delta) {
    mStateTime += delta;
  }
}
