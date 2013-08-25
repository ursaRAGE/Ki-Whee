package com.ursarage.starassault.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class World
{
  // Our player controlled hero
  Bob mBob;

  // A world has a level through which Bob needs to go through
  Level mLevel;

  // The collision boxes
  Array<Rectangle> mCollisionRectangles = new Array<Rectangle>();

  Vector2 mStartingPosition = new Vector2(1, 2);

  public World() {
    createDemoWorld();
  }

  public Array<Rectangle> getCollisionRectangles() {
    return mCollisionRectangles;
  }

  public Bob getBob() {
    return mBob;
  }

  public Level getLevel() {
    return mLevel;
  }

  public List<Block> getDrawableBlocks() { //int width, int height) {
    int x = (int)mBob.getPosition().x - mLevel.getWidth();
    int y = (int)mBob.getPosition().y - mLevel.getHeight();

    if (x < 0)
      x = 0;

    if (y < 0)
      y = 0;

    int x2 = x + 2 * mLevel.getWidth();
    int y2 = y + 2 * mLevel.getHeight();

    if (x2 > mLevel.getWidth())
      x2 = mLevel.getWidth() - 1;

    if (y2 > mLevel.getHeight())
      y2 = mLevel.getHeight() - 1;

    List<Block> blocks = new ArrayList<Block>();
    Block block;

    for (int col = x; col <= x2; col++) {
      for (int row = y; row <= y2; row++) {
        block = mLevel.getBlockArray()[col][row];
        if (block != null)
          blocks.add(block);
      }
    }

    return blocks;
  }

  public List<Barrel> getDrawableBarrels() {

    List<Barrel> barrels = new ArrayList<Barrel>();
    Barrel barrel;

    for (int col = 0; col < mLevel.getWidth(); col++) {
      for (int row = 0; row < mLevel.getHeight(); row++) {
        barrel = mLevel.getBarrelArray()[col][row];
        if (barrel != null)
          barrels.add(barrel);
      }
    }

    return barrels;
  }

  private void createDemoWorld() {
    mBob = new Bob(mStartingPosition);
    mLevel = new Level();
  }
}
