/*
 * Non genericised Delegate Impl (to be same as .Net)
 * Uses genericised version code 
 * Will need codegen support for casting invoke type
 */

package cobra.core;

import java.lang.*;
import java.io.*;
import java.util.*;


// Tmp placeholder
public class DelegateO {
    Delegate<Object> delTgt;
    Class returnType;
    
    public DelegateO(Class returnType, Class... paramTypes) {
    //public Delegate(Class... paramTypes) {
        this.delTgt = new Delegate<Object>(Object.class, paramTypes);
        this.returnType = returnType;
    }

    public void init(Object target, String methodName){
        this.delTgt.init(target, methodName);
    }

    public void init(Class targetClass, String methodName){
        this.delTgt.init(targetClass, methodName);
    }
    public Object invoke(Object... arguments) {
        return delTgt.invoke(arguments);
    }

    public void add(Object target, String methodName){
        delTgt.add(target, methodName);
    }
    
    public void remove(Object target, String methodName){
        delTgt.remove(target, methodName);
    }
    
    public void clear(){
        delTgt.clear();
    }
}

