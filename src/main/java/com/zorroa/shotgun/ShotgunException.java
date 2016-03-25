package com.zorroa.shotgun;

/**
 * Created by chambers on 3/25/16.
 */
public class ShotgunException extends RuntimeException {

    public ShotgunException() {}
    public ShotgunException(String message) {
        super(message);
    }
    public ShotgunException(String message, Exception ex) {
        super(message, ex);
    }

}
