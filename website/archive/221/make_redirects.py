import os
os.mkdir("out")

wikinames = {
    "about" : "AboutJython",
    "books_articles" : "BooksAndArticles",
    "bugs" : "ReportingBugs",
    "devfaq" : "DeveloperFAQ",
    "download" : "DownloadingJython",
    "history" : "JythonHistory",
    "index" : "",
    "installation" : "InstallingJython",
    "jythonc" : "JythoncCompiler",
    "news" : "",
    "userfaq" : "UserFAQ",
    "userguide" : "UserGuide",
    }

def wikiname(name):
    try:
        return wikinames[name]
    except KeyError:
        return name.capitalize()

def process(filename):
    name = os.path.splitext(filename)[0] 
    out = open(os.path.join("out", name + ".txt"), 'w')
    print >> out, "This page has been retired for versions of Jython older than 2.1."
    print >> out
    print >> out, "If you want the archived web page:"
    print >> out
    print >> out, "`2.1 accu04-scripting/%s`_" % name
    print >> out
    #print >> out, ".. _%s: http://wiki.python.org/jython/%s" % (name, wikiname(name))
    #print >> out
    print >> out, ".. _2.1 accu04-scripting/%s: ../archive/21/accu04-scripting/%s.html" % (name, name)

    out.close()

for filename in os.listdir("."):
    process(filename)

