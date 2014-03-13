package de.hannesflicka.dsp;

public class LinearFilterWrapper implements NonlinearFilter {

	Filter filter;
	int filterLength;
	
	public LinearFilterWrapper(Filter filter) {
		this.filter = filter;
		this.filterLength = Math.max(
				filter.getFeedbackCoefficients().length, 
				filter.getForwardCoefficients().length);
	}
	
	@Override
	public float[] apply(float[] data) {
		float[] ret = Dsp.filter(data, filter, data.length + filterLength - 1);
		return ret;
	}

}
