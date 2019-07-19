package com.la.radar.endpoint.targetdetection;

import com.la.radar.RadarConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DspSetting implements RadarConfig {
    short range_mvg_avg_length = 0;	/**< Moving average filter LENGTH used for range */
    short range_thresh_type = 1;	/**< Rang ethreshold type is constant & Adaptive */

    int min_range_cm = 2;			/**< Minimum range below which targets are ignored */
    int max_range_cm = 3;			/**< Maximum range above which targets are ignored */
    int min_speed_kmh = 4;			/**< Minimum speed below which targets are ignored */
    int max_speed_kmh = 5;			/**< Maximum speed above which targets are ignored */
    int min_angle_degree = 6;		/**< Minimum angle below which targets are ignored. Not supported yet */
    int max_angle_degree = 7;       /**< Maximum angle above which targets are ignored. Not supported yet */
    int range_threshold = 8;		/**< Range FFT linear threshold below which targets are ignored */
    int speed_threshold = 9;		/**< Doppler FFT linear threshold below which targets are ignored */

    int adaptive_offset = 10;		/**< For adaptive threshold, this offset is used to be above the noise floor */
    short enable_tracking = 11;		/**< Enable / Disable tracking */
    short num_of_tracks = 12;		/**< Number of active tracks */

    short median_filter_length = 13;	/**< Depth of median filter uzsed to smoothen the angle values */
    short enable_mti_filter = 14;    /**< Enable / Disable MTI filter to remove static targets */

    int mti_filter_length = 15;	    /**< Length of MTI filter in terms of frame count after which static target should be killed */

    @Override
    public int getConfigType() {
        return RadarConfig.TYPE_DSP_SETTINGS;
    }
}
