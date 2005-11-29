#!/bin/bash

#Very crude -- need to automate this.
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/index.txt Project/index.html 
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/license.txt Project/license.html
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/news.txt Project/news.html
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/history.txt Project/history.html
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/faq.txt Project/faq.html
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/books_articles.txt Project/books_articles.html
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/hacking.txt Project/hacking.html 
python ../../../docutils/tools/rst2jysite.py --link-stylesheet --stylesheet=../css/html4css1.css Project/contributors.txt Project/contributors.html 
