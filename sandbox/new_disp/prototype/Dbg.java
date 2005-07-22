public class Dbg {

  public static Class[] types(Object[] ar) {
    Class[] types_ar = new Class[ar.length];
    for (int i = 0; i< ar.length;i++) {
       types_ar[i] = ar[i].getClass();
    }
    return types_ar;
  }

}