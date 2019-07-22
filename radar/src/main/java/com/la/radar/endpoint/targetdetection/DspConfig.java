package com.la.radar.endpoint.targetdetection;

import com.la.radar.RadarConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DspConfig implements RadarConfig {
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

    @Override
    public void setValue(String field, String value) {
        switch (field) {
            case "range_mvg_avg_length":
                range_mvg_avg_length = Short.parseShort(value); break;
            case "range_thresh_type":
                range_thresh_type = Short.parseShort(value); break;
            case "min_range_cm":
                min_range_cm = Integer.parseInt(value); break;
            case "max_range_cm":
                max_range_cm = Integer.parseInt(value); break;
            case "min_speed_kmh":
                min_speed_kmh = Integer.parseInt(value); break;
            case "max_speed_kmh":
                max_speed_kmh = Integer.parseInt(value); break;
            case "min_angle_degree":
                min_angle_degree = Integer.parseInt(value); break;
            case "max_angle_degree":
                max_angle_degree = Integer.parseInt(value); break;
            case "range_threshold":
                range_threshold = Integer.parseInt(value); break;
            case "speed_threshold":
                speed_threshold = Integer.parseInt(value); break;
            case "adaptive_offset":
                adaptive_offset = Integer.parseInt(value); break;
            case "enable_tracking":
                enable_tracking = Short.parseShort(value); break;
            case "num_of_tracks":
                num_of_tracks = Short.parseShort(value); break;
            case "median_filter_length":
                median_filter_length = Short.parseShort(value); break;
            case "enable_mti_filter":
                enable_mti_filter = Short.parseShort(value); break;
            case "mti_filter_length":
                mti_filter_length = Integer.parseInt(value); break;
            default: break;
        }
    }

    @Override
    public String getValue(String field) {
        switch (field) {
            case "range_mvg_avg_length":
                return Short.toString(range_mvg_avg_length);
            case "range_thresh_type":
                return Short.toString(range_thresh_type);
            case "min_range_cm":
                return Integer.toString(min_range_cm);
            case "max_range_cm":
                return Integer.toString(max_range_cm);
            case "min_speed_kmh":
                return Integer.toString(min_speed_kmh);
            case "max_speed_kmh":
                return Integer.toString(max_speed_kmh);
            case "min_angle_degree":
                return Integer.toString(min_angle_degree);
            case "max_angle_degree":
                return Integer.toString(max_angle_degree);
            case "range_threshold":
                return Integer.toString(range_threshold);
            case "speed_threshold":
                return Integer.toString(speed_threshold);
            case "adaptive_offset":
                return Integer.toString(adaptive_offset);
            case "enable_tracking":
                return Short.toString(enable_tracking);
            case "num_of_tracks":
                return Short.toString(num_of_tracks);
            case "median_filter_length":
                return Short.toString(median_filter_length);
            case "enable_mti_filter":
                return Short.toString(enable_mti_filter);
            case "mti_filter_length":
                return Integer.toString(mti_filter_length);
            default:
                return null;
        }
    }
}
