<script type="text/javascript">
/* <![CDATA[ */

var getElem = function(id) {
	return document.getElementById(id);
}

Object.prototype.getElems = function(tag) {
	return this.getElementsByTagName(tag);
}

var resetMenu = function() {
	var menu = getElem('sources');
	var links = menu.getElems('a');
	for (var i = 0, len = links.length; i < len; i++) {
		var className = links[i].className;
		if (className)
			links[i].className = className.replace(/\bcurrent\b/, '');
	}
}

var menuClickHandler = function(e) {
	e.preventDefault();
	var results = getElem('queries');
	var res = results.getElems('table');

	for (var i = 0, len = res.length; i < len; i++)
		res[i].style.display = 'none';

	var currentResult = getElem(e.currentTarget.rel);
	currentResult.style.display = 'block';
	resetMenu();
	e.currentTarget.className = e.currentTarget.className + ' current';
}

window.onload = function() {
	var menu = getElem('sources');
	var links = menu.getElems('a');
	for (var i in links)
		links[i].onclick = menuClickHandler;
}

/* ]]> */	
</script>
