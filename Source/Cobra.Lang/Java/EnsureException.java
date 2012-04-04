/* 
 * java code for cobra Ensure exception
 * thrown when a contracts postcondition fails.
 */

package cobra.lang;

import java.util.*;


public class EnsureException extends AssertException
{

    public EnsureException(/*SourceSite*/ Object sourceSite, 
              java.util.List<Object> expressions /*dynamic */, 
              Object thiss, 
              Object info /*dynamic? */) 
    {
        this(sourceSite, expressions, thiss, info, null);
    }

    public EnsureException(/*SourceSite*/ Object sourceSite, 
              java.util.List<Object> expressions /*dynamic */,  
              Object thiss, 
              Object info /*dynamic? */,
              Exception cause)
          {
              super(sourceSite, expressions, thiss, info, cause);
          }    
}

