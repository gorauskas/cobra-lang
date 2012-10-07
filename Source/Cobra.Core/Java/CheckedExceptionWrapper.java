/* 
 * A Runtime Exception wrapping a Checked Exception.
 * Compiler generates internally in 'throws' codegen and unwrapped in catch clauses codegen
 * See JavaGenerator.cobra ThrowStmt.writeJavaDef and tryStmt.writeCatchBlocksJava
 * 
 * Basically a Marker class for RuntimeException.
 */

package cobra.core;


public class CheckedExceptionWrapper extends RuntimeException {
    
    public CheckedExceptionWrapper(String msg, Exception cause) {
        super(msg, cause);
    }

    public Exception getCause() {
        return (Exception) super.getCause();
    }
}


