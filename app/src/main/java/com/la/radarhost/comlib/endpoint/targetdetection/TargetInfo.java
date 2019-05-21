package com.la.radarhost.comlib.endpoint.targetdetection;

public class TargetInfo {
    int targetID;           /**< An unique ID of that target. */

    float level;            /**< The Level at the peak in dB relative to threshold. */

    float radius;           /**< The Distance of the target from the sensor. */

    float azimuth;          /**< The azimuth angle of the target. Positive values
                                    in right direction from the sensing board perspective. */
    float elevation;        /**< The elevation angle of the target. Positive values
                                    in up direction from the sensing board perspective. */
    float radial_speed;     /**< The change of radius per second. */

    float azimuth_speed;    /**< The change of azimuth angle per second. */

    float elevation_speed;  /**< The change of elevation angle per second. */

    final static int SIZE = 32;    /**< 1*sizeof(int)+7*sizeof(float) = 4Bytes+7*4Bytes = 32Bytes*/

    public TargetInfo(int targetID,
                      float level,
                      float radius,
                      float azimuth,
                      float elevation,
                      float radial_speed,
                      float azimuth_speed,
                      float elevation_speed) {
        this.targetID = targetID;
        this.level = level;
        this.radius = radius;
        this.azimuth = azimuth;
        this.elevation = elevation;
        this.radial_speed = radial_speed;
        this.azimuth_speed = azimuth_speed;
        this.elevation_speed = elevation_speed;
    }
}
