package discoverylab.telebot.master.core.parser;

import java.util.StringTokenizer;

public abstract class DataParser {
	private String delim = " ";
	public abstract Object parse(String str);
	
	public StringTokenizer parseUsingTokenizer(String str){
		return new StringTokenizer(str, delim);
	};
}
