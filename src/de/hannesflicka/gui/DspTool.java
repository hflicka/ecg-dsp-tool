package de.hannesflicka.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import de.hannesflicka.dsp.Dsp;
import de.hannesflicka.dsp.FIRFilter;
import de.hannesflicka.dsp.Filter;
import de.hannesflicka.dsp.IIRFilter;
import de.hannesflicka.dsp.NonlinearFilter;
import de.hannesflicka.dsp.Signal;
import de.hannesflicka.dsp.SquareFilter;
import de.hannesflicka.dsp.TresholdFilter;

@SuppressWarnings({ "serial", "unused" })
public class DspTool extends JFrame {

	private JPanel contentPane;
	private SignalViewer signalA;
	private SignalViewer signalB;
	
	private Filter lowPass = new FIRFilter(new float[]{0.25f, 0.5f, 0.25f});

	private Filter qrsBandPass = new IIRFilter(
			new float[]{1.0f, 0.0f, -1.0f}, 
			new float[]{2.0f, 1.87635f, -0.9216f});
	
	private Filter panLowPass = new IIRFilter(
			new float[]{1.0f, 0f, 0f, 0f, 0f, 0f, 
						2.0f, 0f, 0f, 0f, 0f, 0f, 1.0f},
			new float[]{16.0f, 2.0f, 1.0f});
	
	private Filter panHighPass = new IIRFilter(
			new float[]{-1.0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
						0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
						32.0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
						0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1.0f},
			new float[]{32.0f, 1.0f});
	
	private Filter panDerivative = new IIRFilter(
			new float[]{2.0f, 1.0f, 0.0f, -1.0f, -2.0f}, 
			new float[]{2.0f});
	
	private Filter panIntegrate = new IIRFilter(
			new float[]{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
						1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
						1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
						1f, 1f, 1f, 1f, 1f, 1f},
			new float[]{30.0f});
	
	private NonlinearFilter square = new SquareFilter();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DspTool frame = new DspTool();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DspTool() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(800, 600));
		
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		JPanel panButtons = new JPanel();
		panButtons.setAlignmentY(Component.TOP_ALIGNMENT);
		contentPane.add(panButtons);
		panButtons.setLayout(new BoxLayout(panButtons, BoxLayout.Y_AXIS));
		
		JButton btnLoad = new JButton("Load");
		btnLoad.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int response = fc.showOpenDialog(DspTool.this);
				if (response == JFileChooser.APPROVE_OPTION) {
					float[] signal = Signal.readSignalFromFile(fc.getSelectedFile());
					signalA.setSignal(signal);
					signalB.setSignal(signal);
				}
			}
		});
		panButtons.add(btnLoad);
		
		JButton btnSwap = new JButton("Swap");
		btnSwap.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnSwap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float[] temp = signalA.getSignal();
				signalA.setSignal(signalB.getSignal());
				signalB.setSignal(temp);
			}
		});
		panButtons.add(btnSwap);
		
		JButton btnQrsDetection = new JButton("Complete QRS Detection");
		btnQrsDetection.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnQrsDetection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float[] signal = signalB.getSignal();
				signal = Dsp.filter(signal, panLowPass);
				signal = Dsp.filter(signal, panHighPass); 
				signal = Dsp.filter(signal, panDerivative); 
				signal = square.apply(signal);
				signal = Dsp.filter(signal, panIntegrate); 				
				float mean = Signal.getMean(signal);
				signal = new TresholdFilter(mean, 30.0f).apply(signal);
				signalB.setSignal(signal);
				//JOptionPane.showMessageDialog(DspTool.this, "energy: " + mean);
			}
		});
		
		JButton btnLowpassFilter = new JButton("Gauss Lowpass Filter");
		btnLowpassFilter.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnLowpassFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signalB.setSignal(Dsp.filter(signalB.getSignal(), lowPass));
			}
		});
		
		JButton btnCopyDown = new JButton("Copy Down");
		btnCopyDown.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnCopyDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signalB.setSignal(signalA.getSignal());
			}
		});
		panButtons.add(btnCopyDown);
		panButtons.add(btnLowpassFilter);
		
		JButton btnQrsDetectionLowpass = new JButton("QRS Detection Lowpass");
		btnQrsDetectionLowpass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signalB.setSignal(Dsp.filter(signalB.getSignal(), panLowPass));
			}
		});
		btnQrsDetectionLowpass.setAlignmentX(Component.CENTER_ALIGNMENT);
		panButtons.add(btnQrsDetectionLowpass);
		
		JButton btnQrsDetectionHighpass = new JButton("QRS Detection Highpass");
		btnQrsDetectionHighpass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signalB.setSignal(Dsp.filter(signalB.getSignal(), panHighPass));
			}
		});
		btnQrsDetectionHighpass.setAlignmentX(Component.CENTER_ALIGNMENT);
		panButtons.add(btnQrsDetectionHighpass);
		
		JButton btnQrsDetectionDerivative = new JButton("QRS Detection Derivative");
		btnQrsDetectionDerivative.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signalB.setSignal(Dsp.filter(signalB.getSignal(), panDerivative));
			}
		});
		btnQrsDetectionDerivative.setAlignmentX(Component.CENTER_ALIGNMENT);
		panButtons.add(btnQrsDetectionDerivative);
		
		JButton btnSquareSignal = new JButton("Square Signal");
		btnSquareSignal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signalB.setSignal(square.apply(signalB.getSignal()));
			}
		});
		btnSquareSignal.setAlignmentX(Component.CENTER_ALIGNMENT);
		panButtons.add(btnSquareSignal);
		
		JButton btnIntegrate = new JButton("Integrate");
		btnIntegrate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signalB.setSignal(Dsp.filter(signalB.getSignal(), panIntegrate));
			}
		});
		btnIntegrate.setAlignmentX(Component.CENTER_ALIGNMENT);
		panButtons.add(btnIntegrate);
		
		JButton btnApplyTreshold = new JButton("Apply Treshold");
		btnApplyTreshold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float[] signal = signalB.getSignal();
				float mean = Signal.getMean(signal);
				signalB.setSignal(new TresholdFilter(mean, 30.0f).apply(signal));
			}
		});
		btnApplyTreshold.setAlignmentX(Component.CENTER_ALIGNMENT);
		panButtons.add(btnApplyTreshold);
		panButtons.add(btnQrsDetection);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane);
		
		//qrsBandPass = Dsp.normalize(qrsBandPass);
		
		float[] signal = Signal.centerAroundMean(
				Signal.readSignalFromResourceOrFile(
						"data" + File.separatorChar + "ecg105.txt"));
	
		signalA = new SignalViewer(signal);
		splitPane.setLeftComponent(signalA);
		
		signalB = new SignalViewer(signal);
		splitPane.setRightComponent(signalB);
		pack();
		splitPane.setDividerLocation(0.5);
	}

}
