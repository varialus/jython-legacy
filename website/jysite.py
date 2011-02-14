# Author: Gunnar Schwant
# Contact: g.schwant@gmx.de
# Revision: $Revision: 2822 $
# Date: $Date: 2006-06-14 21:05:15 -0500 (Wed, 14 Jun 2006) $
# Copyright: This module has been placed in the public domain.

from docutils import writers, nodes, languages
import docutils, html4css1, os

class Writer(html4css1.Writer):

    def __init__(self):
        writers.Writer.__init__(self)
        self.translator_class = HTMLTranslator

class HTMLTranslator(html4css1.HTMLTranslator):

    def __init__(self, document):
        html4css1.HTMLTranslator.__init__(self, document)
        self.navigation_bgcolor = '#FFFFFF'
        self.body_prefix = [self.get_body_prefix()]
        self.body_suffix = [self.get_body_suffix()]

    def get_body_prefix(self):
        buffer = []
        script_twitter = '''<script src="css/twitter-1.12.2.js" type="text/javascript" charset="utf-8"></script>
                <script type="text/javascript" charset="utf-8">
                getSearchTwitters('tweet', { 
                  id: 'jython', 
                  count: 10, 
                  enableLinks: true, 
                  ignoreReplies: false, 
                  clearContents: true,
                  template: '%text%'
                });
                </script>'''
        script_extra = '''<script type="text/javascript">
        ddaccordion.init({
	headerclass: "silverheader", //Shared CSS class name of headers group
	contentclass: "submenu", //Shared CSS class name of contents group
	revealtype: "mouseover", //Reveal content when user clicks or onmouseover the header? Valid value: "click", "clickgo", or "mouseover"
	mouseoverdelay: 200, //if revealtype="mouseover", set delay in milliseconds before header expands onMouseover
	collapseprev: true, //Collapse previous content (so only one open at any time)? true/false
	defaultexpanded: [0], //index of content(s) open by default [index1, index2, etc] [] denotes no content
	onemustopen: true, //Specify whether at least one header should be open always (so never all headers closed)
	animatedefault: false, //Should contents open by default be animated into view?
	persiststate: true, //persist state of opened contents within browser session?
	toggleclass: ["", "selected"], //Two CSS classes to be applied to the header when it's collapsed and expanded, respectively ["class1", "class2"]
	togglehtml: ["", "", ""], //Additional HTML added to the header when it's collapsed and expanded, respectively  ["position", "html1", "html2"] (see docs)
	animatespeed: "fast", //speed of animation: integer in milliseconds (ie: 200), or keywords "fast", "normal", or "slow"
	oninit:function(headers, expandedindices){ //custom code to run when headers have initalized
		//do nothing
	},
	onopenclose:function(header, index, state, isuseractivated){ //custom code to run whenever a header is opened or closed
		//do nothing
	}
            }) 
        </script>'''
        buffer.append('<link type="text/css" href="css/newstyle.css" rel="stylesheet"> ')
        buffer.append('<!--[if lt IE 7]>')
        buffer.append('<script defer type="text/javascript" src="css/pngfix.js"></script>')
        buffer.append('<![endif]-->')
        buffer.append(script_twitter)
        buffer.append('<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>')
        buffer.append('<script type="text/javascript" src="css/ddaccordion.js">')
        buffer.append('/***********************************************')
        buffer.append('* Accordion Content script- (c) Dynamic Drive DHTML code library (www.dynamicdrive.com)')
        buffer.append('* Visit http://www.dynamicDrive.com for hundreds of DHTML scripts')
        buffer.append('* This notice must stay intact for legal use')
        buffer.append('***********************************************/')
        buffer.append('</script>')
        buffer.append(script_extra)

        buffer.append('</head>')
        buffer.append('<body>')
        buffer.append('<div id="container">')
        buffer.append('<div id="top">')
        buffer.append('<div id="header">')
        buffer.append('<div id="grouplogo">')
        buffer.append('<a href="./" ><img class="logoImage" alt="Jython" style="border: 0px; padding-top: 20px; position:absolute; left: 35px" src="css/jython.png" title="Jython"></a>')
        buffer.append('</div>')
        buffer.append('<div class="latest_release" style="position:absolute; color:#000; width:180px; top: 15px; right: 30px; padding:0px 10px 10px 30px; font-size:11px; background:url(\'css/latest_release_bg.png\') no-repeat">')
        buffer.append('<p style="top: 25px; color:#000">Latest release - 2.5.2 - RC 4<br/>')
        buffer.append('<a style="color:#000" href="latest.html">View Release Notes</a><br/>')
        buffer.append('Download: <a style="color:#000" href="http://sourceforge.net/projects/jython/files/jython-dev/2.5.2rc4/jython_installer-2.5.2rc4.jar/download">.jar</a>')
        buffer.append('</div>')
        
        buffer.append('<div class="searchbox">')
      #  buffer.append('<label class="header" for="searchbox">Search</label>')
        buffer.append('<form action="http://www.google.com/search" method="get" class="roundtopsmall">')
        buffer.append('''<input value="www.jython.org" name="sitesearch" type="hidden"><input size="25" id="searchbox" name="q" id="query" type="text">&nbsp; ''')
        buffer.append('                    <input name="Search" value="Search" type="submit">')
        buffer.append('</form></p>')
        buffer.append('</div>')
        
        buffer.append('</div></div>')
         #searchbox
        buffer.append('<h2 id="topper">Jython: Python for the Java Platform</h2>')
        buffer.append('<div id="wrapper">')

        buffer.append('<div id="content">')

        return "\n".join(buffer)

    def get_body_suffix(self):
        buffer = self.get_navigation()
        return buffer
    
    def get_navigation(self):
        navtop = os.path.join(os.path.split(self.settings._destination)[0],
                              'top.nav')
        navleft = '%s%s' % (os.path.splitext(self.settings._destination)[0],
                            '.nav')
        if not os.path.exists(navleft):
            navleft = os.path.join(os.path.split(self.settings._destination)[0],
                                   'left.nav')
        if os.path.exists(navleft):
            buffer = []

            if os.path.exists(navtop):

                buffer.append('<ul id="tabs">')
                f = open(navtop, 'rt')
                lines = f.readlines()
                f.close()
                for line in lines:
                    val = line.split('|')
                    for i in range(len(val)):
                        val[i] = val[i].strip()
                    if val[0] == 'logo':
                        pass
                    else:
                        buffer.append('<li class="%s"><a href="%s" >%s</a></li>' \
                                      % (val[0], val[2], val[1]))
                buffer.append('</ul>')
            buffer.append('</div>')
            buffer.append('</div>')
            f = open(navleft, 'rt')
            lines = f.readlines()
            f.close()
            buffer.append('<div id="navigation"><div class="navcontainer"><ul class="navlist">')
            firstSection = True
            for line in lines:
                if line.startswith('#'):
                  continue
                val = line.split('|')
                for i in range(len(val)):
                    val[i] = val[i].strip()
                if val[0] == 'section':
                    if firstSection:
                        firstSection = False
                    else:
                        pass
                        #Maybe add menupagegroup here?
                        #buffer.append('</ul>')
                    buffer.append('<li class="menutitle">%s</li>' % val[1])
                elif val[0] == 'raw':
                    buffer.append(val[1])
                elif val[0] == 'external':
                    buffer.append('<li class="%s">' \
                                  '<a target="_blank" href="%s"><img src="css/moin-www.png" />%s</a></li> ' % (val[0], val[2], val[1]))
                elif val[0] == 'image':
                    buffer.append('<li class="menupageitem">' \
                                  '<a target="_blank" href="%s"><img src="%s" /></a></li> ' % (val[2], val[1]))
                else:
                    buffer.append('<li class="%s">' \
                                  '<a href="%s">%s</a></li> ' % (val[0], val[2], val[1]))
            buffer.append('</ul>')
            buffer.append('</div>')

            buffer.append('</div>')
            
            buffer.append('<div id="extra">')
	    buffer.append('<div class="applemenu">')
            buffer.append('<div class="silverheader"><a>Twitter Updates</a></div>')
            buffer.append('<div class="submenu"  style="height: 375px;">')
            buffer.append('<a style="font-size:8pt; color: green;" href="http://www.twitter.com/jython">Follow Jython</a><br/><br/>')
            buffer.append('<div id="tweet">')

            buffer.append('<br/>')

            buffer.append('</div></div>')
            buffer.append('<div class="silverheader"><a>Using Jython</a></div>')
            buffer.append('<div class="submenu">')
            buffer.append('<iframe src="faq.htm" style="border: 0px; width:100%;height:100%"></iframe>')
            buffer.append('</div>')
            buffer.append('<div class="silverheader"><a>IRC Chat</a></div>')
            buffer.append('<div class="submenu">')
            buffer.append('<iframe style="border: 0px; width: 100%; height: 100%" src="JythonIrcLogin.html"></iframe>')
            buffer.append('</div>')
            buffer.append('<div class="silverheader"><a>Developer Information</a></div>')
            buffer.append('<div class="submenu">')
            buffer.append('<iframe src="developer.htm" style="border: 0px; width:100%; height:100%"></iframe>')
            buffer.append('</div>')
            
            buffer.append('</div>')
            buffer.append('<br/><br/>')

            
           
            buffer.append('</div>')
            buffer.append('<div id="footer">')
            buffer.append('<ul><li><a href="http://wiki.python.org/jython/JythonFaq">About</a></li>')
            buffer.append('<li><a href="http://www.jythonpodcast.com">Jython Podcast</a></li>')
            buffer.append('<li><a href="license.html">License</a>')
            buffer.append('</ul>')
            buffer.append('</div>')
            buffer.append('</div></body>')
        else:
            print 'Does Not Exist'
        return "\n".join(buffer)
