# Author: Gunnar Schwant
# Contact: g.schwant@gmx.de
# Revision: $Revision$
# Date: $Date$
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
        navtop = os.path.join(os.path.split(self.settings._destination)[0],
                              'top.nav')
        navleft = '%s%s' % (os.path.splitext(self.settings._destination)[0],
                            '.nav')
        if not os.path.exists(navleft):
            navleft = os.path.join(os.path.split(self.settings._destination)[0],
                                   'left.nav')
        if os.path.exists(navleft):
            buffer = []
            buffer.append('<link type="text/css" href="../css/basic.css" rel="stylesheet">')
            buffer.append('<link media="screen" type="text/css" href="../css/screen.css" rel="stylesheet"> ')
            buffer.append('<link media="print" type="text/css" href="../css/print.css" rel="stylesheet"> ')
            buffer.append('<link type="text/css" href="../css/profile.css" rel="stylesheet"> ')
            buffer.append('<!--[if lt IE 7]>')
            buffer.append('<script defer type="text/javascript" src="../css/pngfix.js"></script>')
            buffer.append('<![endif]-->')
            buffer.append('</head>')
            buffer.append('<body>')
            buffer.append('<div id="top">')
            buffer.append('<div class="header">')
            buffer.append('<div class="grouplogo">')
            buffer.append('<a href="./" ><img class="logoImage" alt="Jython" src="../css/jython.png" title="Jython"></a>')
            buffer.append('</div>')

            #searchbox
            buffer.append('<div class="searchbox">')
            buffer.append('<form action="http://www.google.com/search" method="get" class="roundtopsmall">')
            buffer.append('''<input value="www.jython.org" name="sitesearch" type="hidden"><input size="25" name="q" id="query" type="text">&nbsp; ''')
            buffer.append('                    <input name="Search" value="Search" type="submit">')
            buffer.append('</form>')
            buffer.append('</div>')

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
            buffer.append('<div id="main"><div id="menu"><div class="menupage">')
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
                    buffer.append('<div class="menupagetitle">%s</div>' % val[1])
                elif val[0] == 'raw':
                    buffer.append(val[1])
                elif val[0] == 'external':
                    buffer.append('<div class="%s">' \
                                  '<a target="_blank" href="%s"><img src="../css/moin-www.png" />%s</a></div> ' % (val[0], val[2], val[1]))
                elif val[0] == 'image':
                    buffer.append('<div class="menupageitem">' \
                                  '<a target="_blank" href="%s"><img src="%s" /></a></div> ' % (val[2], val[1]))
                else:
                    buffer.append('<div class="%s">' \
                                  '<a href="%s">%s</a></div> ' % (val[0], val[2], val[1]))
            buffer.append('</div>')
            buffer.append('</div>')
            buffer.append('</div>')
            buffer.append('<div id="content">')
        else:
            raise Exception("%s does not exisi." % navleft)
        return "\n".join(buffer)

    def get_body_suffix(self):
        return '\n</div></div></body>\n'
