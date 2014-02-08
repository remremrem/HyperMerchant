package grokswell.util;

import java.util.ArrayList;

public class Uniquifier {
    int u_count_1;
    int u_count_2;
	String uniquifier; //minecraft color codes for invisible character in menu title
	private ArrayList<String> color_codes;
    public Uniquifier() {
	    this.color_codes = new ArrayList<String>();
	    this.color_codes.add("§0");
	    this.color_codes.add("§1");
	    this.color_codes.add("§2");
	    this.color_codes.add("§3");
	    this.color_codes.add("§4");
	    this.color_codes.add("§5");
	    this.color_codes.add("§6");
	    this.color_codes.add("§7");
	    this.color_codes.add("§8");
	    this.color_codes.add("§9");
	    this.color_codes.add("§a");
	    this.color_codes.add("§b");
	    this.color_codes.add("§c");
	    this.color_codes.add("§d");
	    this.color_codes.add("§e");
	    this.color_codes.add("§f");
	    this.u_count_1 = 0;
	    this.u_count_2 = 0;
    }
    
    public String uniquify() {
    	this.uniquifier = this.color_codes.get(this.u_count_1)+this.color_codes.get(this.u_count_2);
    	this.u_count_2 = this.u_count_2+1;
    	if (this.u_count_2 == 16) {
    		this.u_count_2 = 0;
    		this.u_count_1 = this.u_count_1+1;
        	if (this.u_count_1 == 16) {
        		this.u_count_1 = 0;
        	}
    	}
    	return this.uniquifier;
    }
}
