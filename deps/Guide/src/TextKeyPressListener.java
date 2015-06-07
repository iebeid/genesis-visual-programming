import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.text.*;
import javax.swing.text.DefaultHighlighter.*;

public class TextKeyPressListener extends KeyAdapter{

	GenMain gm = null;
	Highlighter highlighter = null;
	JTextArea codeTextArea = null;
	
	TextKeyPressListener(GenMain gm) {
		this.gm = gm;
		this.highlighter = gm.getHighlighter();	
		this.codeTextArea = gm.getCodeTextArea();			
	}
	
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		Highlighter.Highlight highlight[] = highlighter.getHighlights();
		int pos = codeTextArea.getCaretPosition();
		
		if (key == KeyEvent.VK_TAB || (key == KeyEvent.VK_I && e.isControlDown())) {			
			
			int i = 0;
			int size = highlight.length;
			boolean found = false;
			while (size > 0 && i < size && found == false) {
				int beg = highlight[i].getStartOffset();
				if (pos < beg) {
					codeTextArea.setCaretPosition(beg);
					found = true;
				}
				i++;
			}	
			
			if (found == false) {
				try {
					int length = codeTextArea.getLineEndOffset(codeTextArea.getLineCount() - 1);
					boolean isSpace = false;
					
					while (pos < length && found == false) {
						if (codeTextArea.getText(pos,1).equals(" ") || codeTextArea.getText(pos,1).equals("\n")) {
							isSpace = true;
						}
						else if (isSpace == true) {
							codeTextArea.setCaretPosition(pos);
							found = true;
							isSpace = false;					
						}	
						pos++;
					}
				}
				catch (BadLocationException ex) {
					
				}
			
			}
			 
			e.consume(); //consume the event so that it is not passed down and not treated as a regular TAB event anymore.		
		}
		else if ((key == KeyEvent.VK_I && e.isAltDown())) {
			int size = highlight.length;
			boolean found = false;
			int i = size - 1;
			while ((i >= 0) && found == false) {
				int end = highlight[i].getEndOffset();
				int beg = highlight[i].getStartOffset();
				if (pos > end) {
					codeTextArea.setCaretPosition(beg);
					found = true;
				}
				i--;
			}	
			
			if (found == false) {
				
				try {
					
					int length = codeTextArea.getLineEndOffset(codeTextArea.getLineCount() - 1);
					boolean isSpace = false;
					
					while (pos >= 0 && pos < length && found == false) {
						if (codeTextArea.getText(pos,1).equals(" ") || codeTextArea.getText(pos,1).equals("\n")) {
							isSpace = true;
						}
						else if (isSpace == true) {
							while (pos >= 0 && (!(codeTextArea.getText(pos,1).equals(" ") || codeTextArea.getText(pos,1).equals("\n")))) {
								pos--;
							}
							pos ++;
							codeTextArea.setCaretPosition(pos);
							found = true;
							isSpace = false;					
						}	
						pos--;
					}
				}
				catch (BadLocationException ex) {
					
				}			
			}
			e.consume(); //consume the event so that it is not passed down and not treated as a regular key press event anymore.				
		}
		else if ((key >= KeyEvent.VK_A && key <= KeyEvent.VK_Z) || key == KeyEvent.VK_SPACE || 
				(key >= KeyEvent.VK_0 && key <= KeyEvent.VK_9)) {
			int i = 0;
			int size = highlight.length;
			
			boolean found = false;
			while (size > 0 && i < size && found == false) {
				int beg = highlight[i].getStartOffset();
				int end = highlight[i].getEndOffset();
				if (pos >= beg && pos <= end) {
					highlighter.removeHighlight(highlight[i]);				
					codeTextArea.replaceRange("", beg, end);
					found = true;
				}
				i++;
			}
		
		}
	}

}