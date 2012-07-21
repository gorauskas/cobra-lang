/* 
 * java code for cobra NonNil Cast exception.
 * Thrown when Null check for non nullable instance fails. 
 */

package cobra.core;

import java.util.*;


public class NonNilCastException extends AssertException
{

    public NonNilCastException(/*SourceSite*/ Object sourceSite, 
              java.util.List<Object> expressions /*dynamic */, 
              Object thiss, 
              Object info /*dynamic? */) 
    {
        this(sourceSite, expressions, thiss, info, null);
    }


    public NonNilCastException(/*SourceSite*/ Object sourceSite, 
              java.util.List<Object> expressions /*dynamic */,  
              Object thiss, 
              Object info /*dynamic? */,
              Exception cause )
          {
              super(sourceSite, expressions, thiss, info, cause);
          }    

    @Override
    public String getMessage() {
        return String.format("Cast to non-nil failed.%s%s", this.nl, super.getMessage() ) ;
    }
}



/*	## Misc exceptions

	class ExpectException inherits Exception

		cue init(expectedExceptionType as Type, actualException as Exception?)
			base.init
			_expectedExceptionType = expectedExceptionType
			_actualException = actualException
	
		get expectedExceptionType from var as Type
	
		get actualException from var as Exception?

		get message as String? is override
			sb = StringBuilder()
			sb.append('Expecting exception: [_expectedExceptionType.name], but ')
			if _actualException
				sb.append('a different exception was thrown: [_actualException]')
			else
				sb.append('no exception was thrown.')
			return sb.toString


	class FallThroughException inherits Exception

		cue init
			.init(nil)
			pass

		cue init(info as Object?)
			base.init
			_info = info

		cue init(info as Object?, innerExc as Exception?)
			base.init(nil, innerExc)
			_info = info

		get message as String is override
			return 'info=[CobraCore.toTechString(_info)]'

		get info from var as Object?


	class SliceException inherits SystemException

		cue init(msg as String?)
			base.init
*/
