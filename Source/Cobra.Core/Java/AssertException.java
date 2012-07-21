/* 
 * java code for cobra assert exception 
 *  ( Root class of Cobra Assertions and contracts exceptions)
 * 
 * Exceptions for Assertions, contracts, etc.
 */

package cobra.core;

import java.util.*;


public class AssertException extends RuntimeException //implements IHasSourceSite, HasAppendNonPropertyKeyValues
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
    
    public AssertException(/*SourceSite*/ Object sourceSite, Object[] expressionsArr /*dynamic */, 
              Object thiss, Object info /*dynamic? */)
    {
        this(sourceSite, expressionsArr, thiss, info, null);
    }

    public AssertException(/*SourceSite*/ Object sourceSite, Object[] expressionsArr /*dynamic */, 
              Object thiss, Object info /*dynamic?*/, Exception innerExc)
    {
        super("assert", innerExc);
        _sourceSite = sourceSite;
        _expressions = Arrays.asList(expressionsArr);
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
