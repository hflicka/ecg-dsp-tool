package de.hannesflicka.dsp;

public class SquareFilter implements NonlinearFilter {

	@Override
	public float[] apply(float[] data) {
		float[] ret = new float[data.length];
		for (int i = 0; i < data.length; i++) {
			ret[i] = data[i] * data[i];
		}
		return ret;
	}

}
