package com.seal.vertx;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class Constants {
    public static final int fps = 5;
    public static final long timeStep = 1000/fps;
    public static final float playerWidth = 1.0f / 18.0f;
    public static final float speed = 5.0f * (playerWidth / 1000f);
    public static final float wallEpsilon = 0.005f;
    public static final long deadTime = 3000;
    public static final long inactiveTimeout = 3000;
}
