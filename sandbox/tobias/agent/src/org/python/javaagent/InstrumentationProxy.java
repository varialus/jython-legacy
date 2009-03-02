package org.python.javaagent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

/**
 * @see org.python.javaagent.DeferringAgent
 * 
 * @author Tobias Ivarsson
 */
public final class InstrumentationProxy {

    private static volatile InstrumentationProxy proxy;
    private final Instrumentation instrumentation;
    private static final Runnable attacher = new Runnable() {
        public void run() {
            // TODO: add means to attach an agent to this, already running, JVM
        }
    };

    private InstrumentationProxy() {
        Instrumentation value;
        try {
            Class<?> agentClass = Class.forName("org.python.javaagent.DeferringAgent");
            Method getMethod = agentClass.getMethod("getInstrumentation",
                    Runnable.class);
            value = (Instrumentation) getMethod.invoke(null, attacher);
        } catch (Exception e) {
            value = null;
        }
        instrumentation = value;
    }

    /**
     * Retrieve an {@link Instrumentation} instance for the current JVM.
     * 
     * @return The instrumentation associated with this agent.
     */
    public static Instrumentation getInstrumentation() {
        InstrumentationProxy instance = proxy;
        if (instance == null) {
            synchronized (InstrumentationProxy.class) {
                instance = proxy;
                if (instance == null) {
                    instance = new InstrumentationProxy();
                    proxy = instance;
                }
            }
        }
        return instance.instrumentation;
    }

}
