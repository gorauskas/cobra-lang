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

public class CobraCore {
    
    public static Boolean _willCheckInvariant = true;
    public static Boolean _willCheckRequire = true;
    public static Boolean _willCheckEnsure = true;
    public static Boolean _willCheckAssert = true;
    public static Boolean _willCheckNil = true;

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

