/*
 * CobraDirectString Used internally for assert, require and ensure to encode strings 
 *  that should not be passed to CobraCore.toTechString.
 */
package cobra.core;
 
public class CobraDirectString {
    public String string;

    // ctor
    public CobraDirectString(String s) { this.string = s;   }

    @Override
    public String toString() {	return string; }
}
