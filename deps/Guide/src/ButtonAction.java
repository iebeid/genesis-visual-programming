import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.swing.text.*;
import javax.swing.text.DefaultHighlighter.*;
import java.util.*;

class ButtonAction implements ActionListener {
   JFileChooser fileDialog = null;
   JTextArea codeTextArea = null; 
   JTextArea debugTextArea = null;
   JTextArea outputTextArea = null;
   JFrame frame = null;
   DefaultHighlightPainter defPaint = null;
   Highlighter highlighter = null;
   Map map = null;
      
   ButtonAction (GenMain gm) {
      this.codeTextArea = gm.getCodeTextArea();
      this.outputTextArea = gm.getOutputTextArea();
      this.debugTextArea = gm.getDebugTextArea();
      this.frame = gm.getFrame();
      this.fileDialog = gm.getFileDialog();
      defPaint = gm.getDefaultHighlightPainter();
      highlighter = gm.getHighlighter();
      this.map = gm.getMap();
   }
   
   public void actionPerformed(ActionEvent e) {
      String s = new String();
      String action = new String();
      if (e.getSource() instanceof JMenuItem) {
         JMenuItem menuItem = (JMenuItem)e.getSource();
         action = menuItem.getActionCommand();
      }
      else if (e.getSource() instanceof JButton) {
         JButton button = (JButton)e.getSource();
         action = button.getActionCommand();
      }
      if (action.equals("Load File")) {
         int returnVal = fileDialog.showOpenDialog(frame);
         if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
               File f = fileDialog.getSelectedFile();
               FileReader fr = new FileReader(f);
               codeTextArea.read(fr, null); 
               fr.close();
               s = codeTextArea.getText();
               codeTextArea.setText("");
               parseAndDisplay(s);
            }
            catch(FileNotFoundException ex){
            
            }
            catch(IOException ex) {
            
            }
         }
      }
      else if (action.equals("Save File")) {
         int returnVal = fileDialog.showSaveDialog(frame);
         if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
               File f = fileDialog.getSelectedFile();
               FileWriter fw = new FileWriter(f);
               codeTextArea.write(fw); 
               fw.close();
            }
            catch(IOException ex) {
            
            }
         }         
      }
         else if (action.equals("Run")) {
         String code = codeTextArea.getText();
         try {
   
            Runtime rt = Runtime.getRuntime();
            
            Process proc = rt.exec("java -jar Genesis.jar -h");
            
            PrintWriter out = new PrintWriter(proc.getOutputStream(), true);
            
            out.println(code);
            out.close();
            
            new InputThread(proc.getInputStream(), outputTextArea).start();
            new InputThread(proc.getErrorStream(), debugTextArea).start();
            
            int exit = proc.waitFor();                 
         }
         catch (Exception excep) {
            System.out.println("there was a problem running the genesis compiler.");
         }            
         }         
      else {
         ProgrammingElement pe = (ProgrammingElement)map.get(action);
         if (pe != null) {
            String format = pe.getFormat();
            if (format != null) {
               parseAndDisplay(pe.getFormat());
            }
         }         
      }
   }
   
protected void parseAndDisplay(String s) {
   int pos = codeTextArea.getCaretPosition();
   codeTextArea.insert(s, pos);
   boolean parse = true;
   int k = 0;
   if (!((k = s.indexOf("_", 0)) > -1)) {
      if (!((k = s.indexOf("_", k+1)) > -1)) {
         parse = false;
      }
   }
   if (parse == true) {
      int len = s.length();
      int i = -1;
      try {
         while ((i < len) && ((i = s.indexOf("_", i + 1)) > -1)) {
            int j;
            j = s.indexOf("_", i + 1);
            highlighter.addHighlight(i + pos, j + pos + 1, defPaint);      
            i = j;
         }
      }
      catch (BadLocationException ex) {
      
      }
    }
}
   
}
