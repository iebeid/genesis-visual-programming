import java.io.*;
import java.util.*;

public class GuideConfig {

	Map<String, ProgrammingElement> map = null;
	BufferedReader br = null;
	FileReader fr = null;
        String readLine(BufferedReader fr) {
           String line = null;
	   try {
              do {
                 line = br.readLine();
              } while (line != null && line.equals(""));
           } 
	   catch (FileNotFoundException fex) {
		
           }
	   catch (IOException ex) {
		
           }
           //System.out.println ("Line read:" + line);
           return line;
        }	
	GuideConfig(String file) {
		try {
			this.fr = new FileReader(file);
			this.br = new BufferedReader(fr);
			map = new LinkedHashMap<String, ProgrammingElement>();
			
			String label = null;
			String format = null;
			String toolTip = null;
			String action = null;
			int i = 0;
			while ((label=readLine(br)) != null) {
                                
				label = clean(label);
				format = readLine(br);
				format = clean(format);
				format = addNewLines(format);
				action = Integer.toString(i);
				toolTip =readLine(br);
				toolTip = clean(toolTip);
				ProgrammingElement pe = new ProgrammingElement(label, toolTip, format, action);
				map.put(action, pe);
				i++;
			}
		}
		catch (FileNotFoundException fex) {
		
		}
		catch (IOException ex) {
		
		}
	}
		public Map getMap() {
			return map;
		}
		
		protected String clean(String s) {
			s = s.trim();
			s = s.substring(s.indexOf("=") + 1);
			s = s.trim();
			return s;
		}
		
		protected String addNewLines(String s) {
			s = s.replaceAll("\\\\n", "\n");
			return s;
		}
    public static void main (String args[]) {
       GuideConfig guideConfig = new GuideConfig("Guide.cfg");
        
    }
}
