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
        
        public String makeString(char i) {
            return new Character(i).toString();
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
        //      _printToStack.Peek().WriteLine(s);
        System.out.println(s);
    }

    static public void printStop() {
    }

    static public void printStop(String s) {
        //      _printToStack.Peek().Write(s);
        System.out.print(s);
    }

    static public String makeString(String[] args) {
        return _printStringMaker.makeString(args);
    }
          
    static public String makeString(String s) {
        return s;
    }
    
    static public Object checkNonNil(Object obj, String sourceCode, Object value, /* SourceSite */ String fileName, int lineNum, String className, String memberName, Object thiss) 
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
    
    
}

