package de.hannesflicka.dsp;

public class TresholdFilter implements NonlinearFilter {

	private float treshold;
	private float result;
	
	public TresholdFilter(float treshold, float result) {
		this.treshold = treshold;
		this.result = result;
	}
	
	@Override
	public float[] apply(float[] data) {
		float[] ret = new float[data.length];
		for (int i = 0; i < data.length; i++) {
			if (data[i] > treshold) {
				ret[i] = result;
			} else {
				ret[i] = 0.0f;
			}
		}
		return ret;
	}

}
