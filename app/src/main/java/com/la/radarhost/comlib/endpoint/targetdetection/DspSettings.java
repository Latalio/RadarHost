package com.la.radarhost.comlib.endpoint.targetdetection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DspSettings {
    short range_mvg_avg_length;	/**< Moving average filter LENGTH used for range */
    short range_thresh_type;	/**< Rang ethreshold type is constant & Adaptive */

    int min_range_cm;			/**< Minimum range below which targets are ignored */
    int max_range_cm;			/**< Maximum range above which targets are ignored */
    int min_speed_kmh;			/**< Minimum speed below which targets are ignored */
    int max_speed_kmh;			/**< Maximum speed above which targets are ignored */
    int min_angle_degree;		/**< Minimum angle below which targets are ignored. Not supported yet */
    int max_angle_degree;       /**< Maximum angle above which targets are ignored. Not supported yet */
    int range_threshold;		/**< Range FFT linear threshold below which targets are ignored */
    int speed_threshold;		/**< Doppler FFT linear threshold below which targets are ignored */

    int adaptive_offset;		/**< For adaptive threshold, this offset is used to be above the noise floor */
    short enable_tracking;		/**< Enable / Disable tracking */
    short num_of_tracks;		/**< Number of active tracks */

    short median_filter_length;	/**< Depth of median filter uzsed to smoothen the angle values */
    short enable_mti_filter;    /**< Enable / Disable MTI filter to remove static targets */

    int mti_filter_length;	    /**< Length of MTI filter in terms of frame count after which static target should be killed */
}
