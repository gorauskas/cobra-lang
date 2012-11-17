/* 
 *  BasePair.java
 *
 *  Abstract base class for simple pair class hierarchy to satisfy RT requirements for compiler
 * used by Pair.java et al
 */ 

package cobra.core;
import java.util.*;

public abstract class BasePair  {

    protected  int _compare(Comparable<Object> a, Comparable<Object> b) {
          /*
           * Gracefully handle comparisons that involve nil.
           * A simple `a.compareTo(b)` does not work if `a` can be `nil`.
           */ 
	if (a == null)
            if (b == null) 
                return 0;
	    else 
                return -1;
	else if (b == null)
	    return 1;
	else
	    return a.compareTo(b);
    }

    public int size() {
            return 2;
       }

}
