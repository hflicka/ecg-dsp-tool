package de.hannesflicka.dsp;

public class FIRFilter implements Filter {
	private float[] forwardCoefficients;
	private static final float[] UNIT_IMPULSE = {1.0f};
	
	@Override
	public float[] getForwardCoefficients() {
		return forwardCoefficients;
	}
	
	@Override
	public float[] getFeedbackCoefficients() {
		return UNIT_IMPULSE;
	}

	public FIRFilter(float[] coefficients) {
		this.forwardCoefficients = coefficients;
	}
}
