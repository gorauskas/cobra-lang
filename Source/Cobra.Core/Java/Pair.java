/* 
 *  Pair.java
 *
 *  Provides simple pair class hierarchy to satisfy RT requirements for compiler
 *  Rough translation based on cobra implementation for .Net
 */ 

package cobra.core;
import java.util.*;

public class Pair<TA extends Comparable<Object>, TB extends Comparable<Object>>  extends BasePair 
implements Iterable, Comparator<Pair<TA, TB>> {	

    final TA a;
    final TB b;

/*	test
		p = Pair<of int, int>(1, 2)
		assert p.a == 1 and p.b == 2
		assert p[0] == 1 and p[1] == 2
		assert p.toString == 'Pair(1, 2)'
		expect IndexOutOfRangeException, assert p[2] == 3
*/
    public Pair(TA a, TB b) {
        super();
        this.a = a;
        this.b = b;
    } 
	
    public Object getItem(int i) {
        if (i == 0)
            return a;
	if (i == 1)
            return b;
	throw new IndexOutOfBoundsException("Index " + i + " is out of range for a pair (0-1).");
    }

    @Override
    public String toString() {
        String sa = CobraCore.toTechString(this.a);
        String sb = CobraCore.toTechString(this.b);
        return String.format("%s (%s, %s)", this.getClass().getName(), sa, sb);
    }

    //Comparator
    public int compare(Pair<TA, TB> t, Pair<TA, TB> p) {
        /* test
		assert Pair<of String, int>('a', 3) < Pair<of String, int>('b', 4)
		assert Pair<of int, int>(2, 3) > Pair<of int, int>(2, 2)
		assert Pair<of String?, String?>(nil, 'a') < Pair<of String?, String?>(nil, 'b')
		assert Pair<of int?, int?>(nil, nil) < Pair<of int?, int?>(1, 1)
         */ 
        int cmp = _compare(t.a, p.a);
	if (cmp == 0)
            cmp = _compare(t.b, p.b);
	return cmp;
    }
    
    public boolean equals(Pair<TA, TB> p) {
	/*test
		assert Pair<String>('a', 'b').equals(Pair<String>('a', 'b'))
		assert Pair<String>('a', 'b').equals(Pair<String>('a', 'c'))
         */ 
	return p.a == this.a && p.b == this.b;
    }
    
    //Comparable
    public int compareTo(Pair<TA, TB> obj) {
        if (obj == null) return 1;
        if (obj == this) return 0;
        if (obj.getClass() == this.getClass() ) {
            int comp_a = this.a.compareTo(obj.a);
            if (comp_a ==0 )
                return this.b.compareTo(obj.b);
            return comp_a;
        }
        return -1;
    }

    @Override
    @SuppressWarnings("unchecked") 
    public boolean equals(Object obj) {
        /*test
            assert Pair<String, String>('a', 'b') == Pair<String, String>('a', 'b')
	    assert Pair<String, String>('a', 'b') <> Pair<String, String>('a', 'c')
         */
        if (obj == null)  return false;
        if (obj == this)  return true;
        if (obj.getClass() == this.getClass() ) {
            Pair<TA,TB> p = (Pair<TA,TB>) obj;
            return p.a == this.a && p.b == this.b;
        }
        return false;
    }
    
    
    @Override
    public int hashCode() {
	/*test
		d = {
			Pair<of int, String>(1, 'one'): 'one',
			Pair<of int, int>(2, 2): 'two',
			Pair<of dynamic, dynamic>(nil, nil): 'nil',
		}
		assert d[Pair<of int, String>(1, 'one')] == 'one'
		assert d[Pair<of int, int>(2, 2)] == 'two'
		assert d[Pair<of dynamic, dynamic>(nil, nil)] == 'nil'
         */
	// same as CobraCore.HashCodeUtils.combine( (this.a == null) ? 0  : this.a.hashCode(),
        //                          (this.b == null) ? 0  : this.b.hashCode() );
        int ha = ((this.a == null) ? 0  : this.a.hashCode());
        int hb = ((this.b == null) ? 0  : this.b.hashCode());
        int hc = 37;
        hc = hc * 23 + ha;
        hc = hc * 23 + hb;
        return hc;
    }
    
    public Iterator<?> iterator() {
	/*test
		assert (for s in Pair<of String, String>('a', 'b') get s) == ['a', 'b']
         */ 
        return new PairIterator();
      }
    
    class PairIterator implements Iterator {
        private int ofst =0;

        public boolean hasNext() { 
            return (this.ofst < 2); 
        }

        public Object next() {
            Object ret;
            switch (this.ofst) {
                case 0: ret= a; break;
                case 1: ret = b; break;
                default:
                    throw new NoSuchElementException("Pair: no element after .a and .b") ;
            }
            this.ofst++;
            return ret;
            
        }
        
        public void remove() {
            throw new UnsupportedOperationException("Pair: no remove operation");
        }
        
    }        
    
}
