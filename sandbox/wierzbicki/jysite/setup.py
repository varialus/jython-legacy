#!/usr/bin/env python

from distutils.core import setup

def do_setup():
    dist = setup(
        name='jython-site-writer',
        description='A HTML writer for constructing the jython site (w/navigation)',
        url='http://jython.sourceforg.net/sandbox/wierzbicki/jysite/',
        version='0.1',
        author='Frank Wierzbicki',
        author_email='fwierzbicki@gmail.com',
        license='Public Domain',
        packages=['docutils.writers'],
        package_dir={'docutils.writers':'writer'},
        scripts=['rst2jysite.py']
    )
    return dist

if __name__ == '__main__':
    do_setup()
