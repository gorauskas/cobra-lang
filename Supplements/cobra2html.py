"""
cobra2html: Convert Cobra source code into syntax highlighted HTML.

Example uses:
    cobra2html foo.cobra
    cobra2html -page foo.cobra
    cobra2html -ln foo.cobra bar.cobra
    cobra2html -styles
    cobra2html -h

Command line options:
    -h or -help for help
    -ln for line numbers
    -page for complete page
    -styles for outputting CSS styles

Support:
    Requires Python and Pygments.
    Tested with Python 2.5 and Pygments 0.9 on Mac OS X 10.5
    http://cobra-language.com/forums/    

Credits:
    Pygments library by Georg Brandl.
    Cobra mods by Chuck Esterbrook.
"""

class MoreNotes:
	"""
	Sure it would be nice if the Cobra command line did this using it's own lexer
	and parser. But it doesn't right now and Georg Brandl <g brandl at gmx net>
	has done a fine job creating a syntax highlighter called Pygments.

	Line numbers would be cool except that at least on Firefox 2.0 on the Mac,
	there is a bug in the display (with Firefox, not the HTML or styles).
	Safari works fine.

	TODO:
	Cobra specific highlighting.
	-page for writing out complete page instead of just fragment
	-source for putting some source right on the command line
	-q or -quiet for less output
	"""
	pass

import os, sys, time
true, false, nil = True, False, None

try:
	import pygments
except ImportError:
	print 'Cannot import required package: pygments'
	print 'Download and install from: http://pygments.org/'
	print 'Exiting.'
	sys.exit(1)

from pygments import highlight
from pygments import lexers # ha get_lexer_by_name, PythonLexer and others
from pygments.formatters import HtmlFormatter

def main(args=None):
	fileNames = []
	linenos = false
	completePage = false

	if args is nil:
		args = sys.argv[1:]
		if not args:
			if os.path.exists('pyg.py'):
				print 'Testing pyg.py'
				args = ['pyg.py']
			else:
				error('Pass some command line args.')
		for arg in args:
			while arg.startswith('--'):
				arg = arg[1:]
			if arg in ['-h', '-help']:
				help()
				return
			if arg == '-ln':
				linenos = true
				continue
			if arg == '-page':
				completePage = true
				continue
			if arg == '-styles':
				writeStyles()
				return
			if arg.startswith('-'):
				error('Unknown option: ' + arg)
			fileNames.append(arg)

	# print fileNames
	for fileName in fileNames:
		processFile(fileName, linenos, completePage)

def error(msg):
	print 'ERROR:', msg
	help()
	sys.exit(1)

def help():
	print __doc__

def processFile(inName, linenos=false, completePage=false):
	print 'Reading', inName
	code = open(inName).read()

	lexer = lexers.get_lexer_by_name('python', stripall=True)
	formatter = HtmlFormatter(linenos=linenos, cssclass='cobra')

	outName = inName + '.html'
	outFile = open(outName, 'wt')
	print 'Writing', outName
	if completePage:
		outFile.write("""<html>
<head>
		<link rel="stylesheet" href="cobra-highlight-styles.css" type="text/css">
</head>
<body>
""")
	highlight(code, lexer, formatter, outFile)
	if completePage:
		outFile.write("""
</body>
</html>
""")
	outFile.close()
	
def writeStyles():
	s = HtmlFormatter().get_style_defs('.cobra')
	outName = 'cobra-highlight-styles.css'
	print 'Writing', outName
	open(outName, 'wt').write(s)

main()
