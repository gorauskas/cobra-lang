using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Domains;

namespace BlindWatchMakerTest {
    [TestClass, PexClass]
    public partial class BlindWatchMakerTest {
        [PexMethod]
        public void RandomString([PexTarget]BlindWatchMaker1 target,
            int length, string alphabet) 
        {
            string result = target.RandomString(length, alphabet);
            PexValue.Add("result", result);
        }

        [PexMethod]
        public void Score([PexTarget]BlindWatchMaker1 target,
            string candidate, string alphabet) {
            int result = target.Score(candidate, alphabet);
            PexValue.Add("result", result);
        }

        [PexMethod]
        public void RunCumulativeSelection([PexTarget]BlindWatchMaker1 target,
            string goal, string alphabet) {
            Cobra.Lang.CobraImp.Reset(Console.Out);
            double result = target.RunCumulativeSelection(goal, alphabet);
            PexValue.Add("result", result);
        }
    }
}
