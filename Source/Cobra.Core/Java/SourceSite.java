/*
 * Sourcesite.java
 */
 
package cobra.core;

import java.io.*;

public class SourceSite {
    /* 
     * See Cobra.Core.SourceSite.cobra
     * 
     *  A SourceSite instance captures the source location of a statement or expression, as in the
     *  filename, line number, declaring class name, method name and run-time object. The Cobra
     *  compiler generates SourceSite instances in various circumstances; for example, the `trace`
     *  statement.
     */

	protected String _fileName; 
	protected int    _lineNum;
	protected String _className;
        protected String _memberName;
        protected Object _object;

	public SourceSite(String fileName, int lineNum, String className, String memberName, Object obj ) {
            this._fileName = fileName;
            this._lineNum = lineNum;
	    this._className = className;
            this._memberName = memberName;
            this._object = obj;
        }

        @Override
        public String toString() {
            //  test
            //	    si = SourceSite('Foo.cobra', 10, 'Object', 'bar', BadToString())
            //	    assert 'Foo.cobra' in si.toString
            //	    assert 'exception on' in si.toString
            Object obj;
            try {
                obj = CobraCore.toTechString(this._object);
            }
            catch (Exception exc) {
                obj = String.format("(.toTechString or .toString exception on a %s: %s: %s)", _object.getClass().getName(), exc.getClass().getName(), exc.getMessage() );
            }
            return String.format("%s:%d in %s.%s for object %s", this._fileName, this._lineNum, this._className,this._memberName, obj);
        }

        // Property fileName
        public String getFileName() { return this._fileName; }
            
        // Property lineNum
        public int getLineNum()  { return this._lineNum; }

        // Property className
	public String getClassName()  { return this._className; }

        // Property memberName
	public String getMemberName()  { return this._memberName; }

        // Property object
	public Object getObject()  { return this._object; }

        // Property runTimeClassName
        public 	String getRunTimeClassname() { 	return this._object.getClass().getName(); }

        public String oneLiner(String sep ) {
            return oneLiner(sep, true);
        }

        public String oneLiner(String sep, boolean  willOutputDirectoryNames) {
            //test
	    //	si = SourceSite('Foo.cobra', 10, 'Object', 'bar', Object())
            //	assert si.oneLiner('; ', true) == 'at Foo.cobra:10; in Object.bar'
	    //	si = SourceSite('Foo.cobra', 10, 'Foo', 'bar', Object())
	    //	assert si.oneLiner('; ', true) == 'at Foo.cobra:10; in Foo.bar; subclass Object'
	    //body
	    StringBuilder sb = new StringBuilder();
            String fileName = this._fileName;
            if ( _fileName.length()>0 && ! willOutputDirectoryNames) 
                fileName = (new File((this._fileName)).getName());
            sb.append( String.format("at %s:%d%sin %s.%s", 
                      fileName, this._lineNum, sep, this._className, this._memberName));
            if ( ! (this._object instanceof Class)) {
                String name = this.getClass().getName();
	        if ( name != _className) 
                    sb.append( String.format("%ssubclass %s", sep, name));
            }
            return sb.toString();
        }

        public String htmlForAt() {
            return htmlForAtArgs(this._fileName, this._lineNum);
        }

        static public String htmlForAtArgs(String fileName, int lineNum ) {
            //"""
            // Returns the HTML for the row labeled "at" in the Cobra Exception Report.
            // This will be an actual link if the environment variable COBRA_EDIT_LINK is set.
            // That's what enables clicking on sources in the report to jump to their location in a text editor.
            //"""
            File f = new File(fileName);
            String baseName = f.getName();
            String dirName = f.getParent();
            if (dirName.length() != 0)
                dirName = " in " + dirName;
            String s = "line " + lineNum + "of " + baseName + dirName;
            String editLink = System.getenv("COBRA_EDIT_LINK");
            if (editLink != null && editLink.length() >0) {
                editLink = editLink.replace("FILE", fileName); //  # TODO: url encode the file name
                editLink = editLink.replace("LINE", "" + lineNum);
                s = "<a href=\""+ editLink + "\">[s]</a>";
            }
            return s;
        }
}
