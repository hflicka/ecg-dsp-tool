package de.hannesflicka.dsp;

public class IIRFilter implements Filter {
	float[] forwardCoefficients;
	float[] feedback;
	
	public IIRFilter(float[] forwardCoefficients, float[] feedback) {
		this.forwardCoefficients = forwardCoefficients;
		this.feedback = feedback;
	}
	
	@Override
	public float[] getForwardCoefficients() {
		return forwardCoefficients;
	}

	@Override
	public float[] getFeedbackCoefficients() {
		return feedback;
	}

}
