package de.hannesflicka.dsp;

public interface Filter {
	public float[] getForwardCoefficients();
	public float[] getFeedbackCoefficients();
}
