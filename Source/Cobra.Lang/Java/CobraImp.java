/*
 * CobraImp.java
 * Native java support code RTL
 *
 *  Currently only stubbed, minimal native code impl.
 * see also Cobra.Lang/NativeExtern.cobra
 */
package cobra.lang;

import java.lang.*;
import java.util.*;

 
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
    //static public StringMaker _printStringMaker = new StringMaker();
    static public SimpleStringMaker _printStringMaker = new SimpleStringMaker();
    static public SimpleStringMaker _techStringMaker = new SimpleStringMaker();
    
    static {
	//  _printToStack = new Stack<TextWriter>();
	//  PushPrintTo(Console.Out);
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
    
    
    
    
    /* IsTrue mappings */
	static public boolean isTrue(char c) {return c!='\0';}

	static public boolean isTrue(Character c) {return c!=null && c.charValue()!='\0';}

	static public boolean isTrue(int i) {return i!=0;	}

	static public boolean isTrue(Integer i) {return i!=null && i.intValue() !=0;}

	static public boolean isTrue(long i) {return i!=0;}

	static public boolean isTrue(Long i) {return i!=null && i.longValue()!=0;}
	static public boolean isTrue(Boolean b) {return b!=null && b.booleanValue();}

	/*  
           static public boolean IsTrue(decimal d) {return d!=0;}
           static public boolean IsTrue(decimal? d) {return d!=null && d.Value!=0;	}
        */   
	static public boolean isTrue(float f) {return f!=0;}

	static public boolean isTrue(Float f) {return f!=null && f.floatValue()!=0.0;}

	static public boolean isTrue(double d) {return d!=0; }

	static public boolean isTrue(Double d) {return d!=null && d.doubleValue() !=0;}

	static public boolean isTrue(String s) {return s!=null; }

	static public boolean isTrue(java.util.Collection c) {return c!=null; }

	static public boolean isTrue(Object x) {  //rely on unboxing??
		if (x==null)
			return false;
		if (x instanceof Boolean)
			return (boolean)(Boolean)x;
		if (x.equals(0))
			return false;
		if (x instanceof Character)
			return (Character)x != '\0';
		//if (x instanceof decimal)
		//	return (decimal)x!=0;  // can't believe x.Equals(0) above doesn't work for decimal. *sigh*
		if (x instanceof Double)
			return (Double)x!=0;
		if (x instanceof Float)
			return (Float)x!=0;
		return true;
	}

    
}

