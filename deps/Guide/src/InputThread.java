import java.io.*;
import javax.swing.*;

class InputThread extends Thread {

    BufferedReader in = null;
	String s = null;
	String message = null;
	JTextArea textArea = null;
	InputThread(InputStream is, JTextArea area) {
		this.in = new BufferedReader(new InputStreamReader(is));
	    this.textArea = area;
	}
	
	public void run () {
		s = new String();
		try {
		while((s = in.readLine()) != null) {
			textArea.append(s + "\n");
		}
		in.close();
		}
		catch (IOException e) {
			System.out.println("input/output error");
		}
	}

}
