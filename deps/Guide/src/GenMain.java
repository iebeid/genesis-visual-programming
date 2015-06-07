import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.text.*;
import javax.swing.text.DefaultHighlighter.*;
import java.util.*;

public class GenMain {

	JFrame frame = null;
	JScrollPane scrollBar = null;
	JScrollPane outputScrollBar = null;
	JScrollPane debugScrollBar = null;
	JSplitPane outputDebugSplitPane = null;
	JSplitPane codeSplitPane = null;
	JSplitPane mainSplitPane = null;
	
	JTextArea codeTextArea = null;
	JTextArea outputTextArea = null;
	JTextArea debugTextArea = null;
	
	JPanel mainPanel = null;
	JPanel panel1 = null;
	JPanel codePanel = null;
	JPanel outputPanel = null;
	JPanel debugPanel = null;
	JPanel buttonPanel = null;
	
	JLabel codeLabel = null;
	JLabel outputLabel = null;
	JLabel debugLabel = null;

	JButton loadButton = null;
	JButton saveButton = null;
	JButton runButton = null;
	
	ButtonAction buttonResponse = null;
	
	DefaultHighlightPainter defPaint = null;
	Highlighter highlighter = null;
	
	TextKeyPressListener textKeyPressResponse = null;
	
	JFileChooser fileDialog = null;
		
	JPanel toolBarPanel = null;
	
	GuideConfig initGuide = null;
	
	Map map = null;
	
	GenMain() {
		frame = new JFrame("GuIDE");
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		fileDialog = new JFileChooser();
		
		mainPanel = new JPanel();
		panel1 = new JPanel();
		codePanel = new JPanel();
		outputPanel = new JPanel();
		debugPanel = new JPanel();
		buttonPanel = new JPanel();		
		toolBarPanel = new JPanel();
		
		outputDebugSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, outputPanel, debugPanel);
		codeSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, codePanel, outputDebugSplitPane);		
		mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, toolBarPanel, codeSplitPane);

		mainPanel.setLayout(new BorderLayout());	
		outputPanel.setLayout(new BorderLayout());
		debugPanel.setLayout(new BorderLayout());
		codePanel.setLayout(new BorderLayout());
		panel1.setLayout(new BorderLayout());
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
		
		
		loadButton = new JButton("Load");
		saveButton = new JButton("Save");
		runButton = new JButton("Run");
		
		codeTextArea = new JTextArea(25,60);
		codeTextArea.setLineWrap(true);
		codeTextArea.setWrapStyleWord(true);
		
		outputTextArea = new JTextArea(5,60);
		outputTextArea.setLineWrap(true);
		outputTextArea.setWrapStyleWord(true);
		
		debugTextArea = new JTextArea(5,60);
		debugTextArea.setLineWrap(true);
		debugTextArea.setWrapStyleWord(true);
		
		outputDebugSplitPane.setOneTouchExpandable(true);
		codeSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setOneTouchExpandable(true);
		
		scrollBar = new JScrollPane(codeTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		outputScrollBar = new JScrollPane(outputTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		debugScrollBar = new JScrollPane(debugTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);		
		
		defPaint = new DefaultHighlightPainter(Color.CYAN);
		highlighter = codeTextArea.getHighlighter();
		
		textKeyPressResponse = new TextKeyPressListener(this);
		
		codeTextArea.addKeyListener(textKeyPressResponse);
		
		initGuide = new GuideConfig("guide.cfg");
		map = initGuide.getMap();
		
		buttonResponse = new ButtonAction(this);
		
		Iterator it = map.values().iterator();
		
		int buttonPanelWidth = 0;
		int buttonPanelHeight = 40;
		while (it.hasNext()) {
			ProgrammingElement pe = (ProgrammingElement)it.next();
			JButton b = new JButton(pe.getLabel());
			b.setActionCommand(pe.getAction());
			b.setToolTipText(pe.getTooltip());
			b.addActionListener(buttonResponse);
			Dimension buttonSize = b.getPreferredSize();
			buttonPanelWidth = buttonPanelWidth + buttonSize.width;
			if (buttonPanelWidth > 700) {
				buttonPanelHeight = buttonPanelHeight + 40;
				buttonPanelWidth = 0;
			}			
			toolBarPanel.add(b);
		}	  
		
		toolBarPanel.setPreferredSize(new Dimension(700, buttonPanelHeight));
		
		loadButton.setActionCommand("Load File");
		loadButton.addActionListener(buttonResponse);
		saveButton.setActionCommand("Save File");
		saveButton.addActionListener(buttonResponse);
		runButton.setActionCommand("Run");
        runButton.addActionListener(buttonResponse);		
		
		codeLabel = new JLabel("Code:", SwingConstants.LEFT);
		outputLabel = new JLabel("Output:", SwingConstants.LEFT);
		debugLabel = new JLabel("Debug:", SwingConstants.LEFT);
				
		codePanel.add(codeLabel, BorderLayout.PAGE_START);
		codePanel.add(scrollBar, BorderLayout.CENTER);
		outputPanel.add(outputLabel, BorderLayout.PAGE_START);
		outputPanel.add(outputScrollBar, BorderLayout.CENTER);
		debugPanel.add(debugLabel, BorderLayout.PAGE_START); 
		debugPanel.add(debugScrollBar, BorderLayout.CENTER);
		
		buttonPanel.add(loadButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(runButton);
		
		panel1.add(mainSplitPane, BorderLayout.CENTER);
		panel1.add(buttonPanel, BorderLayout.LINE_END);
			
		mainPanel.add(panel1, BorderLayout.CENTER);	
		
		frame.setContentPane(mainPanel);
		frame.setLocation(200,0);
		frame.pack();
		frame.setVisible(true);
	}

	public JFrame getFrame() {
		return frame;
	}
	
	public JTextArea getCodeTextArea() {
		return codeTextArea;
	}
	
	public JTextArea getOutputTextArea() {
		return outputTextArea;
	}
	
	public JTextArea getDebugTextArea() {
		return debugTextArea;
	}
	
	public DefaultHighlightPainter getDefaultHighlightPainter() {
		return defPaint;
	}
	
	public Highlighter getHighlighter() {
		return highlighter;
	}
	
	public JFileChooser getFileDialog() {
		return fileDialog;
	}
	
	public Map getMap() {
		return map;
	}

	public static void main(String args[]) {
		GenMain gm = new GenMain();
	}
	
}
