package com.ursarage.starassault.model;

import com.badlogic.gdx.math.Vector2;

public class Level {

  private int mWidth;
  private int mHeight;
  private Vector2 mStartingPosition;
  private Block[][] mBlockArray;
  private Barrel[][] mBarrelArray;
  private Bat[][] mBatArray;

  public Level() {
    loadDemoLevel();
  }

  public int getWidth() {
    return mWidth;
  }

  public int getHeight() {
    return mHeight;
  }

  public Vector2 getStartingPosition() {
    return mStartingPosition;
  }

  public Block[][] getBlockArray() {
    return mBlockArray;
  }

  public Barrel[][] getBarrelArray() {
    return mBarrelArray;
  }

  public Bat[][] getBatArray() {
    return mBatArray;
  }

  public Block getBlock(int x, int y) {
    return mBlockArray[x][y];
  }

  private void loadDemoLevel() {
    mWidth = 16;
    mHeight = 16;
    mStartingPosition = new Vector2(1, 1);
    mBlockArray = new Block[mWidth][mHeight];
    mBarrelArray = new Barrel[mWidth][mHeight];
    mBatArray = new Bat[mWidth][mHeight];

    for (int col = 0; col < mWidth; col++) {
      for (int row = 0; row < mHeight; row++) {
        mBlockArray[col][row] = null;
        mBarrelArray[col][row] = null;
        mBatArray[col][row] = null;
      }
    }

    for (int col = 0; col < 16; col++) {
      addBlock(col, 15);
    }

    for (int row = 0; row < 15; row++) {
      addBlock(0, row);
      addBlock(15, row);
    }

    addBlock(1, 0);
    addBlock(2, 0);
    addBlock(3, 0);
    addBlock(3, 1);
    addBlock(5, 0);
    addBlock(6, 0);
    addBlock(8, 0);
    addBlock(9, 0);
    addBlock(13, 2);
    addBlock(13, 4);
    addBlock(13, 6);
    addBlock(13, 8);
    addBlock(13, 10);
    addBlock(13, 11);
    addBlock(13, 12);
    addBlock(13, 13);
    addBlock(8, 11);
    addBlock(9, 11);
    addBlock(10, 11);
    addBlock(11, 11);
    addBlock(12, 11);
    addBlock(7, 12);
    addBlock(8, 12);
    addBlock(2, 13);
    addBlock(3, 13);
    addBlock(4, 13);
    addBlock(5, 13);
    addBlock(6, 13);
    addBlock(7, 13);
    addBlock(10, 13);
    addBlock(10, 14);

    // Barrel run #1
    addBarrel(10, 0, 0.15f, 0.0f, 90.0f, 0.4f, true);
    addBarrel(14, 0, 0.15f, -90.0f, 90.0f, 0.3f, true);
    addBarrel(14, 3, 0.2f, -180.0f, 90.0f, 0.2f, true);
    addBarrel(12, 3, 0.2f, 90.0f, -90.0f, 0.2f, true);
    addBarrel(12, 9, 0.3f, 180.0f, -90.0f, 0.2f, true);
    addBarrel(14, 9, 0.3f, -90.0f, 90.0f, 0.2f, true);
    addBarrel(14, 14, 0.3f, -180.0f, 90.0f, 0.2f, true);

    // Barrel run #2
    addBarrel(1, 13, 0.3f, 0.0f, 180.0f, 1.2f, true);
    addBarrel(1, 8, 0.3f, 0.0f, 45.0f, 0.25f, true);
    addBarrel(5, 12, 0.3f, 225.0f, -90.0f, 0.25f, true);
    addBarrel(9, 8, 0.3f, -45.0f, -135.0f, 0.25f, true);
    addBarrel(9, 6, 0.3f, 0.0f, -135.0f, 0.25f, true);
    addBarrel(7, 4, 0.3f, 45.0f, -135.0f, 0.25f, true);
    addBarrel(5, 4, 0.3f, 90.0f, -135.0f, 0.25f, true);
    addBarrel(2, 7, 0.3f, 135.0f, 45.0f, 0.25f, true);

    // Extra barrel
    addBarrel(9, 2, 0.2f, 0.0f, 0.0f, 0.0f, false);

    // Add enemies
    addBat(2, 3);
    addBat(12, 12);
    addBat(9, 14);
  }

  private void addBlock(int col, int row) {
    mBlockArray[col][row] = new Block(new Vector2(col, row));
  }

  private void addBarrel(int col, int row, float speed, float startingAngle, float rotationAngle, float delay, boolean automatic) {
    mBarrelArray[col][row] = new Barrel(new Vector2(col, row), speed, startingAngle, rotationAngle, delay, automatic);
  }

  private void addBat(int col, int row) {
    mBatArray[col][row] = new Bat(new Vector2(col, row));
  }
}
