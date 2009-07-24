/*****************************************************************************************
          Script to import XML data files and make them available to JavaScript
                     v2.0.6 written by Mark Wilton-Jones, 13/04/2004
Updated 02/07/2004 to provide native Safari 1.2 and Opera 7.6 support using XMLHttpRequest
                   Updated 24/07/2004 to prevent a Safari caching bug
      Updated 02/10/2004 to include support for older Internet Explorer XML objects
 Updated 09/11/2004 to allow a delay for better response in browsers that use the iframe
               Updated 17/03/2007 to support ActiveX object used by Pocket IE
******************************************************************************************

Please see http://www.howtocreate.co.uk/jslibs/ for details and a demo of this script
Please see http://www.howtocreate.co.uk/tutorials/jsexamples/importingXML.html for a demo and description
Please see http://www.howtocreate.co.uk/jslibs/termsOfUse.html for terms of use

To use this, insert the following into the head of your document:

<script type="text/javascript"><!--
//for older browsers like Netscape 4 ... if you care
window.onerror = function () { return true; }
//--></script>
<script src="PATH TO SCRIPT/importxml.js" type="text/javascript"></script>

This header file provides one function:
var canItWork = importXML( string: locationOfXMLFile, string: nameOfFunction[, optional boolean: allowCache[, optional boolean: delay]] );
eg.
var canItWork = importXML( 'myXML.xml', 'runThis' );

To support (Internet) Explorer 5 on Mac, the XML file should use a stylesheet:
<?xml-stylesheet type="text/css" href="blank.css"?>
Although that stylesheet could in fact be completely empty. Failure to do this will produce errors when you
try to manipulate the DOM of the XML file.

When the xml file has loaded, the named function will be run, and will be passed a reference to the document
object of the XML file. You can then manipulate the data in the file using W3C DOM scripting.

Browsers may cache the XML files (with Safari, the import fails if the file is already cached by the current page).
To prevent this, the script adds a timestamp to the end of each request URL (changes every millisecond).
If you do not want this timestamp to be added, pass the value 'true' as a third parameter.
var canItWork = importXML( 'myXML.xml', 'runThis', true );
This is not recommended.

Browsers that use the iframe may have problems if the XML takes a long time to load, as they will attempt to
access the data before it is ready. If you know that this might happen, you can use the delay parameter to
make the script wait for the specified amount of time before attemting to use the data, hopefully giving the
XML the chance to load. For example, to introduce a 2 second delay:
var canItWork = importXML( 'myXML.xml', 'runThis', false, 2000 );
_______________________________________________________________________________________*/

var MWJ_ldD = [];

function importXML( oURL, oFunct, oNoRand, oDelay ) {
	//note: in XML importing event handlers, 'this' refers to window
	if( !oNoRand ) { oURL += ( ( oURL.indexOf('?') + 1 ) ? '&' : '?' ) + ( new Date() ).getTime(); } //prevent cache
	if( window.XMLHttpRequest ) {
		//alternate XMLHTTP request - Gecko, Safari 1.2+ and Opera 7.6+
		alert ("here");
		MWJ_ldD[MWJ_ldD.length] = new XMLHttpRequest();
		MWJ_ldD[MWJ_ldD.length-1].onreadystatechange = new Function( 'if( MWJ_ldD['+(MWJ_ldD.length-1)+'].readyState == 4 && MWJ_ldD['+(MWJ_ldD.length-1)+'].status < 300 ) { '+oFunct+'(MWJ_ldD['+(MWJ_ldD.length-1)+'].responseXML); }' );
		MWJ_ldD[MWJ_ldD.length-1].open("GET", oURL, true);
		MWJ_ldD[MWJ_ldD.length-1].send(null);
		return true;
	}
	if( !navigator.__ice_version && window.ActiveXObject ) {
		//the Microsoft way - IE 5+/Win (ICE produces errors and fails to use try-catch correctly)
		var activexlist = ['Microsoft.XMLHTTP','Microsoft.XMLDOM'], tho; //add extra progids if you need specifics
		for( var i = 0; !tho && i < activexlist.length; i++ ) {
			try { tho = new ActiveXObject( activexlist[i] ); } catch(e) {}
		}
		if( tho ) {
			MWJ_ldD[MWJ_ldD.length] = tho;
			MWJ_ldD[MWJ_ldD.length-1].onreadystatechange = new Function( 'if( MWJ_ldD['+(MWJ_ldD.length-1)+'].readyState == 4 ) { '+oFunct+'(MWJ_ldD['+(MWJ_ldD.length-1)+'].load?MWJ_ldD['+(MWJ_ldD.length-1)+']:MWJ_ldD['+(MWJ_ldD.length-1)+'].responseXML); }' );
			if( MWJ_ldD[MWJ_ldD.length-1].load ) {
				MWJ_ldD[MWJ_ldD.length-1].load(oURL);
			} else {
				MWJ_ldD[MWJ_ldD.length-1].open('GET', oURL, true);
				MWJ_ldD[MWJ_ldD.length-1].send(null);
			}
			return true;
		}
	}
	if( document.createElement && document.childNodes ) {
		//load the XML in an iframe
		var ifr = document.createElement('DIV');
		ifr.style.visibility = 'hidden'; ifr.style.position = 'absolute'; ifr.style.top = '0px'; ifr.style.left = '0px';
		//onload only fires in Opera so I use a timer for all
		if( !window.MWJ_XML_timer ) { window.MWJ_XML_timer = window.setInterval('MWJ_checkXMLLoad();',100); }
		ifr.innerHTML = '<iframe src="'+oURL+'" name="MWJ_XML_loader_'+MWJ_ldD.length+'" height="0" width="0"><\/iframe>';
		MWJ_ldD[MWJ_ldD.length] = oFunct+'MWJ_SPLIT'+(oDelay?oDelay:1)+'';
		document.body.appendChild(ifr);
		return true;
	}
	return false;
}

function MWJ_checkXMLLoad() {
	//check if each imported file is available (huge files may not have loaded completely - nothing I can do - use the delay to help)
	for( var x = 0; x < MWJ_ldD.length; x++ ) { if( MWJ_ldD[x] && window.frames['MWJ_XML_loader_'+x] ) {
		setTimeout( MWJ_ldD[x].split('MWJ_SPLIT')[0] + '(window.frames.MWJ_XML_loader_'+x+'.window.document);', parseInt(MWJ_ldD[x].split('MWJ_SPLIT')[1]) );
		MWJ_ldD[x] = false;
	} }
}

/********************************************************************************************************
                                  Script to parse RSS/Atom XML data files
                                        Written by Mark Wilton-Jones
                            version 3.1.0 updated 12/10/2005 to add OPML support

Please see http://www.howtocreate.co.uk/jslibs/ for details and a demo of this script
Please see http://www.howtocreate.co.uk/tutorials/jsexamples/rss.html for a demo and description
Please see http://www.howtocreate.co.uk/jslibs/termsOfUse.html for terms of use
********************************************************************************************************/

//display image enclosures: 0 = none, 1 = inside link to item, 2 = after item description
var displayEnclosures = 0;

//display atom links: 0 = before item description, 1 = after item description
var displayAtomLinks = 1;

function cloneTree(oFrom,oTo) { try {
	for( var i = 0, n; i < oFrom.childNodes.length; i++ ) {
		n = oFrom.childNodes[i];
		if( n.tagName ) {
			var oTag = document.createElement(n.tagName);
			oTo.appendChild(oTag);
			for( var j = 0; j < n.attributes.length; j++ ) {
				try { oTag.setAttribute(n.attributes[j].nodeName,n.attributes[j].nodeValue); } catch(e) {}
			}
			cloneTree(n,oTag);
		} else if( n.nodeType == 3 ) {
			oTo.appendChild(document.createTextNode(n.nodeValue));
		}
	}
} catch(e) { alert(oFrom.tagName+' '+n.nodeValue); throw(e); } }
function prepHTMLCode(oFrom) {
	return oFrom.replace(/&/g,'&amp;').replace(/"/g,'&quot;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}
function getUnNestedTextNodes(oFrom) {
	var oStr = '';
	for( var i = 0; i < oFrom.childNodes.length; i++ ) {
		if( ( oFrom.childNodes[i].nodeType == 3 ) || ( oFrom.childNodes[i].nodeType == 4 ) ) { oStr += oFrom.childNodes[i].nodeValue; }
	}
	return oStr;
}
function parseRSS(oDocObj) {
         alert(oDocObj);
	//check if the browser interpreted the XML correctly
	if( oDocObj.documentElement && ( !oDocObj.documentElement.tagName || ( oDocObj.documentElement.tagName && oDocObj.documentElement.tagName.toUpperCase() == 'HTML' ) ) ) {
		setTimeout('alert(\'For no apparent reason, your browser has turned the RSS feed into HTML based garbage.\\nScript aborted.\');',50); return; }
	var chanEl = oDocObj.getElementsByTagName('channel')[0];
	if( !chanEl ) { chanEl = oDocObj.getElementsByTagName('atomSpoof')[0]; }

	var RSSversion = oDocObj.getElementsByTagName('rssSpoof')[0] ? oDocObj.getElementsByTagName('rssSpoof')[0].getAttribute('version') : ( oDocObj.getElementsByTagName('atomSpoof')[0] ? ( 'atomfeed' + oDocObj.getElementsByTagName('atomSpoof')[0].getAttribute('version') ) : '' );

	//get the information about the feed
	for( var x = 0, y, feedInfo = [], tagName2, tagName3; x < chanEl.childNodes.length; x++ ) {
		y = chanEl.childNodes[x];
		if( y.tagName ) {
			tagName2 = y.tagName.toLowerCase();
			if( tagName2 == 'fiximage' ) {
				feedInfo['image'] = [];
				for( var i = 0, j; i < y.childNodes.length; i++ ) {
					j = y.childNodes[i];
					if( j.tagName ) {
						tagName3 = j.tagName.toLowerCase();
						feedInfo['image'][tagName3] = getUnNestedTextNodes(j);
						if( RSSversion.match(/0?\.91/) ) { feedInfo['image'][tagName3] = prepHTMLCode(feedInfo['image'][tagName3]); }
					}
				}
			} else if( tagName2 != 'item' && tagName2 != 'entry' ) {
				if( tagName2 != 'fixlink' || !y.getAttribute('rel') ) {
					feedInfo[tagName2] = getUnNestedTextNodes(y);
					if( RSSversion.match(/0?\.91/) ) { feedInfo[tagName2] = prepHTMLCode(feedInfo[tagName2]); }
				}
			}
		}
	}

	if( !feedInfo['lastbuilddate'] ) { feedInfo['lastbuilddate'] = feedInfo['pubdate']; }

	//parse each news item
	var y = oDocObj.getElementsByTagName('item');
	if( !y.length ) { y = oDocObj.getElementsByTagName('entry'); }
	for( var x = 0, newsItems = []; x < y.length; x++ ) {
		newsItems[x] = [];
		newsItems[x]['links'] = {alternate:[],related:[],via:[]};
		newsItems[x]['content'] = '';
		for( var i = 0, j, theRel; i < y[x].childNodes.length; i++ ) {
			j = y[x].childNodes[i];
			if( j.tagName ) {
				tagName2 = j.tagName.toLowerCase();
				theRel = j.getAttribute('rel');
				if( theRel ) { theRel = theRel.toLowerCase(); }
				theType = j.getAttribute('type');
				if( theType ) { theType = theType.toLowerCase(); }
				if( tagName2 == 'enclosure' || ( ( tagName2 == 'fixlink' ) && ( theRel == 'enclosure' ) ) ) {
					if( !newsItems[x]['enclosure'] ) { newsItems[x]['enclosure'] = []; }
					newsItems[x]['enclosure'][newsItems[x]['enclosure'].length] = [];
					for( var k = 0, l, atn; l = j.attributes[k]; k++ ) {
						atn = l.nodeName.toLowerCase();
						if( atn == 'href' ) { atn = 'url'; }
						newsItems[x]['enclosure'][newsItems[x]['enclosure'].length-1][atn] = l.nodeValue;
					}
				} else if( tagName2 == 'fixlink' ) {
					if( !theRel || ( theRel == 'image' ) ) {
						newsItems[x]['links']['image']= j.getAttribute('href');
					} else if( !theRel || ( theRel == 'alternate' ) ) {
						newsItems[x]['links']['alternate'][0] = prepHTMLCode( j.firstChild ? getUnNestedTextNodes(j) : j.getAttribute('href') );
					} else if( ( theRel == 'via' ) || ( theRel == 'related' ) ) {
						newsItems[x]['links'][theRel][newsItems[x]['links'][theRel].length] = [prepHTMLCode(j.getAttribute('href')),prepHTMLCode(j.getAttribute('title'))];
					}
				} else if( tagName2 == 'author' ) {
					newsItems[x]['author'] = getUnNestedTextNodes(j.getElementsByTagName("name")[0]);
					newsItems[x]['authoruri'] = getUnNestedTextNodes(j.getElementsByTagName("uri")[0]);
				} else if( tagName2 == 'summary' ) {
					if( !theType || ( theType == 'text' ) || theType.match(/^text\/plain$/) ) {
						newsItems[x]['description'] = prepHTMLCode(getUnNestedTextNodes(j));
					} else if( ( theType == 'html' ) || ( theType == 'text\/html' ) ) {
						newsItems[x]['description'] = getUnNestedTextNodes(j);
					} else if( ( theType == 'xhtml' ) || theType.match(/^application\/.*\+xml$/) ) {
						var foo = document.createElement('div');
						cloneTree(j,foo);
						newsItems[x]['description'] = foo.innerHTML;
					}
				} else if( tagName2 == 'content' ) {
					if( !theType || ( theType == 'text' ) || theType.match(/^text\/plain$/) ) {
						newsItems[x]['content'] += (newsItems[x]['content']?'<br>':'')+prepHTMLCode(getUnNestedTextNodes(j));
					} else if( ( theType == 'html' ) || ( theType == 'text\/html' ) ) {
						newsItems[x]['content'] += (newsItems[x]['content']?'<br>':'')+getUnNestedTextNodes(j);
					} else if( ( theType == 'xhtml' ) || theType.match(/^application\/.*\+xml$/) ) {
						var foo = document.createElement('div');
						cloneTree(j,foo);
						newsItems[x]['content'] += (newsItems[x]['content']?'<br>':'')+foo.innerHTML;
					}
				} else {
					newsItems[x][tagName2] = getUnNestedTextNodes(j);
					if( RSSversion.match(/0?\.91/) ) { newsItems[x][tagName2] = prepHTMLCode(newsItems[x][tagName2]); }
				}
			}
		}
	}

	//now that the entire feed has been converted into JavaScript arrays, it is time to write it out
	document.title = 'Jython Twitter Updates';

	//headers
	var oFeedStr = '<h2><a href="feed://search.twitter.com/search.atom?q=%23jython">Jython Twitter Updates<\/a><\/h2><dl>\n';

	//items
	for( var x = 0, y; y = newsItems[x]; x++ ) {
		var published = "<br>", image = "", author = "", name = "", alias = "";
		if (y['published'])
		{
			published = y['published'];
			published = published.replace("T", " ");
			published = published.substring(0, published.length - 4);
		}
		else if (y['updated'])
		{
			published = y['updated'];
			published = published.replace("T", " ");
			published = published.substring(0, published.length - 4);
		}
		if (y['author'])
		{
			var name = y['author'];
			var pos = name.indexOf("(");
			var alias = name.substring(0, pos - 1);
			name = name.substring(pos + 1, name.length - 1);
		}
		if (y['links'] && y['links']['image'])
		{
			image = '<img src="' + y['links']['image'] + '" title="' + name +'">';
		}
		if (y['authoruri'])
		{
			alias = '<a class="author" href="' + y['authoruri'] + '" title="' + name +'">' + alias + '</a>: ';
		}
		oFeedStr += '<dt>' + image + alias + '<a href="'+y['links']['alternate']+'">';
		oFeedStr += prepHTMLCode(y['title'])+'<\/a><\/dt>';
		oFeedStr += '<dd>'+published+'<\/dd>';
	}

	//footer
	oFeedStr += '<\/dl>';

	//output
	document.getElementById('feedcontainer').innerHTML = oFeedStr;
}