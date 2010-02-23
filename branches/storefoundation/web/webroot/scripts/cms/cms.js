
var currentHighlightedElement = null;
var currentCMSMenu = null;


var IMAGES = "/scripts/cms/";

//
// Listener for onMouseOver and onMouseOut
// Displays/Hides the cms menu
//
function onMouse_Item(event, source, typeView) {
	if (typeView == 'border') {
		if (event.type == 'mouseover') {
			//mouseover
			event.cancelBubble=true;
			return showBorder(source);
		} else {
			//mouseout
			event.cancelBubble=true;
			return removeBorder(source);
		}
	} else {
		if (event.type == 'mouseover') {
			event.cancelBubble=true;
			return highlight(source);
		} else {
			event.cancelBubble=true;
			return lowlight(source);
		}
	}
}

//
// Listener for onContextMenu
// Creates the CMS menu
//
function onContextMenu_Item(event, mEdit, mCopy, callbackEvent, callbackWindow, jaloPk, webroot) {
	var element = getMenuHTMLElement();
	showCMSMenu( element, event);
	addItemLinks(element, mEdit, mCopy, callbackEvent, callbackWindow, jaloPk, webroot);
	event.cancelBubble=true;
	return false;
}

//
// Returns the HTML element (span) which holds the CMS menu.
// Lazily creates and appends the element as child of 'body'
//
function getMenuHTMLElement() {
	var result = document.getElementById('cmscontextmenu');
	if (result == null) {
		result = document.createElement("div");
		result.setAttribute( "id", "cmscontextmenu" );
		result.style.width="150px";
		result.style.position="absolute";
		result.style.backgroundColor="#e7e7e7";
		result.style.border="2px outset #e7e7e7";
		result.style.right="0px";
		result.style.top="0px";
		result.style.zIndex="1000";
		result.style.padding="0px";
		result.style.margin="0px";
		result.onmouseover = function() {this.style.visibility='visible';};
		result.onmouseout = function() {this.style.visibility='hidden';};
		var elements = document.getElementsByTagName("body");
		elements[0].appendChild(result);
	}
	return result;
}

function getLeft(element) {
	cmsLeft = element.offsetLeft;
	tempElement = element.offsetParent;
	while (tempElement != null) {
		cmsLeft += tempElement.offsetLeft;
		tempElement = tempElement.offsetParent;
	}
	return cmsLeft;
}

function getTop(element) {
	tsop = element.offsetTop;
	tempElement = element.offsetParent;
	while (tempElement != null) {
		tsop += tempElement.offsetTop;
		tempElement = tempElement.offsetParent;
	}
	return tsop;
}

function getWidth(element) {
	return element.offsetWidth;
}

function getHeight(element) {
	return element.offsetHeight;
}

function highlight(element) {
	document.getElementById("marker.top").style.visibility="visible";
	document.getElementById("marker.top").style.left=getLeft(element);
	document.getElementById("marker.top").style.width=getWidth(element);
	document.getElementById("marker.top").style.top=getTop(element);
	document.getElementById("marker.right").style.visibility="visible";
	document.getElementById("marker.right").style.left=getLeft(element)+getWidth(element)-2;
	document.getElementById("marker.right").style.top=getTop(element);
	document.getElementById("marker.right").style.height=getHeight(element);
	document.getElementById("marker.bottom").style.visibility="visible";
	document.getElementById("marker.bottom").style.left=getLeft(element);
	document.getElementById("marker.bottom").style.width=getWidth(element);
	document.getElementById("marker.bottom").style.top=getTop(element)+getHeight(element)-2;
	document.getElementById("marker.left").style.visibility="visible";
	document.getElementById("marker.left").style.left=getLeft(element);
	document.getElementById("marker.left").style.top=getTop(element);
	document.getElementById("marker.left").style.height=getHeight(element);
}

function lowlight(element) {
	document.getElementById("marker.top").style.visibility="hidden";
	document.getElementById("marker.right").style.visibility="hidden";
	document.getElementById("marker.bottom").style.visibility="hidden";
	document.getElementById("marker.left").style.visibility="hidden";
}

function showBorder(element) {
	element.style.border="2px solid red";
	element.style.zIndex="100";
	element.style.display="block";
}

function removeBorder(element) {
	element.style.border="";
	element.style.zIndex="";
	element.style.display="";
}

function showCMSMenu( menu, event ) {

	if (currentCMSMenu != null) {
		currentCMSMenu.style.visibility='hidden';
	}
	currentCMSMenu = menu;
	currentCMSMenu.style.visibility='visible';
	var scrollTopPos = getScrollY();
	var scrollLeftPos = getScrollX();
	var newCMSLeft = scrollLeftPos + event.clientX - 5;
	var newCMSTop = scrollTopPos + event.clientY - 5;
	currentCMSMenu.style.left = newCMSLeft + "px";
	currentCMSMenu.style.top = newCMSTop + "px"
	if( currentCMSMenu.hasChildNodes() ) {
		while(currentCMSMenu.childNodes.length > 0) {
			currentCMSMenu.removeChild( currentCMSMenu.firstChild );
		}
	}
}

function addTemplateLink( menu, locString, callbackEvent, callbackWindow, template, webroot ) {
	currentCMSMenu = menu;
	var nobr = document.createElement("nobr");
	var link = document.createElement("a");
	link.setAttribute( "href", "#" );
	link.onclick=function() {
		wnd = window.open( callbackEvent + "=openTemplate(" + template + ")", callbackWindow + "_cms" );
		wnd.focus();
		document.getElementById('cmscontextmenu').style.visibility='hidden';
		return false;
	}
	var image = document.createElement("img");
	image.setAttribute("src", webroot + "" + IMAGES + "editTemplate.gif" );
	image.setAttribute("border", "0");
	var text = document.createTextNode(locString);
	link.appendChild( image );
	link.appendChild( text );
	nobr.appendChild( link );
	currentCMSMenu.appendChild( nobr );
}

function addItemLinks( menu, locEditContent, locCopyContent, callbackEvent, callbackWindow, jaloPk, webroot ) {
	currentCMSMenu = menu;
	if( currentCMSMenu.hasChildNodes() ) {
		var br1 = document.createElement("br");
		currentCMSMenu.appendChild(br1);
	}
	

	var nobr = document.createElement("nobr");
	var link = document.createElement("a");
	link.setAttribute("href", "#");
	link.onclick=function() {
		wnd = window.open( callbackEvent + "=openItem(" + jaloPk + ")", callbackWindow + "_cms" );
		wnd.focus();
//		document.getElementById('cmscontextmenu').style.visibility='hidden';
		getMenuHTMLElement().style.visibility='hidden';
		return false;
	};
	
	var image = document.createElement("img");

	image.setAttribute("src", webroot+"" + IMAGES +"edit.gif");
	image.setAttribute("border", "0" );

	var text = document.createTextNode(locEditContent);
	link.appendChild( image );
	link.appendChild( text );
	nobr.appendChild( link );
	currentCMSMenu.appendChild( nobr );
	
	var br = document.createElement("br");
	currentCMSMenu.appendChild( br );
	
	var nobr2 = document.createElement("nobr");
	var link2 = document.createElement("a");
	link2.setAttribute( "href", "#" );
	link2.onclick=function() {
		wnd = window.open( callbackEvent + "=copyItem(" + jaloPk + ")", callbackWindow + "_cms");
		window.clipboardData.setData( "Text", "component://" + jaloPk )
//		document.getElementById('cmscontextmenu').style.visibility='hidden';
		getMenuHTMLElement().style.visibility='hidden';
		return false;
	 };
	
	var image2 = document.createElement("img");
	image2.setAttribute( "src", webroot+"" + IMAGES + "copy.gif");
	image2.setAttribute( "border", "0" );
	var text2 = document.createTextNode(locCopyContent);
	link2.appendChild( image2 );
	link2.appendChild( text2 );
	nobr2.appendChild( link2 );
	currentCMSMenu.appendChild( nobr2 );
	
}

function getScrollX() {
	var scrOfX = 0;
	if( typeof( window.pageYOffset ) == 'number' ) {
		//Netscape compliant
		scrOfX = window.pageXOffset;
	}
	else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
		//DOM compliant
		scrOfX = document.body.scrollLeft;
	}
	else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
		//IE6 standards compliant mode
		scrOfX = document.documentElement.scrollLeft;
	}
	return scrOfX;
}

function getScrollY() {
	var scrOfY = 0;
	if( typeof( window.pageYOffset ) == 'number' ) {
		//Netscape compliant
		scrOfY = window.pageYOffset;
	} else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
		//DOM compliant
		scrOfY = document.body.scrollTop;
	}
		else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
		//IE6 standards compliant mode
		scrOfY = document.documentElement.scrollTop;
	}
	return scrOfY;
}

