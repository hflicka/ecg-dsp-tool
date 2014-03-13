package de.hannesflicka.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.hannesflicka.dsp.Signal;

@SuppressWarnings("serial")
public class SignalViewer extends JPanel {
	
	private SignalPane signalPane;
	
	public SignalViewer(float[] signal) {
        super(new BorderLayout());        

        //Set up the drawing area.
        
        this.setMinimumSize(new Dimension(200,100));
        
        signalPane = new SignalPane(signal);
        signalPane.setBackground(Color.white);

        JScrollPane scroller = new JScrollPane(signalPane);
        //scroller.setPreferredSize(new Dimension(200, 200));

        add(scroller, BorderLayout.CENTER);
	}
	
	public void setZoomX(float zoomX) {
		signalPane.setZoomX(zoomX);
	}
	
	public void setZoomY(float zoomY) {
		signalPane.setZoomY(zoomY);
	}
	
	public void setSignal(float[] signal) {
		signalPane.setSignal(signal);
	}
	
	public float getZoomX() {
		return signalPane.getZoomX();
	}
	
	public float getZoomY() {
		return signalPane.getZoomY();
	}
	
	public float[] getSignal() {
		return signalPane.getSignal();
	}

	public class Rule extends JPanel {
		int orientation;
		static final int SIZE = 35;
		SignalPane pane;
		
		public Rule(int orientation, SignalPane pane) {
			this.orientation = orientation;
			this.pane = pane;
			super.setBackground(Color.GRAY);
		}
		
		public void setPreferredHeight(int ph) {
	        setPreferredSize(new Dimension(SIZE, ph));
	    }

	    public void setPreferredWidth(int pw) {
	        setPreferredSize(new Dimension(pw, SIZE));
	    }
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Rectangle clip = g.getClipBounds();
			
			g.setFont(new Font("SansSerif", Font.PLAIN, 10));
	        g.setColor(Color.black);
	        
	        int minval = (int) (pane.getZoomX() * pane.getSignal().length);
		}
	}
	
	public class SignalPane extends JPanel {
		private float[] signal;
		private float zoomX;
		private float zoomY;
		private float min;
		private float max;
		@SuppressWarnings("unused")
		private float mean;
		
		public SignalPane(float[] signal) {
			zoomX = 1.0f;
			zoomY = 1.0f;
			this.signal = signal;
			float[] minMaxMean = Signal.getMinMaxMean(signal);
			min = minMaxMean[0];
			max = minMaxMean[1];
			mean = minMaxMean[2];
			updateSize();
		}
		
		public void setZoomX(float zoomX) {
			this.zoomX = zoomX;
			updateSize();
			this.revalidate();
		}
		
		public void setZoomY(float zoomY) {
			this.zoomY = zoomY;
			updateSize();
			this.revalidate();
		}
		
		public void setSignal(float[] signal) {
			this.signal = signal;
			float[] minMaxMean = Signal.getMinMaxMean(signal);
			min = minMaxMean[0];
			max = minMaxMean[1];
			mean = minMaxMean[2];
			updateSize();
			this.revalidate();
			this.repaint();
		}
		
		public float[] getSignal() {
			return signal;
		}
		
		public float getZoomX() {
			return zoomX;
		}
		
		public float getZoomY() {
			return zoomY;
		}
		
		public void updateSize() {
			int xsize = (int)(signal.length * zoomX);
			int ysize = (int)((Math.max(max, 0.0f) - Math.min(min, 0.0f)) * zoomY); 
			this.setPreferredSize(new Dimension(xsize, ysize));
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int offsetY = this.getHeight() / 2;
			float median = 0.5f * (max + min);
			
			if (zoomX < 2.0) {
				for (int i = 0; i < signal.length - 1; i++) {
					g.drawLine((int)(i * zoomX), 
							(int)((median - signal[i]) * zoomY) + offsetY, 
							(int)((i + 1) * zoomX), 
							(int)((median - signal[i + 1]) * zoomY) + offsetY);
				}
			} else {
				throw new RuntimeException("not implemented");
			}
			
			g.setColor(Color.BLUE);
			g.drawLine(0, 
					(int)(median * zoomY) + offsetY, 
					(int)(signal.length * zoomX), 
					(int)(median * zoomY) + offsetY);
		}
	}
}
