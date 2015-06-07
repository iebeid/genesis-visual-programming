package edu.genesis.utilities;

import java.io.Serializable;


public class ProgrammingElement implements Serializable {
	String label = null;
	String tooltip = null;
	String format = null;
	String action = null;
        String image = null;
        String className = null;
	
        
        
	ProgrammingElement(String label, String tooltip, String format, String action, String image, String className) {
                super();
		this.label = label;
		this.tooltip = tooltip;
		this.format = format;
		this.action = action;
                this.image = image;
                this.className = className;
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
        public String getImage() {
		return image;
	}
        public String getClassName() {
		return className;
	}
        
        @Override
        public String toString() { return label; }
        
        public ProgrammingElement toObject() { return this; }
	

}