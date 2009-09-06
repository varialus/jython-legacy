Topic: How to build the jython.org site
Author: J. Juneau
Date: 09/06/2009
------------------------------------------

1)  Install docutils
     -  I use Python 2.6 with docutils 0.5

2)  Ensure you have ant installed
     - I use version 1.7.1

3)  Place a copy of jysite.py into your docutils "writers" directory, this is basically the website template.

4)  Traverse into the "website" directory and issue the "ant" command to build
     - If the dist directory already resides, you may need to delete it before building
       to get a fresh build.
       
5)  Newly built site will reside within the "dist" directory.


Changing jython.org
-------------------

The jython site resides on the Python.org server at "dinsdale.python.org" within the /data/jython/ directory.  The /data/jython/ directory is the root of the site and that is where index.html resides.
All other pages also reside within the /data/jython/ directory.  This directory also contains other directories such as CSS and the like.

To deploy the site, you must have commit privileges to dinsdale.python.org.  Once you've been granted commit, you can deploy the site by simply copying the contents of the "dist" directory
on your machine into the /data/jython/ directory on the server as follows:

scp -r dist/* username@dinsdale.python.org:/data/jython/

The -r flag will ensure overwriting of any existing files.


Design and Layout
--------------------------

Template:

Site template is the jysite.py writer file.  This file contains the header, footer, left, and right sides of each webpage contained within the site.  If you are interested in adding anything to the side(s) or header/footer then it will need to be done here.


CSS and Javascript:

All css resides within the CSS and Javascript files reside within the CSS directory.  The file named "newstyle.css" contains the styles for the site.  The CSS directory also contains some images for the site.

Changing Text:

    - Home Page:  To change the home page text "Latest News", modify the index.txt  file residing in the "website" directory using restructuredText format.  

    - All Other Pages:  All other page content resides within the "redirects" directory.  To change the text of any other page on the site, open the file whose name corresponds to the site page and change the text using restructuredText format.

Adding Pages:

    - To add a new page to the site, simply create a file within the "redirects" directory and name it corresponding to the page name you'd like to create.  Use restructuredText to create the page text.
    