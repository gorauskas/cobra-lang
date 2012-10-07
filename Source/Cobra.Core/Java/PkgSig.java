/*
 * Generate a cobra package signature file
 * compile: javac -cp . PkgSig.java
 * run:     
 *   generate sigfile for pkg in CLASSPATH 
 *       java -cp . PkgSig <pkgname>    # generates to stdout
 *       e.g java -cp . PkgSig java.lang    
 *   run generating pkgSig file:  
 *      java -cp . PkgSig <pkgname>  > <pkgname>.sig
 *      e.g java -cp . PkgSig java.lang  > java.lang.sig  
 *   generate sigfile for jarfile - relative pathnames searched for in dirs in CLASSPATH
 *       java -cp . PkgSig -j <jarfile>    # generates to stdout
 *       e.g java -cp . PkgSig -j rt.jar  > rt.jar.sig  
 *           java -cp . PkgSig -j /JavaLibs/bsh-2.0b4.jar 
 *  
 * 
 * TODO 
 *  add cmdline flag generating pkgSig file w/o redirection:
 *      java -cp . PkgSig -cobra <pkgname>
 *  Cleanup and javadoc this file.
 * Put a PkgSig version and timestamp in the pkgSigFile
 */

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.net.*;

import java.lang.reflect.*;

/**
 * This abstract class can be used to obtain a list of all classes in a classpath.
 *
 * <em>Caveat:</em> When used in environments which utilize multiple class loaders--such as
 * a J2EE Container like Tomcat--it is important to select the correct classloader
 * otherwise the classes returned, if any, will be incompatible with those declared
 * in the code employing this class lister.
 * to get a reference to your classloader within an instance method use:
 *  <code>this.getClass().getClassLoader()</code> or
 *  <code>Thread.currentThread().getContextClassLoader()</code> anywhere else
 * <p>
 * @author Kris Dover <krisdover@hotmail.com>
 * @version 0.2.0
 * @since   0.1.0
 * 
 * Downloaded from:
 * 
 * Modified to output just class contents per cobra compiler requirements
 * Mike Hopkirk Mar-2011
 */
public abstract class PkgSig { 
    
    static public class ClassComparator implements Comparator<Class> {
        public int compare(Class cl, Class cl1) {
        String n0 = cl.getName();
        String n1 = cl1.getName();
        return n0.compareTo(n1);
        }
    }
    
  /**
   * Convenience for lookup for a single package name.
   * <p>
   * @param pkg     String name of a package to look for classes for
   * @return A Set of Classes 
   *
   * @throws ClassNotFoundException if the current thread's classloader cannot load
   *                                a requested class for any reason
   */
    public static Set<Class> findClassesForPkg(String pkg)  throws ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        assert loader != null;
        Set<String> pf = new HashSet<String>();
        System.out.println("# PkgSig file for '" + pkg+ "'");
        pf.add(pkg);
        return findClasses(loader, pf, null);
  }
    
   /**
   * Searches the classpath for all classes matching a specified search criteria, 
   * returning them in a map keyed with the interfaces they implement or null if they
   * have no interfaces. The search criteria can be specified via interface, package
   * and jar name filter arguments
   * <p>
   * @param classLoader       The classloader whose classpath will be traversed
   * @param packageFilter     A Set of fully qualified package names to search for or
   *                          or null to return classes in all packages
   * @param jarFilter         A Set of jar file names to search for or null to return
   *                          classes from all jars
   * @return A Set of Classes 
   *
   * @throws ClassNotFoundException if the current thread's classloader cannot load
   *                                a requested class for any reason
   */
   @SuppressWarnings("unchecked") 
   public static Set<Class> findClasses(ClassLoader classLoader,
                                        Set<String> packageFilter,
                                        Set<String> jarFilter) throws ClassNotFoundException {
       Set<Class> allClasses = new java.util.concurrent.ConcurrentSkipListSet(new PkgSig.ClassComparator());
       Object[] classPaths = getClassPaths(classLoader);
    
       for (int h = 0; h < classPaths.length; h++) {
           Enumeration files = null;
           JarFile module = null;
           // for each classpath ...
           File classPath = new File((URL.class).isInstance(classPaths[h]) ?
                                  ((URL)classPaths[h]).getFile() : classPaths[h].toString() );
           if (classPath.isDirectory() /*&& jarFilter == null*/){   // is our classpath a directory and jar filters are not active?
               List<String> dirListing = new ArrayList();
               // get a recursive listing of this classpath
               recursivelyListDir(dirListing, classPath, new StringBuffer() );
               // an enumeration wrapping our list of files
               files = Collections.enumeration(dirListing );
           } else if(classPath.getName().endsWith(".jar") ) {    // is our classpath a jar?
               // skip any jars not list in the filter
               if (! classPath.exists())
                   continue;

               if (jarFilter != null && !jarFilter.contains(classPath.getName()))
                   continue;

               try {
                   // if our resource is a jar, instantiate a jarfile using the full path to resource
                   module = new JarFile(classPath);
               } catch (MalformedURLException mue){
                   throw new ClassNotFoundException("Bad classpath. Error: " + mue.getMessage());
               } catch (IOException io){
                   throw new ClassNotFoundException("jar file '" + classPath.getName() + 
                      "' could not be instantiated from file path. Error: " + io.getMessage());
               }
               System.out.println("# jarfile=" + module.getName());
               // get an enumeration of the files in this jar
               files = module.entries();
           }
      
           // for each file path in our directory or jar
           while (files != null && files.hasMoreElements()) {
               String fileName = files.nextElement().toString();         // get each fileName
    
               if (fileName.endsWith(".class")) {
                   processFile(fileName, classLoader, packageFilter, null, allClasses);
               }
               else if ( fileName.endsWith(".jar") ) {
                   if ( jarFilter == null || jarFilter.contains( fileName )) {
                       String pathName = classPath + File.separator + fileName;
                       processJarFile(pathName, classLoader, packageFilter, null, allClasses);
                   }
               }
           }
      
           // close the jar if it was used
           if (module != null){
               try {
                   module.close();
               } catch(IOException ioe) {
                   throw new ClassNotFoundException("The module jar file '" + classPath.getName() +
                             "' could not be closed. Error: " + ioe.getMessage());
               }
           }
       } // end classpath for loop
       return allClasses;                                            
   } // end method

    static private Object[] getClassPaths( ClassLoader classLoader) 
    {
        Object[] classPaths;
        ArrayList<Object> al;
        try {
            // get a list of all classpaths fm classloader
            classPaths = ((java.net.URLClassLoader) classLoader).getURLs();
        } catch (ClassCastException cce){
            // or cast failed; tokenize the system classpath
            classPaths = System.getProperty("java.class.path", "").split(File.pathSeparator);      
        }
        al = new ArrayList<Object>(Arrays.asList(classPaths)); 
        // alternative to above from classpath setting 
        //classPaths = System.getProperty("java.class.path", "").split(File.pathSeparator);      
        //al.addAll(Arrays.asList(classPaths)); 
        //System.out.printf("# java.class.paths\n");
        //for (Object o : classPaths)
        //    System.out.printf("#   %s\n", o.toString());

        //classPaths = System.getProperty("java.ext.dirs", "").split(File.pathSeparator);      
        // calc fm java home - lotta jarfiles under here tho
        //String jHome =  System.getProperty("java.home", "");
        //jHome = jHome + File.separator + "lib";
        //classPaths = new Object[]{jHome}; 

        classPaths = System.getProperty("sun.boot.class.path", "").split(File.pathSeparator);      
        al.addAll(Arrays.asList(classPaths)); 
        // Print classpath searching along
        //System.out.printf("# sun.boot.class.paths\n");
        //for (Object o : classPaths)
        //   System.out.printf("#   %s\n", o.toString());
        //return classPaths;    
        System.out.printf("# Class paths and boot paths being searched\n");
        for (Object o : al)
            System.out.printf("#   %s\n", o.toString());
        return al.toArray();
    } 
    
    
  /**
   * Recursively lists a directory while generating relative paths. This is a helper function for findClasses.
   * Note: Uses a StringBuffer to avoid the excessive overhead of multiple String concatentation
   *
   * @param dirListing     A list variable for storing the directory listing as a list of Strings
   * @param dir                 A File for the directory to be listed
   * @param relativePath A StringBuffer used for building the relative paths
   */
    private static void recursivelyListDir(List<String> dirListing, File dir, StringBuffer relativePath){
        int prevLen; // used to undo append operations to the StringBuffer
    
        // if the dir is really a directory 
        if ( dir.isDirectory() ) {
            File[] files = dir.listFiles();   // get a list of the files in this directory
            // for each file in the present dir
            for (int i = 0; i < files.length; i++) {
                // store our original relative path string length
                prevLen = relativePath.length();
                // call this function recursively with file list from present
                // dir and relateveto appended with present dir
                recursivelyListDir(dirListing, files[i], relativePath.append( prevLen == 0 ? "" : "/" ).append( files[i].getName() ) );
                //  delete subdirectory previously appended to our relative path
                relativePath.delete(prevLen, relativePath.length());
            }
        } else {
            // this dir is a file; append it to the relativeto path and add it to the directory listing
            dirListing.add( relativePath.toString() );
        }
    }
    
    
    @SuppressWarnings("unchecked") 
    static private void processFile( String fileName, 
                                ClassLoader classLoader, 
                                Set<String> packageFilter,
                                Set<String> suppressFilter,
                                Set<Class> allClasses)  throws ClassNotFoundException {
        // convert our full filename to a fully qualified class name without trailing ".class"
        String className = fileName.replaceAll("/", ".").substring(0, fileName.length() - 6);
        // debug class list
        //System.out.println(className);
        // skip any classes in packages not explicitly requested in our package filter          
        if ( packageFilter != null) {
            int li = className.lastIndexOf("."); 
            if (li < 0 || (li > 0 && !packageFilter.contains(className.substring( 0, li))))
                //System.out.println("skip " + className + " " + li);
                return;
        }     
                                    
        // suppress filtered package contents
        if ( suppressFilter != null) {
            for (String head : suppressFilter) {
                if (className.startsWith(head))  {
                    //System.out.println("suppress " + className);
                    return;
                }
            }
         }
                                    
        // get the class for our class name
        Class theClass = null;
        try {
          theClass = Class.forName(className, false, classLoader);
        } 
        catch (NoClassDefFoundError e){
          System.out.println("Skipping class '" + className + "' for reason " + e.getMessage());
          return;
        } 
        catch (ClassNotFoundException cnfe) {
          System.out.println("LOAD ERROR: Cannot find/load class '" + className + "'");
          System.out.println("Ensure the jarfile or class directory containig the class is given in the classpath");
          System.out.printf("e.g. java -cp '.%smy.jar' PkgSig -j my.jar\n", File.pathSeparator);
          System.out.printf("     java -cp '.%s./java' PkgSig  com.mySource\n\n", File.pathSeparator);
          throw cnfe;
        }
                                    
        int m = theClass.getModifiers();
        //if( java.lang.reflect.Modifier.isPublic(m) )
        //if( ! java.lang.reflect.Modifier.isPrivate(m) )
        if(  Modifier.isPublic(m) || Modifier.isProtected(m) ) {
            allClasses.add( theClass );
        }
                                    
        // special case for pkgPrivate java.lang.AbstractStringBuilder parent of public String{Buffer,Builder}
        if ( ClassSig.isPkgPrivate(m) && theClass.getName().equals("java.lang.AbstractStringBuilder") ) {
            allClasses.add( theClass );
        }
                                          
        // skip interfaces
        //if( theClass.isInterface() ){
        //  continue;
        //}
        //accumulateInterfaces(theClass, classTable);  
    }
    
                        
    @SuppressWarnings("unchecked") 
    static private void processJarFile( String fileName, 
                                ClassLoader classLoader, 
                                Set<String> packageFilter,
                                Set<String> suppressFilter,
                                Set<Class> allClasses)  throws ClassNotFoundException 
    {
        JarFile module = null;
        try {
            module = new JarFile( fileName );
        } catch (MalformedURLException mue){
            throw new ClassNotFoundException("Bad classpath. Error: " + mue.getMessage());
        } catch (IOException io){
            throw new ClassNotFoundException("jar file '" + fileName + 
                      "' could not instantiate Jar from file path. Error: " + io.getMessage());
        }
        System.out.println("# jarfile:  "+ module.getName());
        Enumeration files = module.entries();
        while( files != null && files.hasMoreElements() ){
            String fileNameEl = files.nextElement().toString(); 
            if ( fileNameEl.endsWith(".class") ) {
                //System.out.println("#file:"+fileNameEl);
                processFile(fileNameEl, classLoader, packageFilter, suppressFilter, allClasses);
            }
        }

      if (module != null) {
          try {
              module.close();
          } catch(IOException ioe) {
              throw new ClassNotFoundException("The module jar file '" + fileName +
                "' could not be closed. Error: " + ioe.getMessage());
          }
      }
    }
    
    // --- New cobra stuff from here

    public static void usage() {
        System.out.println("Unknown usage");
        System.out.printf("usage: pkgSig [-p] <pkgName>\n");
        System.out.printf("usage: pkgSig -j <jarfile>\n");
        System.out.printf("usage: pkgSig -jrt\n ");
        System.out.printf("  Display class signatures for a package or contents of a jarfile.\n\n");
        System.out.printf("A class signature is the class name, its superclass and signatures for fields and methods of the class.\n");
        System.out.printf("Packages are searched for in files and jars in classpath.\n");
        System.out.printf("Jarfiles are specified as relative or absolute pathnames. Relative pathname Jarfiles not found are searched for in the classpath\n");
        System.out.printf("The -jrt invocation displays class Signatures for the java runtime jarfile(s)\n");
        System.out.printf("Output format is suitable for parsing and use by the cobra java backend (cross) compiler.\n");
        System.exit(2);
    }
    
    public static File lookForJarFile( String fileName, ClassLoader loader) {
        File jarFile = new File(fileName);
        if ( jarFile.exists())
            return jarFile;
        
        // search for jarfile in Classpath
        if ( ! jarFile.isAbsolute() ) {
            Object[] classPaths = getClassPaths(loader);
            //for (int h = 0; h < classPaths.length; h++) {
            for ( Object cpo : classPaths) { // for each classpath ...
                //System.out.printf("DBG: cpo %s\n", cpo.toString());
                File classPath = new File((URL.class).isInstance(cpo) ? ((URL)cpo).getFile() : cpo.toString());
                if ( classPath.getName().endsWith(".jar") ) {     // is our classpath a jar?
                    if (classPath.getName().endsWith(fileName)) {
                        jarFile = classPath;
                        break;
                    }
                }
                else if ( classPath.isDirectory() ) { 
                    jarFile = new File(classPath, fileName);
                    //System.out.printf("DBG trying %s\n", jarFile.getAbsolutePath());
                    if (jarFile.exists())
                        break;
               }
            }
        }
        return jarFile;
    }
    
    public static void sigForJarFile(String fileName, Set<String> suppressFilter) throws ClassNotFoundException
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        assert loader != null;
        File jarFile = PkgSig.lookForJarFile(fileName, loader);
        if ( !jarFile.exists()) {
            System.out.printf("File '%s' does not exist.\n", jarFile.getAbsolutePath());
            System.exit(1);
        }
            
        //System.out.printf( "DBG: jarfile=%s\n", jarFile.getAbsolutePath());
        Set<Class> clsSet = new java.util.concurrent.ConcurrentSkipListSet<Class>(new PkgSig.ClassComparator());

        System.out.printf("# PkgSig file for jarfile '%s'\n", jarFile.getAbsolutePath());
        processJarFile(jarFile.getAbsolutePath(), loader, null, suppressFilter, clsSet);
        
        for ( Class cls : clsSet) {
            new ClassSig(cls).emit();
        }
    }
    
    public static void main( String[] args) throws Exception {
        if (args.length ==0 )
              usage();

        if (args.length == 1 && !args[0].startsWith("-") ) {  // default -p pkgname [...]
            String pkg = args[0];
            Set<Class> clsSet = PkgSig.findClassesForPkg(pkg);
            if (clsSet.size() == 0 )
            {
                System.out.printf("No package '%s' found in classPath\n", pkg);
                System.exit(1);
            }

            for ( Class cls : clsSet) {
                ClassSig classSig = new ClassSig(cls);
                classSig.emit();    
            }
        }
        else if (args[0].equals("-p")) {
            String pkg = args[1];
            Set<Class> clsSet = PkgSig.findClassesForPkg(pkg);
            for ( Class cls : clsSet) {
                new ClassSig(cls).emit();
            }
        }
        else if (args[0].equals("-j")) {
            PkgSig.sigForJarFile(args[1], null);
        }
        else if (args[0].equals("-jrt")) {  // java runtime 
            Set<String> suppressFilter = new HashSet<String>();
            
            // Theres upwards of 8000 classes in rt.jar most of which we dont care about
            // suppress internal implementation classes which shouldnt be using anyway
            suppressFilter.add("com.sun");
            suppressFilter.add("com.sunw");
            suppressFilter.add("sun");
            suppressFilter.add("sunw");
            
            // suppress some biggies we 'probably' wont ever use - may want to revisit these
            suppressFilter.add("org.omg"); // Corba Sh*t - cos *everybody* needs this in the base RTL
            suppressFilter.add("javax.swing.plaf"); // swing platform GUI implementations - ditto
            suppressFilter.add("javax.print.attribute.standard");
            suppressFilter.add("org.jcp.xml.dsig"); // bloat in the RTL - why should we worry about that?

            PkgSig.sigForJarFile("rt.jar", suppressFilter);
        }
        else {
            usage();
        }
              
            
   }
}

// This class emits class info in format suitable for cobra compiler to (easily) parse.
// see Source/BackEndJvm/JvmJarSig.cobra
// Any changes made here should be in lockstep with that file....
class ClassSig {
    Class cls;
    TypeVariable<?>[] genericParams = {};
    int indent= 0;
    static String TYPE_SEP = ",";
          
    public ClassSig(Class cls) {
        this.cls = cls;
    }    

    // main entry point
    public void emit() {
        // suppress anon inner classes
        if ( this.cls.getSimpleName().trim().length() == 0 )
            return;
        
              
        System.out.println("\n" + this.cls.getName());
        this.indent++;
        emitClassHeader();
        emitFields();
        emitConstructors();
        emitMethods();
        this.indent--;
    }
    
    static public boolean isPkgPrivate(int m) {
        // if (!cobraGen)
        //  return false

        // suppress classes that have only pkgPrivate (default) accessability
        return (! Modifier.isPublic(m) && !Modifier.isProtected(m) && !Modifier.isPrivate(m));
    }
    
    public void emitClassHeader() {
        String flags ="";
        String t ="class";
        if (this.cls.isInterface()) t ="Interface";
        else if (this.cls.isEnum()) t ="Enum";
        printIndent();
        System.out.printf( "%-30s %s\n", t, "# JavaType");
        
        printIndent();
        String pkgName = "";
        if (this.cls.getPackage() != null)
            pkgName = this.cls.getPackage().getName();
        System.out.printf("%-30s %s\n", pkgName, "# package");
        
        // Name of the class(without pkgname), dotNet form for Generic class
        String clsName = this.cls.getName();
        if (clsName.startsWith(pkgName+"."))
              clsName = clsName.substring(pkgName.length()+1);
        String gName = this.dotNetGenericsName(this.cls, clsName);
        if (! gName.isEmpty()) {
            clsName = gName;
            //System.out.print(gName);
            flags += "G";
        }
        
        printIndent();
        System.out.printf("%-30s %s", clsName, "# name");
        System.out.println();
        
        //Superclass name - Use Generics superclass if have one and display in dotNet form
        Class superCls = this.cls.getSuperclass();
        String superName = "-";
        if ( superCls != null)
            superName = superCls.getName();
        if ( superName != "-") {
            String gSuperName = this.dotNetGenericsSuperName(this.cls);
            if (! gSuperName.isEmpty() ) {
                superName = gSuperName;
            }
        }
        printIndent();
        System.out.printf("%-30s %s", superName, "# superclass");
        System.out.println();

        //  Interfaces
        //System.out.printf("%-30s %s\n", '-', " # interfaces");
        //Class[] interfaces = this.cls.getInterfaces();
        //emitClassList(interfaces, "nonG interfaces");
        Type[] gInterfaces = getAllGenericInterfaces(this.cls);
        //Type[] gInterfaces = this.cls.getGenericInterfaces();
        emitTypesList(gInterfaces, " Interfaces");
        //System.out.printf("### nIfcs %d, ngIfcs %d\n", interfaces.length, gInterfaces.length);

        // Modifiers
        //System.out.printf("%-30s %s\n", '-', " # modifiers");
        int m = this.cls.getModifiers();
        emitModifiers(m);

        if (this.genericParams.length > 0 )  // is a generic class
            emitGenericClassParameters(); 
    }
    
    void printIndent() {
        for (int i=0; i < indent; i++)
            System.out.print("    ");       // 4 spaces
    }
    
    String dotNetGenericsName( Class cls, String clsName) {
        // generics class naming like .Net  TypeName`<nTypeParams>
        String gName="";
        TypeVariable<?>[] gParams = cls.getTypeParameters();
        if (gParams.length > 0) { // 0 TypeParameters -> not generic
            gName = String.format("%s`%d", clsName, gParams.length);
            //System.out.printf(" gName=%s`%d", clsName, genArgs.length);            
            this.genericParams = gParams; // copy for later use
        }
        return gName;
    }
    
    String dotNetGenericsSuperName( Class cls) {
        Type gsuperCls = cls.getGenericSuperclass();
        String gsuperName = "";
        //gsuperName = gsuperCls.getCanonicalName();
        if ( gsuperCls instanceof ParameterizedType) {
            gsuperName = gsuperCls.toString(); // this seems the only way to get the name
            int idx = gsuperName.indexOf("<"); 
            gsuperName = gsuperName.substring(0, idx); // break off GenericTypes
            // System.out.printf(" gSuper=%-30s", gsuperName);
            //gsuperName = gsuperCls.getCanonicalName();
            Type[] typeArgs = ((ParameterizedType)gsuperCls).getActualTypeArguments();
            if (typeArgs.length > 0) { // 0 TypeParameters -> not generic
                gsuperName = String.format("%s`%d", gsuperName, typeArgs.length);
            }
        }
        return gsuperName;
    }

    @SuppressWarnings("unchecked") 
    public void emitFields() {
        Field[] fields = this.cls.getDeclaredFields();
        for (java.lang.reflect.Field f : fields) {
            int m = f.getModifiers();
            if( java.lang.reflect.Modifier.isPrivate(m) || isPkgPrivate(m))
                continue;
            printIndent();
            
            //printIndent();
            //System.out.printf("%s\n", f.getName());
            System.out.print("var ");
            System.out.println(f.getName());
            this.indent++;

            emitModifiers(m);

            if (this.cls.isEnum()) {
                printIndent();
                String typeName = f.getType().getName();
                System.out.printf("%s\n", typeName);
                //if ( f.getType().isEnum()) {
                if ( f.isEnumConstant()) {
                    this.indent++;
                    printIndent();
                    System.out.printf("%d\n", Enum.valueOf( this.cls, f.getName()).ordinal() );
                    //System.out.printf("%s\n", Enum.valueOf( this.cls, f.getName()));
                    this.indent--;
                }
            }    
            else
            {
                String gTypeStr = this.makeTypeStr(f.getGenericType());
                String tag = "# Type";
                if (gTypeStr.contains("<"))
                    tag = "# generic Type :G";
                printIndent();
                System.out.printf("%-30s %s\n", gTypeStr, tag);
            }          
            
                
            //TODO attributes
            //printIndent();
            //System.out.printf("%s\n", '-');
            this.indent--;      
        }
    }
            
    public void emitModifiers(int m) {
        StringBuilder sb = new StringBuilder();
        if (Modifier.isStatic(m))    sb.append("static ");
        if (Modifier.isAbstract(m))  sb.append("abstract ");
        if (Modifier.isSynchronized(m))  sb.append("synchronized ");
        if (Modifier.isFinal(m))     sb.append("final ");
        if (Modifier.isPublic(m))    sb.append("public ");
        if (Modifier.isProtected(m)) sb.append("protected ");
        if (Modifier.isPrivate(m))   sb.append("private ");
        if (! Modifier.isPublic(m) && !Modifier.isProtected(m) && !Modifier.isPrivate(m))
            sb.append("default ");
        printIndent();
        String comment = "# modifiers";
        if (sb.length() == 0 ) comment = "# no modifiers";
        System.out.format("%-30s %s\n", sb.toString(), comment);
    }

    public void emitConstructors() {
        Constructor[] ctors = this.cls.getDeclaredConstructors();
        for (Constructor ctor : ctors) {
            int m = ctor.getModifiers();
            if( java.lang.reflect.Modifier.isPrivate(m) || isPkgPrivate(m))
                continue;

            printIndent();

            this.indent++;
            System.out.print("ctor ");
            System.out.println(ctor.getName());

            //Class[] parTypes = ctor.getParameterTypes();
            //emitClassList(parTypes, "parameters");
            Type[] gparTypes = ctor.getGenericParameterTypes();
            emitTypesList(gparTypes, "Parameters");

            this.indent--;
        }
    }

    public void emitMethods() {
        Method[] methods = this.cls.getDeclaredMethods();
        for (Method m : methods) {
            int mod = m.getModifiers();
            if( Modifier.isPrivate(mod) || isPkgPrivate(mod) ) continue;

            printIndent();
            System.out.print("method ");
            String mName = m.getName();
            if (m.isVarArgs())
                mName = mName + "[V]" ;     
            System.out.println(mName);
            
            this.indent++;
            emitModifiers(mod);
            
            //Class retType = m.getReturnType();
            //String retTypeName = retType.getName();
            //printIndent();
            //System.out.printf("%-30s %s\n", retTypeName, "# returnType");
            String retTypeStr = this.makeTypeStr(m.getGenericReturnType());
            //if (! retTypeStr.startsWith(retTypeName)) 
            {
                printIndent();
                String tag = "# returnType";
                if (retTypeStr.contains("<"))
                    tag = "# generic returnType :G";
                System.out.printf("%-30s %s\n", retTypeStr, tag);
            }          
            
            //Class[] parTypes = m.getParameterTypes();
            //emitClassList(parTypes, "parameters");
            Type[] gparTypes = m.getGenericParameterTypes();
            emitTypesList(gparTypes, "Parameters");
            
            //Class[] exceptions = m.getExceptionTypes();
            //emitClassList(exceptions, "exceptions");
            Type[] gExcTypes = m.getGenericExceptionTypes();
            emitTypesList(gExcTypes, "Exceptions");

            this.indent--;
        }
    }

    public void emitClassList(Class[] clsList, String tag) {
        printIndent();
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (Class c : clsList) {
            sb.append(sep);
            sb.append(c.getName());
            sep = TYPE_SEP;            
        }
        if (clsList.length == 0 ) 
            sb.append("-");
        
        String commentPrefix = ((clsList.length == 0 ) ?
              "# no " :
              "# ");
        StringBuilder comment = new StringBuilder(commentPrefix);
        comment.append(tag);
        System.out.printf("%-30s %s\n", sb.toString(), comment.toString());
    }

    public void emitGenericClassParameters() {
        assert this.genericParams.length > 0 ;
        //if (this.genericParams.length == 0 )  // not a generic class
        //    return; 
        
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (TypeVariable<?> p : this.genericParams) { // 
            sb.append(sep);
            sb.append(p.getName());
            sep = TYPE_SEP;            
            
        }
        //if (this.genericParams.length == 0 ) 
        //    return; //sb.append("-");
        
        String commentPrefix = ((this.genericParams.length == 0 ) ? "# no " : "# ");
        StringBuilder comment = new StringBuilder(commentPrefix);
        comment.append(" Generic Parameter list :G");
        printIndent();
        System.out.printf("%-30s %s\n", sb.toString(), comment.toString());
    }
    
    public void emitTypesList(Type[] typList, String tag) {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (Type t : typList) {
            sb.append(sep);
            String tStr = this.makeTypeStr(t);
            sb.append(tStr);
            sep = TYPE_SEP;            
            
        }
        if (typList.length == 0 ) {
            //if (isOptional)
            //    return; 
            //else
                sb.append("-");
        }
        
        String commentPrefix = ((typList.length == 0 ) ?
              "# no " :
              "# ");
        StringBuilder comment = new StringBuilder(commentPrefix);
        comment.append(tag);
        if (sb.toString().contains("<"))
            comment.append(" :G");

        printIndent();
        System.out.printf("%-30s %s\n", sb.toString(), comment.toString());
    }
    
    /*
     * Cleanup a (Generic) Type name to be 'consistant' with class naming.
     * 
     * @param t   Type to do name cleanup on
     * @return     String representation of 'cleaned' Type 
     */
    public String makeTypeStr(Type t) {
        String tStr = t.toString();
        //System.out.printf("### %-50s \n", tStr);
        tStr = tStr.replace("java.util.Map.java.util.Map", "java.util.Map");  // EntrySets screwed for some reason
        tStr = tStr.replace("java.nio.file.WatchEvent.java.nio.file.WatchEvent", "java.nio.file.WatchEvent"); 
        tStr = tStr.replace("java.nio.file.DirectoryStream.java.nio.file.DirectoryStream", "java.nio.file.DirectoryStream");

        tStr = tStr.replace("class ", "");  // sometimes puts on leading 'type '
        tStr = tStr.replace("interface ", "");  
        // possibly others - enum ?
        tStr = tStr.replace("<?>", "");     // lose generic wildcard
        if ( tStr.endsWith("[]")) {     // Type makes ' <name>[]' rather than '[L<name>;' for arrays
            StringBuilder sfx = new StringBuilder();
            while (tStr.endsWith("[]")) {
                tStr = tStr.substring(0, tStr.length()-2);
                sfx = sfx.append("[");
            }
            sfx.append("L");
            sfx.append(tStr);
            sfx.append(";");       
            tStr = sfx.toString();
        }
        return tStr;
    }
    
    /* Walk class inheritance hierachy from a class up,  accumulate and return all the 
     * interfaces (generic and not) supported.
     * Uses getGenericInterfaces to lookup interfaces for each class.
     * 
     * @param cls   Class to obtain all interfaces for.
     * @return      Array of Types containing all generic interfaces found
     */
    public Type[] getAllGenericInterfaces(Class cls) {
        List<Type> ifcs = new ArrayList<Type>();
        while ( cls != null ) {
            for ( Type t : cls.getGenericInterfaces() )
	        ifcs.add(0, t); // insert at front so list is in superclass downwards order
            cls = cls.getSuperclass();
        }

        // remove any dups using a Set
        Set<Type> ifcSet = new LinkedHashSet<Type>();
        for ( Type t : ifcs )
	        ifcSet.add(t);
        return ifcSet.toArray(new Type[0]);
    }
}
