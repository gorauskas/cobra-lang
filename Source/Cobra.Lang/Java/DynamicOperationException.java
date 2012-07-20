/*
*  Dynamic Operations Exceptions
*/
package cobra.core;

import java.util.*;


	//## Exceptions about dynamic

public class DynamicOperationException extends RuntimeException
{ 
   //"""
   // The base class for all dynamic operation exceptions.
   //"""

    public DynamicOperationException(String message) { this(message, null); }
    public DynamicOperationException(String message, Exception cause) {super(message, cause); }
}

class CannotEnumerateException extends DynamicOperationException 
{
    public CannotEnumerateException (String message) {this(message, null); }
    public CannotEnumerateException (String message, Exception cause) {super(message, cause); }
}		
		
class UnknownMemberException extends DynamicOperationException
{
    protected Object _obj;
    protected String _name;
    Class _type;

    public UnknownMemberException(Object obj, String name, Class type) {  this(obj, name, type, null);}
    public UnknownMemberException(Object obj, String name, Class type, Exception cause) 
    {
	super(String.format("obj=%s, name=%s, type=%s", 
                  CobraCore.toTechString(obj), CobraCore.toTechString(name), type),
              cause);
        this._obj = obj;
        this._name = name;
        this._type = type;
    }
}

/*
 * Missing, unreadable Member Exceptions
 * / 
class CannotReadPropertyException inherits UnknownMemberException

		# CC: axe init()s

		cue init(obj as Object, name as String, type as Type)
			.init(obj, name, type, nil)

		cue init(obj as Object, name as String, type as Type, innerExc as Exception?)
			base.init(obj, name, type, innerExc)


	class CannotWritePropertyException inherits UnknownMemberException

		# CC: axe init()s

		cue init(obj as Object, name as String, type as Type)
			.init(obj, name, type, nil)

		cue init(obj as Object, name as String, type as Type, innerExc as Exception?)
			base.init(obj, name, type, innerExc)


	class CannotSliceTypeException inherits UnknownMemberException

		# CC: axe init()s

		cue init(obj as Object, name as String, type as Type)
			.init(obj, name, type, nil)

		cue init(obj as Object, name as String, type as Type, innerExc as Exception?)
			base.init(obj, name, type, innerExc)


	class CannotInTypeException inherits UnknownMemberException

		# CC: axe init()s

		cue init(obj as Object, name as String, type as Type)
			.init(obj, name, type, nil)

		cue init(obj as Object, name as String, type as Type, innerExc as Exception?)
			base.init(obj, name, type, innerExc)


class CannotCompareException inherits DynamicOperationException

		var _a
		var _b

		cue init(a, b)
			.init(a, b, nil)

		cue init(a, b, innerExc as Exception?)
			base.init('a=[a], b=[b]', innerExc)
			#base.init('a=[CobraCore.toTechString(a)], a.getType=[a.getType.name], b=[CobraCore.toTechString(b)], b.getType=[b.getType.name]', innerExc)
			_a = a
			_b = b



*/
