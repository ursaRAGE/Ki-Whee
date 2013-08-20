package com.ursarage.starassault.model;

import com.badlogic.gdx.math.Vector2;

public class Level {

  private int mWidth;
  private int mHeight;
  private Block[][] mBlockArray;
  private Barrel[][] mBarrelArray;

  public int getWidth() {
    return mWidth;
  }

  public void setWidth(int width) {
    mWidth = width;
  }

  public int getHeight() {
    return mHeight;
  }

  public void setHeight(int height) {
    mHeight = height;
  }

  public Block[][] getBlockArray() {
    return mBlockArray;
  }

  public void setBlocks(Block[][] blocks) {
    mBlockArray = blocks;
  }

  public Barrel[][] getBarrelArray() {
    return mBarrelArray;
  }

  public void setBarrels(Barrel[][] barrels) {
    mBarrelArray = barrels;
  }

  public Level() {
    loadDemoLevel();
  }

  public Block getBlock(int x, int y) {
    return mBlockArray[x][y];
  }

  public Barrel getBarrel(int x, int y) {
    return mBarrelArray[x][y];
  }

  private void loadDemoLevel() {
    mWidth = 16;
    mHeight = 16;
    mBlockArray = new Block[mWidth][mHeight];
    mBarrelArray = new Barrel[mWidth][mHeight];

    for (int col = 0; col < mWidth; col++) {
      for (int row = 0; row < mHeight; row++) {
        mBlockArray[col][row] = null;
        mBarrelArray[col][row] = null;
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
    addBarrel(10, 0, 0.15f, 90.0f, true);
    addBarrel(14, 0, 0.15f, 0.0f, true);
    addBarrel(14, 3, 0.15f, -90.0f, true);
    addBarrel(12, 3, 0.15f, 0.0f, true);
    addBarrel(12, 9, 0.15f, 90.0f, true);
    addBarrel(14, 9, 0.15f, 0.0f, true);
    addBarrel(14, 14, 0.15f, -90.0f, true);

    // Barrel run #2
    addBarrel(1, 13, 0.2f, 180.0f, true);
    addBarrel(1, 8, 0.2f, 45.0f, true);
    addBarrel(5, 12, 0.2f, 135.0f, true);
    addBarrel(9, 8, 0.2f, 180.0f, true);
    addBarrel(9, 6, 0.2f, 225.0f, true);
    addBarrel(7, 4, 0.2f, 270.0f, true);
    addBarrel(5, 4, 0.2f, 315.0f, true);
    addBarrel(2, 7, 0.2f, 180.0f, true);

    // Extra barrel
    addBarrel(9, 2, 0.2f, 0.0f, false);
  }

  private void addBlock(int col, int row) {
    mBlockArray[col][row] = new Block(new Vector2(col, row));
  }

  private void addBarrel(int col, int row, float speed, float angle, boolean automatic) {
    mBarrelArray[col][row] = new Barrel(new Vector2(col, row), speed, angle, automatic);
  }
}

//  private void loadDemoLevel() {
//    mWidth = 10;
//    mHeight = 7;
//    mBlockArray = new Block[mWidth][mHeight];
//    mBarrelArray = new Barrel[mWidth][mHeight];
//
//    for (int col = 0; col < mWidth; col++) {
//      for (int row = 0; row < mHeight; row++) {
//        mBlockArray[col][row] = null;
//        mBarrelArray[col][row] = null;
//      }
//    }
//
//    for (int col = 0; col < 10; col++) {
//      mBlockArray[col][0] = new Block(new Vector2(col, 0));
//      mBlockArray[col][6] = new Block(new Vector2(col, 6));
//      if (col != 1 && col != 2) {
//        mBlockArray[col][1] = new Block(new Vector2(col, 1));
//      }
//    }
//
//    mBlockArray[9][2] = new Block(new Vector2(9, 2));
//    mBlockArray[9][3] = new Block(new Vector2(9, 3));
//    mBlockArray[9][4] = new Block(new Vector2(9, 4));
//    mBlockArray[9][5] = new Block(new Vector2(9, 5));
//
//    mBlockArray[6][3] = new Block(new Vector2(6, 3));
//    mBlockArray[6][4] = new Block(new Vector2(6, 4));
//    mBlockArray[6][5] = new Block(new Vector2(6, 5));
//
//    mBarrelArray[1][2] = new Barrel(new Vector2(1, 2), 0.2f, 45.0f);
//    mBarrelArray[4][5] = new Barrel(new Vector2(4, 5), 0.2f, 180.0f);
//  }
//}
