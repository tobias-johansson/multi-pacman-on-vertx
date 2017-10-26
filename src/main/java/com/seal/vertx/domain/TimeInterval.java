package com.seal.vertx.domain;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class TimeInterval {
    public final long startTime;
    public final long endTime;

    public TimeInterval(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TimeInterval intersect(TimeInterval other) {
        return new TimeInterval(Math.max(startTime, other.startTime), Math.min(endTime, other.endTime));
    }
}
