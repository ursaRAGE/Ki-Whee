package com.ursarage.starassault.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Block {
  public static final float SIZE = 1f;

  Vector2 mPosition = new Vector2();
  Rectangle mBounds = new Rectangle();

  public Block(Vector2 position) {
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
