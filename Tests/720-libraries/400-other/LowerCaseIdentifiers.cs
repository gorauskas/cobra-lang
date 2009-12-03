/*
lcase namespace in  lib test - Library
cobra -t:lib lcase.cs
gmcs -t:library -out:lcase.dll lcase.cs
csc /t:library /out:lcase.dll lcase.cs
*/
namespace lcase {
   public class A {
      string _what = "class A";
      
      public int One = 1;

      public string what {
        get { return _what; }
        set { _what = value;}
      }
      public string getWho() {
        return "lc";
      }
   }
}
