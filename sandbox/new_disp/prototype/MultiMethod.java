import org.apache.commons.lang.StringUtils;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.python.core.Py;

public class MultiMethod {

    private Map precs;
    private final Integer MAX_PREC;
    private Map BETTER_APPLICABLE;

    private static final int STD_KIND = 0;
    private static final int PYARGS_KIND = 1;
    private static final int PYARGSKWS_KIND = 2;
    
    public MultiMethod() {
        precs = new HashMap();
        precs.put(Long.TYPE, new Integer(1));
        precs.put(Integer.TYPE, new Integer(2));
        precs.put(Short.TYPE, new Integer(3));
        precs.put(Character.TYPE, new Integer(4));
        precs.put(Byte.TYPE, new Integer(5));
        precs.put(Double.TYPE, new Integer(6));
        precs.put(Float.TYPE, new Integer(7));
        precs.put(Boolean.TYPE, new Integer(8));
        precs.put(String.class, new Integer(9));
        MAX_PREC = new Integer(10);
        
        BETTER_APPLICABLE = new HashMap();
    }


    //getArrayType(spec)
    public static Class atyp(String spec) throws ClassNotFoundException {
        int d = spec.indexOf('[');
        String comp_ty;
        if (d != -1) {
            comp_ty = spec.substring(0, d);
        } else {
            comp_ty = spec;
        }
        d = StringUtils.countMatches(spec, "[");
        if (comp_ty.length() != 1) {
            comp_ty = "L" + comp_ty + ";";
        }
        spec = StringUtils.repeat("[", d) + comp_ty;
        System.out.println("trying:" + spec);
        return Class.forName(spec);
    }
   
    private int class_compare(Class cl1, Class cl2) {
        if (cl1 == cl2) {
            return 0;
        }

        Integer prec1 = (Integer)precs.get(cl1);
        if (prec1 == null) {
            prec1 = MAX_PREC;
        }
        
        Integer prec2 = (Integer)precs.get(cl2);
        if (prec2 == null) {
            prec2 = MAX_PREC;
        }
        int cmp = prec2.intValue() - prec1.intValue();
        if (cmp > 0) {
            return 1;
        }
        if (cmp < 0) {
            return -1;
        }
        if (cl2.isAssignableFrom(cl1)) {
            return 1;
        }
        if (cl1.isAssignableFrom(cl2)) {
            return -1;
        }
        if (cl1.isArray() && cl2.isArray()) {
            return class_compare(cl1.getComponentType(), cl2.getComponentType());
        }
        //FIXME: Py.None or null or -2 here?
        return -2;
    }
    
    class Sig {
        private final Class PyObj_AR;
        private final Class String_AR;
        private final Method meth;
        private final Class declaringClass;
        private final Class[] argTypes;
        private final boolean isstatic;
        private final int kind;

        public Sig(Method aMeth) {
            PyObj_AR = Py.EmptyObjects.getClass();
            String_AR = Py.NoKeywords.getClass();
            meth = aMeth;
            declaringClass = meth.getDeclaringClass();
            argTypes = meth.getParameterTypes();
            isstatic = Modifier.isStatic(meth.getModifiers());

            if (argTypes.length == 1 && argTypes[0] == PyObj_AR) {
                kind = PYARGS_KIND;
            } else if (argTypes.length == 2 && argTypes[0] == PyObj_AR && argTypes[1] == String_AR) {
                kind = PYARGSKWD_KIND;
            } else {
                kind = STD_KIND;
            }
        }

        public int getKind() {
            return kind;
        }

        public Class[] getArgTypes() {
            return argTypes;
        }

        public boolean isStatic() {
            return isstatic;
        }

        public int compareTo(Sig osig) {
            int cmp = getKind() - oval.getKind();
            if (cmp > 0) {
                return 1;
            }
            if (cmp < 0) {
                return -1;
            }
           
            if (!isStatic() && osig.isStatic()) {
                return 1;
            }
            if (isStatic() && !osig.isStatic()) {
                return -1;
            }

            cmp = getArgTypes().length - osig.getArgTypes().length;
            if (cmp < 0) {
                return 1;
            }

            if (cmp > 0) {
                return -1;
            }
            
            for(int j=getArgTypes.length - 1, j <= 0, j--) {
                cmp = class_compare(getArgtypes()[j], osig.getArgTypes()[j]);
                if (cmp != 0) {
                    return cmp;
                }
            }
            //FIXME: implement:
            //repl = is_ass_from(osig.declaringClass,self.declaringClass) # self.declCl < osig.declCl
            //
            //if not self.static:
            //    repl = not repl
            // 
            //if repl:
            //    return 'replace'

            return 0;
        }

        private int better_applicable(Sig osig, CallData calldata) {
            //FIXME: finish after CallData
            if (!kind.equals(PYARGKWD_KIND)) {
            }
        }

    }
    class CallData() {
        private static final int STATIC = 1;
        private static final int INST = 0;
        
        private PyObject _owner = null;
        private PyObject _iself = null;
        private PyObject[] _kws;
        private PyObject[] _args;

        private int[] arg_len;

        public CallData(PyObject iself, PyObject[] args, PyObject[] kws) {
            _kws = kws;
            _args = args;
            //XXX: what is a?
            int a = 0;
            if (_iself == null) {
                _iself = iself;
                a += 1;
            }
            int n = args.length;
            int inst_n = n - a;
            arg_len = int[]{inst_n,n}
            
        }

        public PyObject fresh_parms(Sig sig, int static) {
            if (owner == sig) {
                return null;
            }

        
    }

    public static void main(String args[]) throws Exception {
        Class clazz = atyp("java.lang.Object[]");
        System.out.println(clazz.getName());
    }
}
