This sandbox contains Tobias projects. Most notably the "advanced" compiler.

Here double version control systems are used. Subversion is used at the base.
All sub projects are managed in subversion. The main Jython code is linked
using svn:externals.
Mercurial is used for managing patches on the main Jython code.
For this to work a few things needs to be maintained:
 * The .hgignore file should contain all subprojects. Only linked code should
   be visible to mercurial.
 * Subversion is set to ignore everything in the .hg directory except for the
   patches directory where Mercurial Patch Queues on the main Jython code goes.
 * When starting a new working copy all the files needs to be added to hg,
   this is because the entire hg repository is NOT stored in subversion,
   only the patches.

Hopefully this setup will decrease the overhead of branch management. This by
eliminating the need for merges from trunk, since there is no branch. And
making merge back a simple commit (with patches applied).

Further information about each sub project can be found in the README files
in the directory for each sub project respectively.
