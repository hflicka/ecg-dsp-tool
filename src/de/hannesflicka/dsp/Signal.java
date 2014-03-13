package de.hannesflicka.dsp;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Signal {
    /**
     * Opens a file or resource and returns InputStream for that
     * @param resourceName
     * @return corresponding InputStream or null, if not found
     */
	public static InputStream openResourceOrFile(String resourceName) {
		InputStream res = Signal.class.getResourceAsStream(resourceName);
		if (res == null) {
			InputStream file;
			try {
				file = new BufferedInputStream(new FileInputStream(resourceName));
			} catch (FileNotFoundException e) {
				if (!resourceName.startsWith("/")) {
					res = Signal.class.getResourceAsStream("/" + resourceName);
					return res;
				}
				return null;
			}
			return file;
		}
		return res;
	}
	
	public static float[] readSignalFromResourceOrFile(String name) {
		return readSignalFromStream(openResourceOrFile(name));
	}
	
	public static float[] readSignalFromStream(InputStream inputStream) {
		Scanner s = new Scanner(inputStream);
		
		List<Float> data = new LinkedList<Float>();
		while (s.hasNextFloat()) {
			data.add(s.nextFloat());
		}
		s.close();
		Float[] f = data.toArray(new Float[data.size()]);
		float[] ret = new float[data.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = f[i].floatValue();
		}		
		return ret;
	}
	
	public static float[] readSignalFromFile(File file) {
		try {
			return readSignalFromStream(
					new BufferedInputStream(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void writeToFile(float[] signal, File file) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			for (float f : signal) {
				writer.append(String.format(Locale.US, "%.6f\n", f));				
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static float getMean(float[] signal) {
		float sum = 0.0f;
		for (float f : signal) {
			sum += f;
		}
		return sum / signal.length;
	}
	
	public static float getMin(float[] signal) {
		float min = Float.MAX_VALUE;
		for (float f : signal) {
			if (f < min) {
				min = f;
			}
		}
		return min;
	}
	
	public static float getMax(float[] signal) {
		float max = Float.MIN_VALUE;
		for (float f : signal) {
			if (f > max) {
				max = f;
			}
		}
		return max;
	}
	
	public static float[] getMinMaxMean(float[] signal) {
		float sum = 0.0f;
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		for (float f : signal) {
			sum += f;
			if (f > max) {
				max = f;
			}
			if (f < min) {
				min = f;
			}
		}
		return new float[]{min, max, sum / signal.length};
	}
	
	public static float getEnergy(float[] signal) {
		float sum = 0.0f;
		for (float f : signal) {
			sum += f * f;
		}
		return sum;
	}
	
	public static float getSecondMoment(float[] signal) {
		float mean = getMean(signal);
		float sum = 0.0f;
		for (float f : signal) {
			float g = f - mean;
			sum += g * g;
		}
		return sum;
	}
	
	public static float[] centerAroundMean(float[] signal) {
		float mean = getMean(signal);
		float[] ret = new float[signal.length];
		for (int i = 0; i < signal.length; i++) {
			ret[i] = signal[i] - mean;
		}
		return ret;
	}
}
