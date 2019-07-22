package com.la.radar.endpoint.targetdetection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Target {
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
}
