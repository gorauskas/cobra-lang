call cobra-new -compile BlindWatchMaker1
call cobra-new -compile Download
call cobra-new -compile google-api GoogleSearchService.cs
call cobra-new -compile Sizes
call cobra-new -compile WordCount
mv GoogleSearchService.cs GoogleSearchService.cs.save
del *.cs *.pdb
mv GoogleSearchService.cs.save GoogleSearchService.cs
dir
