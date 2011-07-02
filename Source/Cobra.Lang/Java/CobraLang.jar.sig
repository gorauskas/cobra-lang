# PkgSig file for jarfile 'C:\home\hops\src\cobra\wkspace\Source\Cobra.Lang\java\CobraLang.jar'
# jarfile:  C:\home\hops\src\cobra\wkspace\Source\Cobra.Lang\java\CobraLang.jar

cobra.lang.CobraCore
    class                          # JavaType
    cobra.lang                     # package
    CobraCore                      # name
    java.lang.Object               # superclass
    -                              # no  Interfaces
    public                         # modifiers
    var _willCheckInvariant
        static public                  # modifiers
        java.lang.Boolean              # Type
    var _willCheckRequire
        static public                  # modifiers
        java.lang.Boolean              # Type
    var _willCheckEnsure
        static public                  # modifiers
        java.lang.Boolean              # Type
    var _willCheckAssert
        static public                  # modifiers
        java.lang.Boolean              # Type
    var _willCheckNil
        static public                  # modifiers
        java.lang.Boolean              # Type
    ctor cobra.lang.CobraCore
        -                              # no Parameters
    method noOp[V]
        static public                  # modifiers
        int                            # returnType
        [Ljava.lang.Object;            # Parameters
        -                              # no Exceptions
    method runAllTests
        static public                  # modifiers
        void                           # returnType
        -                              # no Parameters
        -                              # no Exceptions
    method printDebuggingTips
        static public                  # modifiers
        void                           # returnType
        -                              # no Parameters
        -                              # no Exceptions

cobra.lang.CobraImp
    class                          # JavaType
    cobra.lang                     # package
    CobraImp                       # name
    java.lang.Object               # superclass
    -                              # no  Interfaces
    public                         # modifiers
    var _printStringMaker
        static public                  # modifiers
        cobra.lang.CobraImp$SimpleStringMaker # Type
    ctor cobra.lang.CobraImp
        -                              # no Parameters
    method printLine
        static public                  # modifiers
        void                           # returnType
        -                              # no Parameters
        -                              # no Exceptions
    method printLine
        static public                  # modifiers
        void                           # returnType
        java.lang.String               # Parameters
        -                              # no Exceptions
    method printStop
        static public                  # modifiers
        void                           # returnType
        -                              # no Parameters
        -                              # no Exceptions
    method printStop
        static public                  # modifiers
        void                           # returnType
        java.lang.String               # Parameters
        -                              # no Exceptions
    method makeString
        static public                  # modifiers
        java.lang.String               # returnType
        java.lang.String               # Parameters
        -                              # no Exceptions
    method makeString
        static public                  # modifiers
        java.lang.String               # returnType
        [Ljava.lang.String;            # Parameters
        -                              # no Exceptions

cobra.lang.CobraImp$SimpleStringMaker
    class                          # JavaType
    cobra.lang                     # package
    CobraImp$SimpleStringMaker     # name
    java.lang.Object               # superclass
    -                              # no  Interfaces
    static public                  # modifiers
    ctor cobra.lang.CobraImp$SimpleStringMaker
        -                              # no Parameters
    method makeString
        public                         # modifiers
        java.lang.String               # returnType
        [Ljava.lang.String;            # Parameters
        -                              # no Exceptions
    method makeString
        public                         # modifiers
        java.lang.String               # returnType
        java.lang.String               # Parameters
        -                              # no Exceptions
    method makeString
        public                         # modifiers
        java.lang.String               # returnType
        int                            # Parameters
        -                              # no Exceptions
    method makeString[V]
        public                         # modifiers
        java.lang.String               # returnType
        [Ljava.lang.Object;            # Parameters
        -                              # no Exceptions
