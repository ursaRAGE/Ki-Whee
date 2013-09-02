package com.ursarage.starassault.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bat {
  public static final float SIZE = 0.5f; // half a unit

  Vector2 mPosition = new Vector2();
  Rectangle mBounds = new Rectangle();

  public Bat(Vector2 position) {
    mPosition = position;
    mBounds.setX(position.x);
    mBounds.setY(position.y);
    mBounds.width = SIZE;
    mBounds.height = SIZE;
  }

  public Vector2 getPosition() {
    return mPosition;
  }

  public Rectangle getBounds() {
    return mBounds;
  }
}
