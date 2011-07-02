/*
 * CobraImp.java
 * Native java support code RTL
 *
 *  Currently only stubbed, minimal native code impl.
 */
package cobra.lang;

import java.lang.*;

 
public class CobraImp {
    // simple minimal StringMaker pending cobra compilation of StringMaker.cobra
    public static class SimpleStringMaker {
        
        public String makeString(String[] args) {
            StringBuilder sb = new StringBuilder();
	    for (Object arg : args)
		sb.append(arg);
            return sb.toString();
	}
        public String makeString(String s) {
            return s;
	}
        public String makeString(int i) {
            return new Integer(i).toString();
	}
        
        public String makeString(Object... args) {
            StringBuilder sb = new StringBuilder();
	    for (Object arg : args)
		sb.append(arg.toString());
            return sb.toString();
	}
    }
    //static public StringMaker _printStringMaker;
    static public SimpleStringMaker _printStringMaker = new SimpleStringMaker();
    
    static {
	//  _printToStack = new Stack<TextWriter>();
	//  PushPrintTo(Console.Out);
	//_printStringMaker = new SimpleStringMaker();
	//  _printStringMaker = new PrintStringMaker();
	//  _techStringMaker = new TechStringMaker();
	//  PromoteNumerics = NumericTypeInfo.PromoteNumerics;
	}
    
    static public void printLine() {
	//_printToStack.Peek().WriteLine();
        System.out.println();
    }

    static public void printLine(String s) {
	//	_printToStack.Peek().WriteLine(s);
        System.out.println(s);
    }

    static public void printStop() {
    }

    static public void printStop(String s) {
	//	_printToStack.Peek().Write(s);
        System.out.print(s);
    }

    static public String makeString(String[] args) {
        return _printStringMaker.makeString(args);
    }
          
    static public String makeString(String s) {
        return s;
    }

}

