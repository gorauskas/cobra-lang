/*
 * Start of a Delegate baseclass for Cobra implemented for Java
 * The cobra compiler java backend will be reponsible for mapping the construction, initialisation and 
 *  invocation of a Delegate to the API below
 *  construct sig delName(param as paramType0,...) as retType   -> d = new Delegate(retType, paramType0, ...)
 *                    (  or no args and void returnType           -> d = new Delegate()  )
 *  assign    myDelegate as delName = ref inst.method             -> d.init(inst, "method")
 *  call      myDelegate(arg0,...)                                -> d.invoke(arg0,...)  
 *            (or no args:  myDelegate()                          -> d.invoke()  )
 *
 * Doesnt support Generics.
 * This Implementation genericised on ReturnType
 */

package cobra.lang;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class Delegate<T> {
    Class<T> returnType;  //  method returnType
    Class<?>[] paramTypes;  // Types of the method parameters 
    
    Class targetClass;
    Object target;
    String methodName;
    Method method;
    
    protected List<Delegate<T>> multiCastList;
    
    /*
     * A Delegate is a Type describing a method signature (returnType and ParamTypes) that can hold a
     * method and target to invoke it on (instance or static). 
     * Start with recording the signature - return Type and parameter Types
     */
    public Delegate(Class<T> returnType, Class<?>... paramTypes) {
    //public Delegate(Class... paramTypes) {
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }

    /*
     * Convenience for a Delegate with no params, no return type.
     */
    static public Delegate allVoid() {
        return new Delegate<Void>(void.class);
    }
    
    /*
     * Initialise to a instance method on a particular target object
     * Repeated calls will overwrite any previous settings.
     */
    public void init(Object target, String methodName){
        this.target = target;
        this.targetClass = target.getClass();
        this.methodName = methodName;
        matchMethod();
    }

    /*
     * Initialise to a static method (on a target class)
     * Repeated calls will overwrite any previous settings.
     */
    public void init(Class targetClass, String methodName){
        this.target = null;
        this.targetClass = targetClass;
        this.methodName = methodName;
        matchMethod();
    }
    
    
    /*
     * Ensure that there is a match for the given method name, return type and paramtypes on the target given.
     * Record the Method and make accessible for later invocation
     */
    void matchMethod() {
        NoSuchMethodException e0 = null;
        Class<?> mclass = this.targetClass;
        for(;;) {
            try {
                this.method = mclass.getDeclaredMethod(this.methodName, this.paramTypes);
                this.method.setAccessible(true);
                this.targetClass = mclass;              
                break;
            } catch(NoSuchMethodException e) {
                if (mclass == Object.class) {
                    throw new RuntimeException(e0!=null ? e0 : e);
                } else {
                    if (e0 == null) e0 = e;
                    mclass = mclass.getSuperclass();
                }
            }
        }
        Class methodType =  this.method.getReturnType();
        if ( ! isCompatibleType(this.returnType, methodType)) {
             String msg = "NoSuchMethodException "+this.methodName + ":" ;
             msg += "method returnType " + methodType.getName() + " not compatible with Delegate returnType " + this.returnType.getClass().getName();
             throw new RuntimeException(new NoSuchMethodException(msg));
        }
    }
    
    /** Maps primitives to their corresponding wrappers and vice versa. */
    private static final Map<Class,Class> PRIMITIVES_MAP = new HashMap<Class,Class>();
  
    static {
        PRIMITIVES_MAP.put(boolean.class, Boolean.class);
        PRIMITIVES_MAP.put(byte.class, Byte.class);
        PRIMITIVES_MAP.put(char.class, Character.class);
        PRIMITIVES_MAP.put(double.class, Double.class);
        PRIMITIVES_MAP.put(float.class, Float.class);
        PRIMITIVES_MAP.put(int.class, Integer.class);
        PRIMITIVES_MAP.put(long.class, Long.class);
        PRIMITIVES_MAP.put(short.class, Short.class);
        PRIMITIVES_MAP.put(void.class, Void.class);
        PRIMITIVES_MAP.put(Boolean.class, boolean.class);
        PRIMITIVES_MAP.put(Byte.class, byte.class);
        PRIMITIVES_MAP.put(Character.class, char.class);
        PRIMITIVES_MAP.put(Double.class, double.class);
        PRIMITIVES_MAP.put(Float.class, float.class);
        PRIMITIVES_MAP.put(Integer.class, int.class);
        PRIMITIVES_MAP.put(Long.class, long.class);
        PRIMITIVES_MAP.put(Short.class, short.class);    
        PRIMITIVES_MAP.put(Void.class, void.class);    
    }
    
    //private boolean isCompatibleType(Class<?> c1, Class<?> c2) {
    private boolean isCompatibleType(Class<T> c1, Class<?> c2) {
        return c1 == c2 || 
              c1 == PRIMITIVES_MAP.get(c2) || 
              c1.isAssignableFrom(c2) || 
              c2.isAssignableFrom(c1);
    }
    
    
     /**
     * Executes the method synchronously on the calling thread and returns the 
     * result value.
     * Any checked exceptions are caught and wrapped in a RuntimeException.
     * MultCast Delegates are all run and the result of the last returned. 
     * 
     * @param arguments Values to pass to the method.
     * @return result of calling the method.
     */
    @SuppressWarnings("unchecked")  // doesnt like the return cast
    public T invoke(Object... arguments) {
        assert !(this.method == null && this.multiCastList == null) : "Delegate.invoke: both method and multicastList null";
        Object result = null;
        if (this.method != null) {
            try {            
                result = this.method.invoke(this.target, arguments);
            } catch(IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch(InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        }
        if ( this.multiCastList != null && ! this.multiCastList.isEmpty()) {
            for (Delegate d : this.multiCastList) {
                result = d.invoke(arguments);
            }
        }
        return (T) result;      
    }

    /*
     * Add an additional Delegate to make a multicast invocation List on a single Delegate.
     * The original Delegate is not inserted in the multicast List
     */
    public void add(Object target, String methodName){
        //Delegate d = new Delegate(this.returnType, this.paramTypes);
        Delegate<T> d = new Delegate<T>(this.returnType, this.paramTypes);
        if (target instanceof Class)
            d.init((Class)target, methodName);
        else
            d.init(target, methodName);

        if (this.multiCastList == null) {
            this.multiCastList = new ArrayList<Delegate<T>>();
        }
        this.multiCastList.add(d);
    }
    
    /*
     * Remove an entry from a multicast invocation List.
     */
    public void remove(Object target, String methodName){
        if (this.multiCastList == null || this.multiCastList.isEmpty()) {
            return;
        }
        Delegate found = null;
        for (Delegate d : this.multiCastList) {
            if (d.target == target && d.methodName == methodName) {
                found = d;
                break;
            }
        }
        if (found != null) {
            this.multiCastList.remove(found);
        }
    }

    public void clear(){
        this.target = null;
        this.targetClass = null;
        this.methodName = null;
        this.multiCastList = null;
    }
}    


class TestDelegate {

    public static void main(String... args) { 
        testSimple();
        testDiffInstances();
        testMulticast();
        
        Assert.end();
    }
    
    static void testSimple() {
        String s = "Testing Java Delegate!";        
        Delegate d = new Delegate<Void>(Void.class, String.class);
        d.init(System.out, "println"); // d.set(System.out.println)
        d.invoke(s);
        
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        d.init(ps, "print");  // d.set(ps.print)
        d.invoke("xx");
        //System.out.println("["+os.toString()+"]");
        Assert.equals(os.toString(), "xx");
         
        d = Delegate.allVoid(); // no args, no return
        //d.init(System.out, "println"); // d.set(System.out.println)
        //d.invoke();
        os = new ByteArrayOutputStream();
        ps = new PrintStream(os);
        d.init(ps, "println"); // d.set(System.out.println)
        d.invoke();
        Assert.equals(os.toString(), System.getProperties().getProperty("line.separator"));
        
    }
    
    static class X {
        public String getS1() { return "S1";}
        public String getS1x(int n) { return "S1"+n; }
    }

    static class Y {
        public String aStr() { return "Str";}
        public String nStr(int i) { return "" + i + "Str"; }
    }

    static void testDiffInstances() {
        Delegate<String> d = new Delegate<String>(String.class); // no arg
        X x = new X();
        Y y = new Y();
        d.init(x, "getS1"); // d.set(x.getS1)
        String s = d.invoke();
        Assert.equals(s, "S1"); 
        //System.out.println("[" + s + "]");

        d.init(y, "aStr");  // d.set(y.aStr)
        s = (String) d.invoke();
        Assert.equals(s, "Str");
    
        Delegate<String> d1 = new Delegate<String>(String.class, int.class);
        d1.init(x, "getS1x"); // d.set(x.getS1x)
        String s1 = d1.invoke(99);
        Assert.equals(s1, "S199"); 
    
        d1.init(y, "nStr"); // d.set(y.nStr)
        s1 = d1.invoke(1234);
        Assert.equals(s1, "1234Str"); 
    }

    static void testMulticast() {
        Delegate<String> d0 = new Delegate<String>(String.class); // no args
        X x = new X();
        Y y = new Y();
        d0.add(x, "getS1"); // d.add(x.getS1)
        String s = d0.invoke();
        Assert.nonNull(s, "s"); 
        Assert.equals(s, "S1"); 
    
        d0.add(y, "aStr"); // d.set(y.nStr)
        String s1 = d0.invoke();
        Assert.equals(s1, "Str"); 
        

        String nl = System.getProperties().getProperty("line.separator");

        Delegate<Void> d = new Delegate<Void>(void.class, String.class);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        d.add(ps, "println"); // d.set(ps.println)
        d.invoke("11");
        Assert.equals(os.toString(), "11"+nl);
        
        d.clear();
        os.reset();
        d.add(ps, "println"); // d.set(ps.println)
        d.add(ps, "print"); // d.set(ps.print)
        d.invoke("22");
        Assert.equals(os.toString(), "22"+nl+"22");
        //System.out.println(nl+os.toString());

        os = new ByteArrayOutputStream();
        ps = new PrintStream(os);
        d.clear();
        d.add(ps, "print"); // d.set(ps.print)
        d.add(ps, "print"); // d.set(ps.print)
        d.add(ps, "print"); // d.set(ps.print)
        d.invoke("33.");
        Assert.equals(os.toString(), "33.33.33.");
        
        d.add(ps, "println"); // d.set(ps.println)
        os.reset();
        d.invoke("33.");
        Assert.equals(os.toString(), "33.33.33.33."+nl);
    }

}

class Assert {
   static boolean assertsEnabled = false;
   static {
        assert assertsEnabled = true;  // Intentional side-effect!!!
       // Now assertsEnabled is set to the correct value, true if asserts enabled, false otherwise
   }

    
    static void equals(Object s0, Object s1) {
        assert s0.equals(s1) : mkAssertMsg(s0, s1);
        if (assertsEnabled)  System.out.print(".");
    }
    
    static String mkAssertMsg(Object s0, Object s1) {
        return String.format("\"%s\".equals(\"%s\")\n   arg0=[%s]\n   arg1=[%s]", s0, s1, s0, s1);
    }
    
    static void nonNull(Object o, String s0 ) {
        assert o != null : String.format("\"%s\" not Null", s0);
        if (assertsEnabled) System.out.print("1");
    }
        
    static void end() { if (assertsEnabled) System.out.println(); }
}
