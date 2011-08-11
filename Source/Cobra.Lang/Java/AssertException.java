/* 
 * java code for cobra assert exception ( placeholder currently)
 * Initially using java assertion till this is working
 * 
 * Exceptions for Assertions, contracts, etc.
 */

package cobra.lang;

import java.util.*;


class AssertException extends RuntimeException //implements IHasSourceSite, HasAppendNonPropertyKeyValues
//		has DetailedStackTrace(false)
{
    protected Object _this;
    protected Object _info;   /* dynamic */
    protected Object /*SourceSite*/ _sourceSite;
    protected java.util.List<Object> _expressions;
    
    String nl = System.getProperties().getProperty("line.separator");
    
    public AssertException(/*SourceSite*/ Object sourceSite, java.util.List<Object> expressions /*dynamic */, 
              Object thiss, Object info /*dynamic? */)
    {
        this(sourceSite, expressions, thiss, info, null);
    }

    public AssertException(/*SourceSite*/ Object sourceSite, java.util.List<Object> expressions /*dynamic */, 
              Object thiss, Object info /*dynamic?*/, Exception innerExc)
    {
        super("assert", innerExc);
        _sourceSite = sourceSite;
        _expressions = expressions;
        _this = thiss;
        _info = info;
    }
    
    // Property this
    public Object getThis() { return _this; }
	
    // Property info
    public Object getInfo() { return _info; }
		
    // Property sourceSite
    public Object /*SourceSite*/ getSourceSite() { return _sourceSite; }
	
    // Property expressions
    public List<Object> getExpressions() { return _expressions; }

    @Override
    public String getMessage()  {
	StringBuilder sb = new StringBuilder(nl);
	sb.append(String.format("sourceSite = %s%s", this._sourceSite, nl));
	sb.append(String.format("info       = %s%s", this.makeString(this._info), nl));
	sb.append(String.format("this       = %s%s", this.makeString(_this), nl));
	int indentLevel = 1;
        int i = 1;
        List<Object> exprs = _expressions;
        while (i < exprs.size()) {
            Object item = exprs.get(i);
            if (item.equals(+1)) {
                indentLevel++;
                i += 1;
            }
            else if (item.equals(-1)) {
                indentLevel--;
                i += 1;
            }
            else {
                String source = (String) item;
                Object value = exprs.get(i+1);
                String valueString = "";
                valueString = (value instanceof CobraDirectString) ? 
                      ((CobraDirectString)value).string  : this.makeString(value);
                sb.append( this.replicateString(" ", indentLevel*4) );
                sb.append( String.format("%s = %s%s", source, valueString, nl));
                i += 2;
            }
        }
        return sb.toString();
    }
	
    String replicateString(String s, int count)   {
	StringBuilder sb = new StringBuilder(s);
        while (--count > 0) sb.append(s);
        return sb.toString();
    }
    
    String makeString(Object obj /*dynamic? */)   {
	String s;
	try {
	    s = CobraCore.getTechStringMaker().makeString(obj);
	} 
	catch (Exception e) {
	    s = "CobraCore.techStringMaker.makeString exception: " + e.getMessage();
	}
	return s;
    }

    /*
       def appendNonPropertyKeyValues(target as HasAppendKeyValue )
			# Invoked by the Cobra Exception Report and CobraMain-ObjectExplorer-WinForms
			# By adding the expression breakdown as entries in the view,
			# object values will be clickable which will lead to their own detailed view.
			indentLevel = 0
			target.appendKeyValue('expression breakdown:', Html(''))
			i, exprs = 1, _expressions
			while i < exprs.count
				item = exprs[i]
				if item == +1
					indentLevel += 1
					i += 1
				else if item == -1
					indentLevel -= 1
					i += 1
				else
					source = item to String
					value = exprs[i+1]
					target.appendKeyValue(String(c' ', indentLevel*4)+source, value)
					i += 2

	def populateTreeWithExpressions(tree as ITreeBuilder)
		# Invoked by the Object Explorer, but any tool could use this by implementing ITreeBuilder.
		# By adding the expression breakdown as entries in the view,
		# object values will be clickable which will lead to their own detailed view.
		i, exprs = 1, _expressions
			while i < exprs.count
				item = exprs[i]
				if item == +1
					tree.indent
					i += 1
				else if item == -1
					tree.outdent
					i += 1
				else
					source = item to String
					value = exprs[i+1]
					tree.appendKeyValue(source, value)
					i += 2

*/

}

class InvariantException extends AssertException 
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

class RequireException extends AssertException
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


class EnsureException extends AssertException
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

class NonNilCastException extends AssertException
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
