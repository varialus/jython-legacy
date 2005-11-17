#!/bin/bash

#Very crude -- need to automate this.
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/index.txt Project/index.html 
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/license.txt Project/license.html
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/news.txt Project/news.html
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/history.txt Project/history.html
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/faq.txt Project/faq.html
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/books_articles.txt Project/books_articles.html
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Jython2.1/index.txt Jython2.1/index.html 
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Jython2.2/index.txt Jython2.2/index.html 
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Community/index.txt Community/index.html 
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Community/contributors.txt Community/contributors.html 
