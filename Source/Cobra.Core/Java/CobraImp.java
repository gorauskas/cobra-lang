/*
 * CobraImp.java
 * Native java support code RTL
 *
 *  Currently only stubbed, minimal native code implementation.
 */
package cobra.core;

import java.lang.*;
import java.util.*;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.IOException;

 
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
            if (s == null) return "nil";
            return s;
        }
        public String makeString(int i) {
            return new Integer(i).toString();
        }
        
        public String makeString(char i) {
            return new Character(i).toString();
        }
        
        public String makeString(Object... args) {
            StringBuilder sb = new StringBuilder();
            for (Object arg : args) {
                if (arg == null) 
                    sb.append("nil");
                else
                    sb.append(arg.toString());
            }
            return sb.toString();
        }
    }

    //static public StringMaker _printStringMaker = new StringMaker();
    static public SimpleStringMaker _printStringMaker = new SimpleStringMaker();
    static public SimpleStringMaker _techStringMaker  = new SimpleStringMaker();

    //static private Deque<PrintStream> _printToStack; 
    // above is Stack<PrintStream> TODO: support as PrintWriter
    static private Deque<Writer> _printToStack; 
    static private PrintWriter outPW;
    static private boolean _prtErr = false;
    static {
        _printToStack = new ArrayDeque<Writer>(); 
        outPW = new PrintWriter(System.out, true);  
        pushPrintTo(outPW);
        //  _printStringMaker = new PrintStringMaker();
        //  _techStringMaker = new TechStringMaker();
        //  PromoteNumerics = NumericTypeInfo.PromoteNumerics;
        }
    
    static public Writer getPrintDestination() {
        return _printToStack.peek();
    }

    // Convenience for System.out
    static public void pushPrintTo(PrintStream ps) {
        Writer wr = new PrintWriter(ps, true);
        pushPrintTo(wr);      
    }
    
    static public void pushPrintTo(Writer wr) {
	_printToStack.push(wr);
    }

    static public void popPrintTo() {
	_printToStack.pop();
    }

    static public void reset() {
	_printToStack.clear();
	pushPrintTo(outPW);
    }
    
    static public void reset(Writer printDestination) {
	_printToStack.clear();
	pushPrintTo(printDestination);
    }
    
    static public void printLine() {
        printLine("");
        //_printToStack.peek().println();
        //System.out.println();
    }
        
    // Convenience for System.out
    static public void printLine(PrintStream ps, String s) {
        Writer wr = new PrintWriter(ps, true);
        printLine(wr, s);
    }
    
    static public void printLine(String s) {
        printLine(_printToStack.peek(), s);
    }
    static public void printLine(Writer wr, String s) {
        // We do this manually rather than using a PrintWriter so we can support use of a StringWriter
        // which (of course) doesnt support the useful things like print and println...
        try {
            wr.write(s);
            wr.write(System.lineSeparator());
            wr.flush();
        }
        catch(IOException ioe) {
            _prtErr = true;
        }
        //_printToStack.peek().println(s);
        //System.out.println(s);
    }

    static public void printStop() {
    }

    // Convenience for System.out
    static public void printStop(PrintStream ps, String s) {
        Writer wr = new PrintWriter(ps, true);
        printStop(wr, s);
    }
    
    static public void printStop(String s) {
        printStop(_printToStack.peek(), s);
    }
    
    static public void printStop(Writer wr, String s) {
        try {
            wr.write(s);
        }
        catch(IOException ioe) {
            _prtErr = true;
        }
            
        //_printToStack.peek().print(s);
        //System.out.print(s);
    }
    /*
     * Check if a Print error (IOException on print) has ocurred.
     */
    static public boolean chkPrtError() {
        return _prtErr;
    }

    /*
     * Clear any print error setting, return the value that was set.
     */
    static public boolean clearPrtError() {
        boolean r = _prtErr;
        _prtErr = false;
        return r;
    }



    static public String makeString(String[] args) {
        return _printStringMaker.makeString(args);
    }
          
    static public String makeString(String s) {
        return s;
    }
    
    /*
     * Convenience ctors for literal Lists, Sets and Dicts
     */
    @SuppressWarnings("unchecked")
    static public <T> List<T> makeList(T... args) {
	//return new ArrayList<T>(Arrays.asList(args));
	return Arrays.asList(args);
    }
    
    /*
    @SuppressWarnings("unchecked")
    static public <T> List<T> makeList(T... args) {
	List<T> l = new ArrayList<T>();
        for (T arg : args) {
            l.add(arg);
        }
        return l;
    }
    */
    
    @SuppressWarnings("unchecked")
    static public <T> Set<T> makeSet(T... args) {
        return new HashSet<T>(Arrays.asList(args));
    }
    
    @SuppressWarnings("unchecked") 
    static public <K,V> Map<K,V> makeDict( Object... args) {
	Map<K,V>  d = new HashMap<K,V>();
	for (int i=0; i<args.length; i+=2)
            d.put((K)args[i], (V)args[i+1]); //[unchecked] warning here
	return d;
    }
    
    
    
    static public <T> T checkNotNull(T value, String msg)
    {
        // used for generic checking for non null arg, throws NPE if fails
        // msg expected to contain info and name of item being checked.
        if (/*cobra.core.CobraCore._willCheckNil && */ value == null) {
           throw new NullPointerException(msg);
        }
        return value;
    }

    static public <T> T checkNonNil(Object obj, String sourceCode, T value, /* SourceSite */ String fileName, int lineNum, String className, String memberName, Object thiss) 
    {
        // used for "x to !" and "someNilable to SomethingNotNilable"
        if (value == null) {
            List<Object> l = new ArrayList<Object>();
            l.add(0);
            l.add(sourceCode);
            l.add(value);
            throw new NonNilCastException( new SourceSite(fileName, lineNum, className, memberName, thiss),
                      l, obj, null  );
        }
        return value;
    }
    
 /*   
    static public String toTechString(boolean x) 
    { 
        return x ? "true" : "false";
    }
    
    static public String toTechString(byte x) 
    { 
        return "" + x;
    }
    static public String toTechString(int x) 
    { 
        return "" + x;
    }
    static public String toTechString(short x) 
    { 
        return "" + x;
    }
    static public String toTechString(long x) 
    { 
        return "" + x;
    }
   */ 
    static public String toTechString(Object x) 
    {
        if (x == null)
            return "nil";
        if (x instanceof Boolean)
            return String.format("%b", (Boolean)x ? "true" : "false");
        if (x instanceof String) 
        {  // TODO: handle double backslash
            String s = (String)x;
            s = s.replace("\n", "\\n");
            s = s.replace("\r", "\\r");
            s = s.replace("\t", "\\t");
            s = "'" + s + "'";  // TODO: could be more sophisticated with respect to ' and "
            return s;
        }
        if (x instanceof java.util.List) 
        {
            // TODO: should not go into infinite loop for circular references
            StringBuilder sb = new StringBuilder();
            sb.append(x.getClass().getName() );
            sb.append("[");          
            String sep = "";
            for (Object item : (java.util.List)x) 
            {
                sb.append(sep);
                sb.append( toTechString(item) );
                sep = ", ";
            }
            sb.append("]");
            return sb.toString();
        }
        if (x instanceof java.util.Map) 
        {
            // TODO: should not go into infinite loop for circular references
            Map idict = (java.util.Map)x;
            StringBuilder sb = new StringBuilder();
            sb.append( x.getClass().getName()); 
            sb.append("{");
            String sep = "";
            for (Object key : idict.keySet()) {
                 sb.append( String.format("$1s%2$s: %3$s", sep, toTechString(key), toTechString(idict.get(key))) );
                 sep = ", ";
            }
            sb.append("}");
            return sb.toString();
        }
        if (x instanceof java.lang.Iterable)
        {
            // TODO: should not go into infinite loop for circular references
            java.lang.Iterable iter = (java.lang.Iterable)x;
            StringBuilder sb = new StringBuilder();
            sb.append(x.getClass().getName());
            sb.append("[");
            String sep = "";
            for (Object item : iter) 
            {
                sb.append(String.format("%1$s%2$s", sep, toTechString(item)));
                sep = ", ";
            }
            sb.append("]");
            return sb.toString();
        }
        if (x instanceof java.lang.Enum) 
        {
            return x.getClass().getName() + "." + x.toString() + " enum";
        }

        // TODO: For StringBuilder, return StringBuilder'aoeu'
        String tts = x.toString();
        if (isInterestingClass(x.getClass())) 
        {
            String typeName = x.getClass().getName();
            if (!tts.contains(typeName) && !(x instanceof CobraDirectString))
                tts = String.format("%1$s (%2$s)", tts, typeName);
        }
        return tts;
    }

    static boolean isInterestingClass(Class t) 
    {
        if (t == java.lang.Integer.class)
            return false;
        if (t == java.lang.Character.class)
            return false;
        if (t == java.lang.Boolean.class)
           return false;
        return true;
   }
    
    
    
    /* IsTrue mappings */
    static public boolean isTrue(char c) {return c!='\0';}

    static public boolean isTrue(Character c) {return c!=null && c.charValue()!='\0';}

    static public boolean isTrue(int i) {return i!=0;   }

    static public boolean isTrue(Integer i) {return i!=null && i.intValue() !=0;}

    static public boolean isTrue(long i) {return i!=0;}

    static public boolean isTrue(Long i) {return i!=null && i.longValue()!=0;}
    static public boolean isTrue(Boolean b) {return b!=null && b.booleanValue();}

        /*  
           static public boolean IsTrue(decimal d) {return d!=0;}
           static public boolean IsTrue(decimal? d) {return d!=null && d.Value!=0;      }
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
        //      return (decimal)x!=0;  // can't believe x.Equals(0) above doesn't work for decimal. *sigh*
        if (x instanceof Double)
            return (Double)x!=0;
        if (x instanceof Float)
            return (Float)x!=0;
        return true;
    }
    
    static public boolean referenceEquals(Object o, Object o1) { return o == o1; }
    
    static public boolean is(Object a, Object b) {
	return CobraImp.referenceEquals(a, b);
    }

    static public boolean isNot(Object a, Object b) {
            return !CobraImp.referenceEquals(a, b);
    }

    static public boolean is(Enum a, Enum b) {
        if (a==null) return b==null;
        if (b==null) return false;
        //   return a.equals(b);
        return a == b;  
    }
    
    /*
     * ........ Support for inclusion testing: 'a in b' ................... 
     */

    static public boolean in(char a, String b) {
	return b.indexOf(a) != -1;
    }

    static public boolean in(String a, String b) {
	return b.indexOf(a) != -1;
    }
/*    static public boolean in(Object a, Collection b) {
	return b.contains(a);
    }

    static public boolean in(Object a, Iterable b) {
	for (Object item : b) {
            if (item.equals(a) /*equals(item, a)* /)
		return true;
	}
	return false;
    }
 */
    
    // Covers collections including enums via EnumSet
    static public <T> boolean in(T a, Collection<T> b) {
	return b.contains(a);
    }

    static public <T> boolean in(T a, Iterable<T> b) {
	for (T item : b) {
	    if (item.equals(a) /* Equals(item, a) */)
		return true;
	}
	return false;
    }

    static public <T> boolean in(T a, Map<T,?> b) {
	return b.containsKey(a);
    }

/* LATER    static public <T> bool InWithNullCheck(T a, Predicate<T> predicate) {
        if (a == null) return false;
	    return predicate(a);
    }
*/
    static private boolean _noNestedIn = false;

    static public boolean in(Object a, Object b) {
        if (_noNestedIn)
            throw new RuntimeException(String.format("_noNestedIn a=0$s, a.getClass=%1$s, b=%2$s, b.getClass=%3$s", 
                a, a==null ? "" : a.getClass().getName(), 
                b, b==null ? "" : b.getClass().getName() ) );
	_noNestedIn = true;
	try {
		if (b instanceof Collection) {   // probably the most common case, check has to come before Iterable
		    return in(a, (Collection<?>)b);
		} else if (b instanceof String) {
		    if (a instanceof String) {
			return in((String)a, (String)b);
		    } if (a instanceof Character) {
			return in( ((Character)a).charValue(), (String)b);
		    } else {
			throw new CannotInTypeException(a, "a of `a in b`", a.getClass());
		    }
		} else if (b instanceof Iterable) {
		    return in(a, (Iterable<?>)b);
		} else if (b instanceof Map) {
		    return in(a, (Map<?,?>)b);
		} else if (b instanceof Enum) {
    	            throw new RuntimeException("Cannnot test inclusion in an Enum, should be using EnumSet"); 
		} else {
		    throw new CannotInTypeException(b, "b of `a in b`", b.getClass());
		}
	} finally {
		_noNestedIn = false;
	}
    }
    
    // support for throws codegen and checked exception wrapping
    static public RuntimeException reThrow(Exception e) {
        if (e instanceof RuntimeException) return (RuntimeException)e;
        return new CheckedExceptionWrapper("Wrapping rethrown exception", e);
    }
}

