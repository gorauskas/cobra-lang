/* 
 * java code for cobra Invariant exception 
 *  thrown when an invariant failure detected (Contracts)
 * 
 * 
 */

package cobra.lang;

import java.util.*;


public class InvariantException extends AssertException 
{
    public InvariantException(/*SourceSite*/ Object sourceSite, 
              java.util.List<Object> expressions /*dynamic */,  Object thiss, Object info /*dynamic? */) 
    {
        this(sourceSite, expressions, thiss, info, null);
    }
    
    public InvariantException(/*SourceSite*/ Object sourceSite, 
              java.util.List<Object> expressions /*dynamic */, Object thiss, Object info /*dynamic? */, 
              Exception cause)
    {
        super(sourceSite, expressions, thiss, info, cause);
    }
}
