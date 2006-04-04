package org.python.util.install.driver;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class ConsoleAutotest extends SilentAutotest {

    private Collection _answers;

    protected ConsoleAutotest(File targetDir) {
        super(targetDir);
        _answers = new ArrayList(50);
    }

    protected void addAnswer(String answer) {
        _answers.add(answer);
    }

    protected Collection getAnswers() {
        return _answers;
    }

}
