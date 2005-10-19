package org.python.util.install;

public interface ProgressListener {

    public int getInterval();

    public void progressChanged(int newPercentage);

    public void progressEntry(String entry);

    public void progressStartScripts();

    public void progressFinished();
}