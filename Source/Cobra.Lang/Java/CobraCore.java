/*
 * CobraCore.java
 * 
 * Java code implementation of RTL support code and code
 * thats needed for simple cobra apps (in java) prior to 
 * cobra library code generated into java.
 * 
 * Eventually contents of this file will be all cobra.
 */
package cobra.lang;

import java.util.*;

//interface IHasSourceSite {
//    public SourceSite getSourceSite();
//}


public class CobraCore {
    
    public static Boolean _willCheckInvariant = true;
    public static Boolean _willCheckRequire = true;
    public static Boolean _willCheckEnsure = true;
    public static Boolean _willCheckAssert = true;
    public static Boolean _willCheckNil = true;

    //public static String getRuntimePlatform() { return "jvm"; } // prop
    public static final String runtimePlatform =  "jvm"; 
    
    // property willCheckAssert
    public static boolean getWillCheckAssert() { return _willCheckAssert; }
    public static void    setWillCheckAssert(boolean b) { _willCheckAssert = b; }
    
    public static List<String> commandLineArgs = new ArrayList<String>();

    // Compiler inserts calls to this to initialize commandLineArgs
    public static void _recordCommandLine(String[] args) {
        commandLineArgs.addAll(java.util.Arrays.asList(args));
        StackTraceElement[] stack = Thread.currentThread ().getStackTrace();
        StackTraceElement main = stack[stack.length - 1];
        String mainClass = main.getClassName();    
        commandLineArgs.add(0, mainClass);
    }
    
    public static void exit(int exitCode) {
        //""" Exits the process. """
	System.exit(exitCode);
    }
			
    public static String newLine =  System.getProperty("line.separator");
	//""" Returns the newline for the current environment/platform. """

    // Property StringMaker techStringMaker
    /*
     * 	Used by `assert` failures, `trace` statements and .toTechString methods.
     */
    static public CobraImp.SimpleStringMaker getTechStringMaker() { return CobraImp._techStringMaker; }
    static public void setTechStringMaker(CobraImp.SimpleStringMaker value) { CobraImp._techStringMaker = value; }
    
    static public String toTechString(Object x) 
    {
	return CobraImp.toTechString(x);
    }
    
    public static int noOp(/* allowNull */ Object... args) { 
        /* """
	No operation. Primarily used in Cobra's own test suite to consume a local variable to avoid undesired warnings.
	Takes any number of values and returns an undefined value of type `dynamic?` for maximum flexibility.
	""" */

	return 0;
    }
    
    /*
        def runAllTests
	"""
	Run all Cobra `test` sections in all assemblies using reflection to locate them.
	"""
	if CobraImp.showTestProgress, listener = Cobra.Lang.Test.TextWriterListener(Console.out)
	else, listener = Cobra.Lang.Test.TextWriterOnlyOnFailureListener(Console.out)
	tr = Cobra.Lang.Test.TestRunner(listener)
	tr.runAllTests
	if listener.testFailures, CobraCore.exit(1)
     */
    public static void runAllTests()
    {
        CobraImp.printLine("stub:running all tests...");
    }
    
    public static void printDebuggingTips()
    {
        CobraImp.printLine("An unhandled exception has occurred.");
        CobraImp.printLine();
        CobraImp.printLine( "Cobra debugging tips:");
        CobraImp.printLine( "    To get file name and line number information for the stack frames, use:");
        CobraImp.printLine( "        cobra -debug foo.cobra");
        CobraImp.printLine( "    To get a post-mortem, HTML-formatted report with more details about your objects:");
        CobraImp.printLine( "        cobra -debug -exception-report foo.cobra");
        CobraImp.printLine( "    For even more information, try:");
        CobraImp.printLine( "        cobra -debug -exception-report -detailed-stack-trace foo.cobra");
        CobraImp.printLine( "    Or use the abbreviations:");
        CobraImp.printLine( "        cobra -d -er -dst foo.cobra");
        // TODO: CobraImp.printLine( "    See also: http://cobra-language.com/docs/debugging");
        CobraImp.printLine();
    }
}

