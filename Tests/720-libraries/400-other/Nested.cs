/*
ticket:127
see also read-lib-nested-classes-twist.cobra in this directory

csc /t:library /out:Nested.dll Nested.cs

*/


namespace Nested {

	public class A {
		
		// note the nested classes
		public class B {
			
		}
		
		public class C : B {  // and one inheriting from the other. the twist.
			
		}
		
	}

	public class D : A {
		
	}
	
}
