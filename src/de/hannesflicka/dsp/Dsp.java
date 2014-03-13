package de.hannesflicka.dsp;

import java.io.File;

public class Dsp {
	public static final float[] UNIT_IMPULSE = {1.0f};
	
	public static float[] convolve(float[] input, float[] h) {
		float[] ret = new float[input.length + h.length - 1];
		for (int i = 0; i < ret.length; i++) {
			float sum = 0.0f;
			for (int j = 0; j < h.length; j++) {
				// invariant: 0 <= i - j < input.length
				if (0 <= i - j && i - j < input.length) {
					sum += input[i - j] * h[j];
				}
			}
			ret[i] = sum;
		}
		return ret;
	}
	
	public static float[] filter(float[] input, Filter filter) {
		int len = input.length;
		float[] ret = filter(input, filter, len);
		return ret;
	}
	
	public static float[] filter(float[] input, Filter filter, int resultLength) {
		float[] a = filter.getFeedbackCoefficients();
		float[] b = filter.getForwardCoefficients();
		float a0inv = 1.0f / a[0];
		float[] ret = new float[resultLength];
		for (int i = 0; i < resultLength; i++) {
			float sum = 0.0f;
			// forwardCoefficients
			for (int j = 0; j < b.length; j++) {
				// invariant: 0 <= i - j < input.length
				if (0 <= i - j && i - j < input.length) {
					sum += b[j] * input[i - j];
				}
			}
			// feedback
			for (int k = 1; k < a.length; k++) {
				if (0 <= i - k && i - k < ret.length) {
					sum += a[k] * ret[i - k];
				}
			}
			sum *= a0inv;
			ret[i] = sum;
		}
		return ret;
	}
	
	public static void print(float[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.println("[" + i + "] = " + arr[i]);
		}
	}
	
	public static void print(float[] arr, int limit) {
		for (int i = 0; i < Math.min(arr.length, limit); i++) {
			System.out.println("[" + i + "] = " + arr[i]);
		}
	}
	
	public static void main(String[] args) {
		float[] data = Signal.readSignalFromFile(new File("data\\ecg105.txt"));
		print(data, 20);
		float[] processed = Dsp.filter(data, new FIRFilter(new float[]{1.0f}));
		print(processed, 20);
	}

    @Deprecated
	public static Filter normalizeFilter(Filter filter) {
		float[] forward = filter.getForwardCoefficients().clone();
		float[] feedback = filter.getFeedbackCoefficients().clone();
		int len = Math.max(forward.length, feedback.length);
        // the following calculations are approximate
		float[] response = filter(UNIT_IMPULSE, filter, 256 * len);
		float energy = Signal.getEnergy(response);
		feedback[0] *= Math.sqrt(energy);
		return new IIRFilter(forward, feedback);
	}
}
