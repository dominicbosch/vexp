package nano.debugger;

import java.util.Enumeration;
import java.util.Hashtable;

public class Debg {
	public static final boolean IS_ON = true;
	public static final boolean DETAILED_ON = false;
	public static final boolean GENERATE_DATA = false;
	
	public static void print(int indent, String msg){
		if(IS_ON) System.out.println(shiftString(indent + 2, "[" + getSource() + "] " + msg));
	}
	
	private static  String shiftString(int indent, String msg){
		return String.format("%" + (indent + 2) + "s", "") + msg;
	}
	
	public static void print(int indent, String msg, byte[] b){
		String bstr = Byte.toString(b[0]);
		int i, avg = 0;
		byte min = Byte.MAX_VALUE, max = Byte.MIN_VALUE;
		if(IS_ON) {
			for(i = 1; i < b.length; i++){
				bstr += "|" + b[i];
				if(b[i] < min) min = b[i];
				if(b[i] > max) max = b[i];
				avg += b[i];
			}
			avg = avg/(i-1);
			System.out.println(String.format("%" + (indent + 2) + "s", "")
					+ "[" + getSource() + "] " + msg + " first=" + b[0] + ", min=" 
					+ min + ", max=" + max + ", avg=" + avg + "\n" + String.format("%8s", "") + bstr);
		}
	}
	
	public static void err(int indent, String msg){
		if(IS_ON) System.err.println(String.format("%" + (indent + 2) + "s", "") 
				+ "[" + getSource() + "] " + msg);
	}
	
	public static void explainParserError(String cmd, Hashtable cmds){
		String msg;
		StackTraceElement[] stack = new Exception().getStackTrace();
		String caller = stack[2].getClassName();
		String[] splittedName = caller.split("\\.");
		msg = "Parser in " + splittedName[splittedName.length - 1] + 
			" couldn't find the command: " + cmd + ", it knows only: ";
		for (Enumeration e = cmds.keys(); e.hasMoreElements();)
		       msg += e.nextElement() + ", ";
		System.err.println(shiftString(2, msg));
	}
	
	public static String getSource(){
		String name;
		int line;
		StackTraceElement[] stack = new Exception().getStackTrace();
		String caller = stack[1].getClassName();

		// in case getSource is called directly and not from within this class
		if(caller.equals(Debg.class.getName())) {
			name = stack[2].getClassName();
			line = stack[2].getLineNumber();
		} else {
			name = stack[1].getClassName();
			line = stack[1].getLineNumber();	
		}
		 
		if (DETAILED_ON) return name + ":" + line;
		else {
			String[] splittedName = name.split("\\.");
			return splittedName[splittedName.length - 1]+ ":" + line;
		}
	}
}
