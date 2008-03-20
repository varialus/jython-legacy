package org.python.newcompiler;

public class GlobalEnvironment extends AbstractEnvironment {

    private boolean futureAllowed = true;

    public GlobalEnvironment() {
        super(null);
    }

    @Override
    public GlobalEnvironment getGlobalEnvironment() {
        return this;
    }

    public void addParameter(String name) {
        error(EnvironmentError.CANNOT_HAVE_PARAMETERS);
    }

    @Override
    public void markAsGlobal(String name) {/* dropped */}

    @Override
    public void addFuture(String feature) throws Exception {
        if (futureAllowed) {
            Future.addFeature(feature, flags);
        } else {
            super.addFuture(feature);
        }
    }
}
