<script type="text/javascript">
/* <![CDATA[ */

var config = {
	popupDistance: 20,
	clickForPopup: false // Set to true to open popups with a click
};


var getElem = function(id) {
	return document.getElementById(id);
}

var findByTag = function(obj, tag) {
	return obj.getElementsByTagName(tag);
}

var findByClass = function(elem, className) {
	if (elem.getElementsByClassName)
		return elem.getElementsByClassName(className);
	else {
		var classRegex = new RegExp('\\b' + className + '\\b');
		var elems = findByTag(document, '*');
		var matches = [];
		var count = 0;

		for (var i = 0, len = elems.length; i < len; i++) {
			var curElem = elems[i];
			if (curElem.className && classRegex.test(curElem.className)) {
				matches[count] = curElem;
				count++;
			}
		}
	}
}

var css = function(id, cssprop) {
	var elem = typeof id == 'string' ? document.getElementById(id) : id;
	if (elem.currentStyle)
		return elem.currentStyle[cssprop];
	else if (window.getComputedStyle)
		return document.defaultView.getComputedStyle(elem, null).getPropertyValue(cssprop);
}

var getComputed = function(elem, cssprop) {
	var elem = typeof elem == 'string' ? document.getElementById(elem) : elem;
	return document.defaultView.getComputedStyle(elem, null).getPropertyValue(cssprop);	
}

// Source: http://webcodingeasy.com/Javascript/Get-scroll-position-of-webpage--crossbrowser
var getScroll = function(){
    var x = 0, y = 0;
    if( typeof( window.pageYOffset ) == 'number' ) {
        //Netscape compliant
        y = window.pageYOffset;
        x = window.pageXOffset;
    } else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
        //DOM compliant
        y = document.body.scrollTop;
        x = document.body.scrollLeft;
    } else if( document.documentElement && 
    ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
        //IE6 standards compliant mode
        y = document.documentElement.scrollTop;
        x = document.documentElement.scrollLeft;
    }
    var obj = new Object();
    obj.x = x;
    obj.y = y;
    return obj;
};

var getClientHeight = function() {
	if (typeof window.innerHeight != 'undefined')
		return window.innerHeight;
	return document.documentElement.clientHeight;
}

var getClientWidth = function() {
	if (typeof window.innerWidth != 'undefined')
		return window.innerWidth;
	return document.documentElement.clientWidth;
}

var getOuterHeight = function(elem) {
	return parseFloat(css(elem, 'height')) + parseFloat(css(elem, 'padding-top')) + parseFloat(css(elem, 'padding-bottom'));
}

var getOuterWidth = function(elem) {
	return parseFloat(css(elem, 'width')) + parseFloat(css(elem, 'padding-left')) + parseFloat(css(elem, 'padding-right'));
}

var resetMenu = function() {
	var menu = getElem('sources');
	var links = findByTag(menu, 'a');
	for (var i = 0, len = links.length; i < len; i++) {
		var className = links[i].className;
		if (className)
			links[i].className = className.replace(/\bcurrent\b/, '');
	}
}

var menuClickHandler = function(e) {
	e.preventDefault();
	var results = getElem('queries');
	var res = findByTag(results, 'table');

	for (var i = 0, len = res.length; i < len; i++)
		res[i].style.display = 'none';

	var currentResult = getElem(e.currentTarget.rel);
	currentResult.style.display = 'block';
	resetMenu();
	e.currentTarget.className = e.currentTarget.className + ' current';
}


var initMenu = function() {
	var menu = getElem('sources');
	var links = findByTag(menu, 'a');
	for (var i in links)
		links[i].onclick = menuClickHandler;
}

var openPopups = {};

var showPopup = function(id, position) {
	closePopups(id);
	var elem = getElem(id);
	if (elem) {
		openPopups[id] = elem;
		elem.style.left = position.left + 'px';
		elem.style.top = position.top + 'px';
		elem.style.display = 'block';
	}
}

var closePopups = function(skipId) {
	for (var key in openPopups) {
		if (key == skipId) 
			continue;
		var popup = openPopups[key];
		popup.style.display = 'none';
		getElem('link-' + key).style.color = null; // remove highlighting
		delete openPopups[key];
	}
}

var popupClickHandler = function(e) {
	e.stopPropagation();
}

var documentClickHandler = function(e) {
	closePopups();
}

var initPopups = function() {
	var popupLinks = findByClass(document, 'box-pair-link');

	for (var i = 0, len = popupLinks.length; i < len; i++) {
		popupLinks[i][config.clickForPopup ? 'onclick' : 'onmouseover'] = function(e) {
			e.preventDefault();
			var link = e.currentTarget;

			if (config.clickForPopup) {
				e.stopPropagation();
			}
			else {
				link.onclick = function(e) { 
					e.preventDefault(); 
					e.stopPropagation();
				}
			}	

			var relatedPopup = link.rel;
			if (!relatedPopup)
				return; // No full doc popup for this link.

			var popup = getElem(relatedPopup);
			if (!popup)
				return; // A related popup was specified but the popup was not generated.

			// Highlight link style for active popup
			link.style.color = '#f00';

			var rect = link.getBoundingClientRect(); // .left and .top are with reference to the client area (disregarding scrolls)
			getElem(relatedPopup).onclick = popupClickHandler;
			
			var scroll = getScroll();
			var clientWidth = getClientWidth();
			var clientHeight = getClientHeight();
			var popupHeight = getOuterHeight(popup);
			var popupWidth = getOuterWidth(popup);
			var leftDistance = (rect.left + rect.width / 2) - popupWidth / 2;
			var topDistance = scroll.y + rect.top + rect.height + config.popupDistance;

			if (leftDistance + popupWidth > clientWidth)
				leftDistance = clientWidth - popupWidth - config.popupDistance;

			if (leftDistance < 0)
				leftDistance = config.popupDistance;
			
			if (rect.top + popupHeight > clientHeight && rect.top > popupHeight + config.popupDistance)
				topDistance = scroll.y + rect.top - popupHeight - config.popupDistance;
				
			var popupPosition = {
				left: leftDistance,
				top: topDistance
			};		
				
			showPopup(relatedPopup, popupPosition);
		}
	}
	document.onclick = documentClickHandler;
}

window.onload = function() {
	initMenu();
	initPopups();
}

/* ]]> */	
</script>
