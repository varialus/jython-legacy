"""
   Prototype for new Jython Java dispatching logic 

   >>> import OvProto
   >>> ovp = OvProto()
   >>> rf = ReflFunc()
   >>> for m in [x for x in OvProto.declaredMethods if x.name == 'ov_posprec']: rf.addMethod(m)
   >>> rf(ovp,[0,0],None)
   'ov_posprec(int i,long l)'
   >>> rf(None,[ovp,0,0],None)
   'ov_posprec(int i,long l)'
   >>> rf(ovp,[],None)
   Traceback (most recent call last):
     File "<string>", line 1, in ?
     File "d:\exp\jy-new-disp\test\proto.py", line 397, in __call__
       raise Exception,"ReflFunc call: error or ambiguous"
   Exception: ReflFunc call: error or ambiguous
   >>> rf(ovp,[0,'a'],None)
   Traceback (most recent call last):
     File "<string>", line 1, in ?
     File "d:\exp\jy-new-disp\test\proto.py", line 397, in __call__
       raise Exception,"ReflFunc call: error or ambiguous"
   Exception: ReflFunc call: error or ambiguous
   >>> rf(ovp,['a',0],None)
   Traceback (most recent call last):
     File "<string>", line 1, in ?
     File "d:\exp\jy-new-disp\test\proto.py", line 397, in __call__
       raise Exception,"ReflFunc call: error or ambiguous"
   Exception: ReflFunc call: error or ambiguous
   >>> rf(ovp,[0,0],['a'])
   Traceback (most recent call last):
     File "<string>", line 1, in ?
     File "d:\exp\jy-new-disp\test\proto.py", line 397, in __call__
       raise Exception,"ReflFunc call: error or ambiguous"
   Exception: ReflFunc call: error or ambiguous
   >>> rf(None,[ovp,0],None)
   Traceback (most recent call last):
     File "<string>", line 1, in ?
     File "d:\exp\jy-new-disp\test\proto.py", line 397, in __call__
       raise Exception,"ReflFunc call: error or ambiguous"
   Exception: ReflFunc call: error or ambiguous
   >>> rf(None,[lang.Object(),0,0],None)
   Traceback (most recent call last):
     File "<string>", line 1, in ?
     File "d:\exp\jy-new-disp\test\proto.py", line 397, in __call__
       raise Exception,"ReflFunc call: error or ambiguous"
   Exception: ReflFunc call: error or ambiguous

"""
#

# to consider:
#
# String < [char < Character ?
#
# seq vs [PyObject vs PyObject ?
#
# keep using right-to-left arg precedence or not ? 

import java.lang
import org.python.core

tojava = org.python.core.PyJavaClass.lookup(org.python.core.PyObject).__tojava__


def atyp(spec):
    d = spec.index('[')
    comp_ty = spec[:d]
    d = spec.count('[',d)
    if len(comp_ty) != 1:
        comp_ty = 'L%s;' % comp_ty
    spec = '['*d + comp_ty
    return lang.Class.forName(spec)

precs = {}

prec = 1
for wrapcl in "Long Integer Short Character Byte Double Float Boolean".split():
    precs[getattr(java.lang,wrapcl).TYPE] = prec
    prec +=1

precs[java.lang.String] = prec

MAX_PREC = prec+1

is_ass_from = java.lang.Class.isAssignableFrom
is_array = java.lang.Class.isArray
is_prim = java.lang.Class.isPrimitive
comp_type = java.lang.Class.getComponentType

def class_compare(cl1,cl2):
    """
       compare the absolute precedence of java classes/types cl1,cl2
        0 cl1 == cl2
        1 cl1 < cl2
       -1 cl2 > cl2
       None ambiguous

       >>> import java.lang as lang
       >>> tcomp  = lambda a,b: (class_compare(a,b),class_compare(b,a))
       >>> tcomp(lang.Long.TYPE,lang.Integer.TYPE)
       (1, -1)
       >>> tcomp(lang.Integer.TYPE,lang.Short.TYPE)
       (1, -1)
       >>> tcomp(lang.Short.TYPE,lang.Double.TYPE)
       (1, -1)
       >>> tcomp(lang.Double.TYPE,lang.Float.TYPE)
       (1, -1)
       >>> tcomp(lang.Double.TYPE,lang.Boolean.TYPE)
       (1, -1)
       >>> tcomp(lang.Boolean.TYPE,lang.String)
       (1, -1)
       >>> tcomp(lang.Integer,lang.Number)
       (1, -1)
       >>> tcomp(lang.Integer,lang.Object)
       (1, -1)
       >>> tcomp(lang.String,lang.Character)
       (1, -1)
       >>> ia = atyp('I[]')
       >>> iaa = atyp('I[][]')
       >>> oa = atyp('java.lang.Object[]')
       >>> ca = atyp('C[]')
       >>> caa = atyp('C[][]')
       >>> sa = atyp('java.lang.String[]')
       >>> poa = atyp('org.python.core.PyObject[]')
       >>> po = core.PyObject
       >>> li = core.PyList
       >>> tcomp(iaa,lang.Object)
       (1, -1)
       >>> tcomp(oa,lang.Object)
       (1, -1)
       >>> tcomp(iaa,oa)
       (1, -1)
       >>> tcomp(ia,oa)
       (1, -1)
       >>> tcomp(sa,oa)
       (1, -1)
       >>> tcomp(sa,lang.Object)
       (1, -1)
       >>> tcomp(lang.String,ca)
       (1, -1)
       >>> tcomp(ca,sa)
       (1, -1)
       >>> tcomp(sa,caa)
       (1, -1)
       >>> # this should be disambiguated specially
       >>> tcomp(ca,lang.Character)
       (None, None)
       >>> tcomp(caa,lang.Character)
       (None, None)
       >>> tcomp(poa,po)
       (None, None)
       >>> tcomp(poa,li)
       (None, None)
       >>> tcomp(caa,caa)
       (0, 0)
       >>> tcomp(lang.Object,lang.Object)
       (0, 0)
       >>> tcomp(lang.Integer.TYPE,lang.Integer.TYPE)
       (0, 0)
       >>> tcomp(lang.Boolean,lang.Integer)
       (None, None)
       >>>

    """

    if cl1 is cl2: return 0

    prec1 = precs.get(cl1,MAX_PREC)
    prec2 = precs.get(cl2,MAX_PREC)
    cmp = prec2 - prec1
    if cmp >0:
        return 1
    if cmp <0:
        return -1

    if is_ass_from(cl2,cl1):
        return 1
    if is_ass_from(cl1,cl2):
        return -1

    if is_array(cl1) and is_array(cl2):
        return class_compare(comp_type(cl1),comp_type(cl2))

    return None

BETTER_APPLICABLE = {}

def arg_better_applicable(val,cand_cl,cl,parm_ar,j):
    """
         cl SHOULD be applicable wrt val (that means val can be conterted to cl) or None
         
         0   cl is cand_cl, no conversion
        -1   val cannot be converted to candl_cl or cand_cl > cl
         1   val can be converted to cand_cl, cand_cl < cl, if parm_ar is not None, converted val goes into parm_ar[j]
        None val can be converted to candl_cl but cand_cl,cl order ambiguous, no conversion
        
    """
    return BETTER_APPLICABLE[type(val)](val,cand_cl,cl,parm_ar,j)

KINDS = { 'std': 0, 'pyargs': 1, 'pyargskws': 2 } 

class Sig:

    PyObj_AR = java.lang.Object.getClass(org.python.core.Py.EmptyObjects)
    String_AR = java.lang.Object.getClass(org.python.core.Py.NoKeywords)

    def __init__(self,meth):
        self.meth = meth
        self.declaringClass = meth.declaringClass
        argtypes = self.argtypes = meth.parameterTypes
        self.static = java.lang.reflect.Modifier.isStatic(meth.modifiers)

        self.kind = 'std'
        if len(argtypes) == 1 and argtypes[0] is Sig.PyObj_AR:
            self.kind = 'pyargs'
        elif len(argtypes) == 2 and argtypes[0] is Sig.PyObj_AR and argtypes[1] is Sig.String_AR:
            self.kind = 'pyargskws'

    def compareTo(self,osig):
        """
             0 self == osig
             1 self < osig
            -1 self > osig
           None ambiguous
           'replace' self can replace osig
        """
        
        cmp = KINDS[self.kind] - KINDS[osig.kind]
        if cmp >0:
            return 1
        if cmp <0:
            return -1

        cmp = self.static - osig.static
        if cmp <0:
            return 1
        if cmp >0:
            return -1

        cmp = len(self.argtypes) - len(osig.argtypes)
        if cmp <0:
            return 1
        if cmp >0:
            return -1

        for j in range(len(self.argtypes)-1,-1,-1):
            cmp = class_compare(self.argtypes[j],osig.argtypes[j])
            if cmp != 0:
                return cmp

        repl = is_ass_from(osig.declaringClass,self.declaringClass) # self.declCl < osig.declCl

        if not self.static:
            repl = not repl

        if repl:
            return 'replace'
        return 0

    # ??? for caching: 
    #  taken/not-taken value-dep potential cand?!
    #  order can be value-dep
    #  applicability can be value-dep
    # => slicing approach

    # need predicate maybe-applicable Py-type java cl:
    #   -> yes
    #   -> no
    #   -> value-dep
    #
    #   order value-dep?

    # only one call-data inst: use ownership notion

    def better_applicable(self,osig,calldata):
        """
            -1 self > osig or non applicable
             1 self applicable < osig
           None self applicable, osig self amb
        """
 
        kws = calldata.kws

        if self.kind != 'pyargskws':
            if kws: return -1 # err keywords

            if osig:
                if KINDS[self.kind] - KINDS[osig.kind] <0:
                    return -1 # >

                if self.static - osig.static >0:
                    return -1 # >  

        static = self.static

        iself = calldata.iself[static]

        args = calldata.args
        arg_len = calldata.arg_len[static]
        a = calldata.a[static]

        if self.kind == 'pyargskws':
            pass # ??? to-do either 1/-1
        elif self.kind == 'pyargs':
            pass # ??? to-do either 1/-1

        argtypes = self.argtypes

        if arg_len != len(argtypes):
            return -1 # err arg number

        parms = calldata.fresh_parms(self,static)

        if iself: # ??? acthung! PyObjects vs. proxies
            # for now only proxy case
            if not is_ass_from(self.declaringClass,lang.Object.getClass(iself)):
                return -1

        cmp = 0

        if osig:
            for j in range(len(argtypes)-1,-1,-1):
                cmp = arg_better_applicable(args[a+j],self.argtypes[j],osig.argtypes[j],parms,j)
                # if cmp == 0 no conversion has been done!
                if cmp != 0:
                    break
        else:
            j = len(argtypes)

        if cmp == -1:  return -1 # non-applicable or >

        if cmp is None: # amb
            for j in range(0,j):
                if arg_better_applicable(args[a+j],self.argtypes[j],None,None,-1) == -1:
                    return -1
            calldata.claim(self) # at least two amb sigs, so calldata validity is not relevant, avoid vasted conversions 
            return None
        else: # < and applicable
            if parms:
                for j in range(0,j):
                    if arg_better_applicable(args[a+j],self.argtypes[j],None,parms,j) == -1:
                        return -1 # err conv error
                for j in range(j+2,len(argtypes)):
                    if arg_better_applicable(args[a+j],self.argtypes[j],None,parms,j) == -1:
                        return -1 # err conv error
                calldata.claim(self)
            return 1

class CallData:

    STATIC = 1
    INST   = 0

    def __init__(self,iself,args,kws):
        self.owner = None
        self.kws = kws
        self.args = args

        a = 0
        if iself is None:
            iself = args[0]
            a += 1
        self.iself = [iself,None]
        self.a = [a,0]

        n = len(args)

        inst_n = n-a

        self.arg_len = [inst_n,n]

        import jarray
        self.parms = []
        self.parms.append([jarray.zeros(inst_n,lang.Object),jarray.zeros(n,lang.Object)])
        self.parms.append([jarray.zeros(inst_n,lang.Object),jarray.zeros(n,lang.Object)])

        self.which = 1

    def fresh_parms(self,sig,static):
        if self.owner is sig:
            return None
        nxt = 1-self.which
        return self.parms[nxt][static]

    def claim(self,sig):
        if self.owner is not sig:
            self.which = 1- self.which
            self.owner = sig

    def invoke(self,sig):
        assert sig is self.owner,"invoke owner mismatch ?!"
        static = sig.static
        meth = sig.meth
        return meth.invoke(self.iself[static],self.parms[self.which][static])



class ReflFunc:

    def __init__(self,meth=None):
        self.sigs = []

    # copy

    def handles(self,meth):
        osig = Sig(meth)
        for sig in self.sigs:
            if osig.compareTo(sig) == 0:
                return 1
        return 0

    def addMethod(self,meth):
        lt = []
        gt = []
        newsig = Sig(meth)

        j = 0
        for sig in self.sigs:
            cmp = newsig.compareTo(sig)
            if cmp == 0:
                return
            if cmp == 'replace':
                self.sigs[j] = newsig
                return
            if cmp == 1:
                gt.append(sig)
            else:
                lt.append(sig)
            j += 1

        self.sigs = lt
        self.sigs.append(newsig)
        self.sigs.extend(gt)


    def __call__(self,iself,args,kws):
        # ??? caching?
        cands = [None] * len(self.sigs)
        ncands = 0

        calldata = CallData(iself,args,kws)

        for sig in self.sigs:
            if ncands == 0:
                if sig.better_applicable(None,calldata) == 1:
                    ncands = 1
                    cands[0] = sig 
            else:
                i = 0
                app = 1
                while i < ncands:
                    ans = sig.better_applicable(cands[i],calldata) # ??? what about error, conversions?
                    if ans == -1: # sig > cands.i or non-applicable
                        #print "IGNORE"
                        app = 0
                        break
                    if ans == 1:
                        #print "DISCARD"
                        ncands -= 1
                        cands[i] = cands[ncands]
                if app:
                    cands[ncands] = sig
                    ncands += 1
        if ncands == 1:
            return calldata.invoke(cands[0])
        else:
            raise Exception,"ReflFunc call: error or ambiguous"


core = org.python.core
lang = java.lang
io = java.io

def noconv_(val,parm_ar,j):
    import jarray
    py = jarray.zeros(1,core.PyObject)
    py[0] = val
    lang.System.arraycopy(py,0,parm_ar,j,1)

# ??? simplify
def int_better_applicable(val,cand_cl,cl,parm_ar,j):
    """
        >>> import Dbg
        >>> import jarray
        >>> a=jarray.zeros(1,lang.Object)
        >>> int_better_applicable(1,lang.Integer.TYPE,core.PyObject,a,0)
        1
        >>> Dbg.types(a) == jarray.array([lang.Integer],lang.Class)
        1
        >>> int_better_applicable(1,lang.Boolean.TYPE,lang.Integer,a,0)
        1
        >>> Dbg.types(a) == jarray.array([lang.Boolean],lang.Class)
        1
        >>> int_better_applicable(1,lang.Number,core.PyInteger,a,0)
        1
        >>> Dbg.types(a) == jarray.array([lang.Integer],lang.Class)
        1
        >>> int_better_applicable(1,core.PyInteger,lang.Number,a,0)
        -1
        >>> int_better_applicable(1,core.PyInteger,io.Serializable,a,0)
        1
        >>> Dbg.types(a) == jarray.array([core.PyInteger],lang.Class)
        1
        >>> int_better_applicable(1,lang.Object,None,a,0)
        1
        >>> Dbg.types(a) == jarray.array([lang.Integer],lang.Class)
        1
    """
    if cl is cand_cl:
        return 0 # no conversion
    # primitive types
    if cl is lang.Long.TYPE:
        return -1
    if cand_cl is lang.Long.TYPE:
        if parm_ar: parm_ar[j] = lang.Long(val)
        return 1
    if cl is lang.Integer.TYPE:
        return -1
    if cand_cl is lang.Integer.TYPE:
        if parm_ar: parm_ar[j] = lang.Integer(val)
        return 1
    if cl is lang.Short.TYPE:
        return -1
    if cand_cl is lang.Short.TYPE:
        if parm_ar: parm_ar[j] = lang.Short(val)
        return 1
    if cl is lang.Byte.TYPE:
        return -1
    if cand_cl is lang.Byte.TYPE:
        if parm_ar: parm_ar[j] = lang.Byte(val)
        return 1
    if cl is lang.Double.TYPE:
        return -1
    if cand_cl is lang.Double.TYPE:
        if parm_ar: parm_ar[j] = lang.Double(val)
        return 1
    if cl is lang.Float.TYPE:
        return -1
    if cand_cl is lang.Float.TYPE:
        if parm_ar: parm_ar[j] = lang.Float(val)
        return 1
    if cl is lang.Boolean.TYPE:
        return -1
    if cand_cl is lang.Boolean.TYPE:
        if parm_ar: parm_ar[j] = lang.Boolean(val)
        return 1
    # wrappers
    if cl is lang.Long:
        return -1
    if cand_cl is lang.Long:
        if parm_ar: parm_ar[j] = lang.Long(val)
        return 1
    if cl is lang.Integer:
        return -1
    if cand_cl is lang.Integer:
        if parm_ar: parm_ar[j] = lang.Integer(val)
        return 1
    if cl is lang.Short:
        return -1
    if cand_cl is lang.Short:
        if parm_ar: parm_ar[j] = lang.Short(val)
        return 1
    if cl is lang.Byte:
        return -1
    if cand_cl is lang.Byte:
        if parm_ar: parm_ar[j] = lang.Byte(val)
        return 1
    if cl is lang.Double:
        return -1
    if cand_cl is lang.Double:
        if parm_ar: parm_ar[j] = lang.Double(val)
        return 1
    if cl is lang.Float:
        return -1
    if cand_cl is lang.Float:
        if parm_ar: parm_ar[j] = lang.Float(val)
        return 1
    if cl is lang.Boolean:
        return -1
    if cand_cl is lang.Boolean:
        if parm_ar: parm_ar[j] = lang.Boolean(val)
        return 1
    if cl is lang.Number:
        return -1
    if cand_cl is lang.Number:
        if parm_ar: parm_ar[j] = val
        return 1

    if cl and is_ass_from(cand_cl,cl):
        return -1

    if is_ass_from(core.PyObject,cand_cl):
        if parm_ar: noconv_(val,parm_ar,j)
        return 1

    if cand_cl is lang.Object:
        if parm_ar: parm_ar[j] = val
        return 1

    if cand_cl is io.Serializable:
        if parm_ar: parm_ar[j] = val
        return 1

    return -1

BETTER_APPLICABLE[core.PyInteger] = int_better_applicable

# ??? simplify
def seq_better_applicable(val,cand_cl,cl,parm_ar,j):
    """
       >>> import Dbg,jarray; a=jarray.zeros(1,lang.Object)
       >>> seq_better_applicable([1,2],atyp("I[]"),None,a,0)
       1
       >>> seq_better_applicable([1,2],atyp("I[]"),lang.Object,a,0)
       1
       >>> seq_better_applicable([1,2],atyp("I[]"),None,None,0)
       1
       >>> seq_better_applicable([1,2],atyp("I[]"),atyp("Z[]"),None,0)
       1
       >>> seq_better_applicable([1,2],atyp("I[]"),atyp("Z[]"),a,0)
       1
       >>> Dbg.types(a) == jarray.array([atyp("I[]")],lang.Class)
       1
       >>> seq_better_applicable([1,2],atyp("I[]"),atyp("J[]"),None,0)
       -1
       >>> seq_better_applicable([1,2],atyp("I[]"),atyp("J[]"),a,0)
       -1
       >>> seq_better_applicable([1,2],atyp("I[]"),core.PyList,a,0)
       -1
       >>> seq_better_applicable([1,2],core.PyList,atyp("J[]"),a,0)
       1
       >>> Dbg.types(a) == jarray.array([core.PyList],lang.Class)
       1
       >>> seq_better_applicable([['a','b']],atyp("C[][]"),None,a,0)
       1
       >>> Dbg.types(a) == jarray.array([atyp("C[][]")],lang.Class)
       1
       >>> seq_better_applicable([['a','b'],[]],atyp("C[][]"),None,a,0)
       1
       >>> seq_better_applicable(['ab','bc'],atyp("C[][]"),atyp("java.lang.Character[]"),a,0)
       1
       >>> seq_better_applicable(['a','b'],atyp("java.lang.Character[]"),atyp("C[][]"),a,0)
       -1
       >>> seq_better_applicable(['a','b'],atyp("java.lang.Character[]"),atyp("org.python.core.PyObject[]"),a,0)
       1
       >>> seq_better_applicable(['a','bc'],atyp("java.lang.Character[]"),None,None,0)
       -1
       >>> seq_better_applicable([['a','b'],[]],atyp("C[][]"),atyp("org.python.core.PyList[]"),a,0)
       -1
       >>> seq_better_applicable([['a','b'],[]],atyp("org.python.core.PyList[]"),atyp("C[][]"),a,0)
       1
       >>> seq_better_applicable([['a','b'],[]],atyp("C[][]"),atyp("java.lang.Object[]"),a,0)
       1
       >>> seq_better_applicable([['a','b'],[]],atyp("java.lang.Object[]"),atyp("org.python.core.PyList[]"),None,0)
       -1
       >>> seq_better_applicable([['a','b'],"ab"],atyp("C[][]"),atyp("org.python.core.PyObject[]"),a,0)
       >>> # ambiguous
       >>> seq_better_applicable([[],[]],atyp("C[][][]"),None,a,0)
       1
       >>> Dbg.types(a) == jarray.array([atyp("C[][][]")],lang.Class)
       1
       >>> seq_better_applicable([[],[['a','b'],"ab"]],atyp("C[][][]"),atyp("org.python.core.PyObject[][]"),a,0) 
       >>> # ambiguous
       >>> seq_better_applicable([],lang.Object,atyp("java.lang.Object[]"),None,0)
       -1
       >>> seq_better_applicable([],lang.Integer,atyp("java.lang.Object[]"),None,0)
       -1
       >>> seq_better_applicable([],core.PyList,lang.Object,a,0)
       1
       >>> seq_better_applicable([],core.PyList,core.PyObject,a,0)
       1
       >>> seq_better_applicable([],core.PyObject,None,None,0)
       1
       >>> seq_better_applicable([],lang.Integer,core.PyList,a,0)
       -1
    """

    if cand_cl is cl:
        return 0
    if cl and is_ass_from(cand_cl,cl): # cl < cand_cl
        return -1

    # order: ... PyObject arrays ... interfaces ... Object

    if is_array(cand_cl):
        cl_comp_type = None
        if not cl: # ignore cl
            pass
        elif is_array(cl): # cl,cand_cl in <arrays>
            cl_comp_type = comp_type(cl)
        else:
            if is_ass_from(core.PyObject,cl): # cl in <...PyObject>, cand_cl in <arrays>
                return -1
            # cand_cl in <arrays>, cl in <interfaces ... Object>
        cand_comp_type = comp_type(cand_cl)
    else:
        if cl and is_array(cl): # cl in <arrays>
            if is_ass_from(core.PyObject,cand_cl) and is_ass_from(cand_cl,type(val)): # cand_cl applicable in <...PyObject>
                if parm_ar: parm_ar[j] = val # no conv
                return 1 
            else:
                return -1
        else:
            if (not cl or is_ass_from(cl,cand_cl)) and is_ass_from(cand_cl,type(val)): # type(v) < cand_cl < cl
                if parm_ar: parm_ar[j] = val # no conv        
                return 1
            else:
                return -1

    prim = 0
    if parm_ar:
        import jarray
        dest = jarray.zeros(len(val),cand_comp_type)
        if is_prim(cand_comp_type):
            data = jarray.zeros(1,lang.Object)
            prim = 1 
        else:
            data = dest
    else:
        dest = None
        data = None

    n = len(val)
    lt = 0
    gt = 0

    for k in range(n):
        cmp = arg_better_applicable(val[k],cand_comp_type,cl_comp_type,data,(not prim and k or 0))
        if cmp == 1:
            lt += 1
            if prim:
                dest[k] = data[0] # ??? tricky: use Array.set
        else:
            if cmp == -1:
                # inefficency to favor all other situations,
                # otherwise the better_applicable
                # interface needs to be even
                # more complex and distinguish between
                # non-applicable and > cases
                # when cl is not None
                if arg_better_applicable(val[k],cand_comp_type,None,None,-1) == -1:
                    return -1
                gt += 1
            data = None
            prim = 0

    if lt == n:
        if parm_ar:
            parm_ar[j] = dest
        return 1

    if gt == n:
        return -1

    return None

BETTER_APPLICABLE[core.PyList] = seq_better_applicable
BETTER_APPLICABLE[core.PyTuple] = seq_better_applicable
# etc...

# ??? simplify
def str_better_applicable(val,cand_cl,cl,parm_ar,j):
    """
       >>> import jarray
       >>> import Dbg
       >>> a=jarray.zeros(1,lang.Object)
       >>> str_better_applicable('a',lang.Character.TYPE,lang.String,a,0)
       1
       >>> Dbg.types(a)==jarray.array([lang.Character],lang.Class)
       1
       >>> str_better_applicable('a',lang.Character.TYPE,lang.Character.TYPE,a,0)
       0
       >>> str_better_applicable('ab',lang.Character.TYPE,lang.Object,a,0)
       -1
       >>> str_better_applicable('a',lang.String,lang.Character.TYPE,a,0)
       -1
       >>> str_better_applicable('a',lang.String,lang.Character,a,0)
       1
       >>> Dbg.types(a)==jarray.array([lang.String],lang.Class)
       1
       >>> str_better_applicable('a',lang.String,core.PyString,None,0)
       1
       >>> str_better_applicable('abc',atyp("C[]"),lang.Character,a,0)
       1
       >>> Dbg.types(a) == jarray.array([atyp("C[]")],lang.Class)
       1
       >>> str_better_applicable('abc',atyp("C[]"),None,None,0)
       1
       >>> str_better_applicable('abc',lang.Character,atyp("C[]"),None,0)
       -1
       >>> str_better_applicable('abc',core.PyString,atyp("C[]"),None,0)
       -1
       >>> str_better_applicable('abc',lang.Object,atyp("C[]"),None,0)
       -1
       >>> str_better_applicable('abc',core.PyString,None,None,0)
       1
       >>> str_better_applicable('abc',core.PyString,core.PyObject,None,0)
       1
       >>> str_better_applicable('abc',core.PyObject,core.PyString,None,0)
       -1
       >>> str_better_applicable('abc',core.PyObject,lang.Object,a,0)
       1
       >>> Dbg.types(a) == jarray.array([core.PyString],lang.Class)
       1
       >>> str_better_applicable('abc',lang.Object,io.Serializable,a,0)
       -1
       >>> str_better_applicable('abc',io.Serializable,None,None,0)
       1
       >>> str_better_applicable('abc',io.Serializable,lang.Object,a,0)
       1
       >>> Dbg.types(a) == jarray.array([lang.String],lang.Class)
       1
       >>> str_better_applicable('abc',lang.Object,None,a,0)
       1
       >>> Dbg.types(a) == jarray.array([lang.String],lang.Class)
       1
    """
    # order: char String [char [byte Character PyString PyObject Serializable Object
    if cand_cl is cl:
        return 0
    if cl is lang.Character.TYPE:
        return -1
    if cand_cl is lang.Character.TYPE:
        if len(val) == 1:
            if parm_ar: parm_ar[j] = lang.Character(val)
            return 1
        else:
            return -1

    if cl is lang.String:
        return -1
    if cand_cl is lang.String:
        if parm_ar: parm_ar[j] = val
        return 1

    if cl is lang.Class.forName("[C"):
        return -1
    if cand_cl is lang.Class.forName("[C"):
        import jarray
        if parm_ar: parm_ar[j] = jarray.array(val,'c')
        return 1
    if cl is lang.Class.forName("[B"):
        return -1
    if cand_cl is lang.Class.forName("[B"):
        if parm_ar: parm_ar[j] = tojava(val, lang.Class.forName("[B"))
        return 1

    if cl is lang.Character:
        return -1
    if cand_cl is lang.Character:
        if len(val) == 1:
            if parm_ar: parm_ar[j] = lang.Character(val)
            return 1
        else:
            return -1

    if cl and is_ass_from(cand_cl,cl):
        return -1

    if is_ass_from(core.PyObject,cand_cl):
        if parm_ar: noconv_(val,parm_ar,j)
        return 1

    if cand_cl is lang.Object:
        if parm_ar: parm_ar[j] = val
        return 1

    if cand_cl is io.Serializable:
        if parm_ar: parm_ar[j] = val
        return 1

    return -1

BETTER_APPLICABLE[core.PyString] = str_better_applicable
