/* 
 * java code for cobra Require exception  
 * thrown when a contract precondition test fails
 */

package cobra.lang;

import java.util.*;


public class RequireException extends AssertException
{
    protected RequireException _next;

    public RequireException(/*SourceSite*/ Object sourceSite, 
              java.util.List<Object> expressions /*dynamic */, 
              Object thiss, 
              Object info /*dynamic? */) 
    {
        this(sourceSite, expressions, thiss, info, null);
    }
    
    public RequireException(/*SourceSite*/ Object sourceSite, 
              java.util.List<Object> expressions /*dynamic */,  
              Object thiss, 
              Object info /*dynamic? */,
              Exception cause )
          {
              super(sourceSite, expressions, thiss, info, cause);
          }    
    
    //Property RequireException next
    public RequireException getNext() { return this._next;}
    public void setNext(RequireException value) { this._next = value; }
}

