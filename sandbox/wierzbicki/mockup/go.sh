#!/bin/bash

#Very crude -- need to automate this.
rst2jysite --link-stylesheet --stylesheet=../css/html4css1.css Project/index.txt Project/index.html 
rst2jysite --link-stylesheet --stylesheet=../css/html4css1.css Project/differences.txt Project/differences.html 
rst2jysite --link-stylesheet --stylesheet=../css/html4css1.css Project/license.txt Project/license.html
rst2jysite --link-stylesheet --stylesheet=../css/html4css1.css Project/news.txt Project/news.html
rst2jysite --link-stylesheet --stylesheet=../css/html4css1.css Project/history.txt Project/history.html
rst2jysite --link-stylesheet --stylesheet=../css/html4css1.css Project/faq.txt Project/faq.html
rst2jysite --link-stylesheet --stylesheet=../css/html4css1.css Project/books_articles.txt Project/books_articles.html
rst2jysite --link-stylesheet --stylesheet=../css/html4css1.css Project/hacking.txt Project/hacking.html 
rst2jysite --link-stylesheet --stylesheet=../css/html4css1.css Project/contributors.txt Project/contributors.html 
rst2jysite --link-stylesheet --stylesheet=../css/html4css1.css Project/userdoc.txt Project/userdoc.html 
rst2jysite --link-stylesheet --stylesheet=../css/html4css1.css Project/bugs.txt Project/bugs.html 
rst2jysite --link-stylesheet --stylesheet=../css/html4css1.css Project/download.txt Project/download.html
rst2jysite --link-stylesheet --stylesheet=../css/html4css1.css Project/todo.txt Project/todo.html
