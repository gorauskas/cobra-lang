/*
csc /out:Foo.iSeries.dll /target:library Foo.iSeries.cs
gmcs -out:Foo.iSeries.dll -t:library Foo.iSeries.cs
*/
namespace Foo {
	namespace iSeries {
		public class iConnection {
			protected string _s;
			public virtual string S {
				get{
					return _s;
	  			}
				set{
					_s = value;
				}
			}
			public iConnection(string s) {
				_s = s;
			}
		}
	}
}
namespace little {
	
	public class A {
		
		public int One {
			get { return 1; }
		}
		
	}
	
}
