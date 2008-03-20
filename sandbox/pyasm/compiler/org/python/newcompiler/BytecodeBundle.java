package org.python.newcompiler;

import java.io.File;

public interface BytecodeBundle {

    Class loadHandle(BytecodeLoader loader);

    Class saveFilesAndLoadHandle(BytecodeLoader loader, File dir);
}
