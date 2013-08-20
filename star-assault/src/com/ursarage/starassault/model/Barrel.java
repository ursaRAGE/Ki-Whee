package com.ursarage.starassault.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Barrel {
  public static final float SIZE = 1f;

  Rectangle mBounds = new Rectangle();
  Vector2 mPosition = new Vector2();
  Vector2 mVelocity = new Vector2();
  float mAngle = 0.0f;
  boolean mAutomatic = true;

  public Barrel(Vector2 position, float speed, float angle, boolean automatic) {
    mBounds.setX(position.x);
    mBounds.setY(position.y);
    mBounds.width = SIZE;
    mBounds.height = SIZE;
    mPosition = position;
    mVelocity.x = speed * (float)(Math.sin(Math.toRadians(angle)));
    mVelocity.y = speed * (float)(Math.cos(Math.toRadians(angle)));
    mAngle = angle;
    mAutomatic = automatic;
  }

  public Rectangle getBounds() {
    return mBounds;
  }

  public Vector2 getPosition() {
    return mPosition;
  }

  public Vector2 getVelocity() {
    return mVelocity;
  }

  public float getAngle() {
    return mAngle;
  }

  public boolean isAutomatic() {
    return mAutomatic;
  }
}
