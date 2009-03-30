package org.python.frame;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.python.core.FrameAccessor;
import org.python.core.PyBaseCode;
import org.python.core.PyBaseFrame;
import org.python.core.PyObject;
import org.python.core.TraceFunction;
import org.thobe.frame.Frame;

public class JavaFrameAccessor extends FrameAccessor {
    @Override
    protected PyBaseFrame getFrame() {
        List<Frame> python_frames = new LinkedList<Frame>();
        for (Frame frame : Frame.getFrames()) {
            if (hasPythonFrame(frame.getMethod())) {
                python_frames.add(frame);
            }
        }
        return buildStack(python_frames);
    }

    private boolean hasPythonFrame(Method method) {
        StringBuilder frame = new StringBuilder(method.getReturnType().getName());
        frame.append(" ");
        frame.append(method.getDeclaringClass().getName());
        frame.append("#");
        frame.append(method.getName());
        String sep = "(";
        for (Class<?> param : method.getParameterTypes()) {
            frame.append(sep);
            frame.append(param.getName());
            sep = ",";
        }
        if (sep.equals("(")) frame.append("(");
        frame.append(")");
        System.out.println(frame);
        // TODO Auto-generated method stub
        return false;
    }

    private PyBaseFrame buildStack(Iterable<Frame> frames) {
        DelegatingFrame result = null;
        for (Frame frame : frames) {
            result = new DelegatingFrame(frame, result);
        }
        return result;
    }

    JavaFrameAccessor() {
    }

    @Override
    protected void setFrame(PyBaseFrame frame) {
        System.out.println("=== SET FRAME ===");
        new Throwable().printStackTrace();
        // do nothing
    }

    public static final class Factory extends FrameAccessor.Factory {
        @Override
        protected FrameAccessor createAccessor() {
            return new JavaFrameAccessor();
        }

        @Override
        protected boolean isAvailable() {
            if (true) return false; // NOTE: this is here until this thing works
            try {
                Class<?> frameclass = Class.forName("org.thobe.frame.Frame");
                return (Boolean) frameclass.getMethod("isAvailable").invoke(
                        null);
            } catch (Throwable e) {
                return false;
            }
        }
    }

    private static class DelegatingFrame extends PyBaseFrame {
        private final Frame frame;
        private final DelegatingFrame f_back;

        DelegatingFrame(Frame frame, DelegatingFrame f_back) {
            this.frame = frame;
            this.f_back = f_back;
        }

        @Override
        public void delglobal(String index) {
            // TODO Auto-generated method stub

        }

        @Override
        public void dellocal(String index) {
            // TODO Auto-generated method stub

        }

        @Override
        public void dellocal(int index) {
            // TODO Auto-generated method stub

        }

        @Override
        public PyObject getBuiltins() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PyBaseCode getCode() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PyObject getGlobals() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getLastI() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getLineno() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public PyObject getLocals() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PyBaseFrame getPrevious() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public TraceFunction getTraceFunction() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PyObject getclosure(int index) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PyObject getderef(int index) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PyObject getglobal(String index) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PyObject getlocal(int index) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PyObject getname(String index) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PyObject getname_or_null(String index) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setLastI(int f_lasti) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setTraceFunction(TraceFunction trace) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setderef(int index, PyObject value) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setglobal(String index, PyObject value) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setlocal(String index, PyObject value) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setlocal(int index, PyObject value) {
            // TODO Auto-generated method stub

        }
    }
}
