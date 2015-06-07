
public class ProgrammingElement {
	String label = null;
	String tooltip = null;
	String format = null;
	String action = null;
	
	ProgrammingElement(String label, String tooltip, String format, String action) {
		this.label = label;
		this.tooltip = tooltip;
		this.format = format;
		this.action = action;
	}
	
	public String getTooltip() {
		return tooltip;
	}
	public String getFormat() {
		return format;
	}
	public String getLabel() {
		return label;
	}
	public String getAction() {
		return action;
	}
	

}