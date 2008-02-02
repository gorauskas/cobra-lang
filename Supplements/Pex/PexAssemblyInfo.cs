using Microsoft.Pex.Framework.Validation;
using Microsoft.ExtendedReflection.Metadata;
using Microsoft.Pex.Framework.Packages;
using Microsoft.Pex.Engine.ComponentModel;
using Microsoft.ExtendedReflection.Utilities.Safe.Diagnostics;
using Microsoft.Pex.Engine.Exceptions;
using Microsoft.ExtendedReflection.Utilities;
using Microsoft.ExtendedReflection.Symbols;
using System;
[assembly: PexAllowedExceptionFromAssembly(typeof(System.ArgumentNullException), "TestProject1")]


[assembly: Cobra.CobraAssumptionExceptionValidator]

namespace Cobra {
	
	// Peli de Halleux said in an email: There's a bug in the version of the attribute that I sent you...
	// Currently I'm checking for multiple instance of require_ in the stack trace but more has to be done.
	// We can chat about this.  2008-01
	
    public sealed class CobraAssumptionExceptionValidatorAttribute :
        PexExplorationPackageAttributeBase {
        protected override object BeforeExploration<TAbstractValue>(IPexExplorationComponent<TAbstractValue> host) {
            SafeDebug.AssumeNotNull(host, "host");

            host.ExplorationServices.ExceptionManager.AddAssumptionExceptionValidator(
                new Validator<TAbstractValue>(host)
                );
            return null;
        }

        class Validator<TAbstractValue> : IPexAssumptionExceptionValidator {
            IPexExplorationComponent<TAbstractValue> host;

            public Validator(IPexExplorationComponent<TAbstractValue> host) {
                SafeDebug.AssumeNotNull(host, "host");
                this.host = host;
            }

            bool IPexAssumptionExceptionValidator.IsAssumption(Exception ex) {
                SafeDebug.AssumeNotNull(ex, "ex");

                // 1) must be a requires exception
                string trace;
                Type exceptionType = ex.GetType();
                if (exceptionType == typeof(Cobra.Lang.RequireException) &&
                    ExceptionHelper.TryGetBestStackTrace(ex, out trace)) {
                    StackTraceEx tx = host.Services.SymbolManager.ParseStackTrace(trace, true);

                    bool globalsFound = false;
                    for (int i = tx.Frames.Count - 1; i >= 0; i--) {
                        StackFrameEx frame = tx.Frames[i];
                        if (frame.MethodName.StartsWith("require_")) {
                            if (globalsFound) // already found? that's not good
                                return false;
                            globalsFound = true;
                        }
                    }

                    return globalsFound;
                }

                return false;
            }
        }

        protected override void AfterExploration<TAbstractValue>(Microsoft.Pex.Engine.ComponentModel.IPexExplorationComponent<TAbstractValue> host, object data) {
        }
    }
}
