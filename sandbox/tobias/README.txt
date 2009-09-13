This sandbox contains Tobias projects. Most notably the "advanced" compiler.

The most recent version can be found at:
    https://jython.svn.sourceforge.net/svnroot/jython/trunk/sandbox/tobias

Here double version control systems are used. Subversion is used at the base.
All sub projects are managed in subversion. The main Jython code is linked
using svn:externals.
Mercurial is used for managing patches on the main Jython code.
Subversion is set to ignore everything in the .hg directory except for the
patches directory where Mercurial Patch Queues on the main Jython code goes.
To be able to use Mercurial Patch Queues the first thing you need to do after
checking out this sandbox is to add all source files to hg. This can be done by
executing the "initialize" Ant target. This is needed because the full hg
repository is NOT stored in subversion, only the patches.

Hopefully this setup will decrease the overhead of branch management. This by
eliminating the need for merges from trunk, since there is no branch. And
making merge back a simple commit (with patches applied).

Further information about each sub project can be found in the README files
in the directory for each sub project respectively.
