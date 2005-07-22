public class Bench {

  private java.util.HashMap h;

  private static Class[] classes = { Long.class,Integer.class,Short.class,Byte.class,Double.class,Float.class, Character.class };
  
  public int iget(Class cl) {
    if (cl == Integer.class) return 1;
    if (cl == Double.class) return 4;
    if (cl == Byte.class) return 3;
    if (cl == Long.class) return 0;
    if (cl == Short.class) return 2;
    if (cl == Float.class) return 5;
    return -1;
  }

  public void setup() {
    h = new java.util.HashMap();

    for(int i = 0; i < classes.length-1; i++) {
       h.put(classes[i],new Integer(i));
    }
  }

  public int h_iget(Class cl) {
    Integer ind = (Integer)h.get(cl);
    if (ind != null) return ind.intValue();
    return -1;
  }


  public static void main(String[] args) {
    Bench me = new Bench();
    me.setup();

    System.out.println(classes[6]);

    long t0 = System.currentTimeMillis();

    int n;
    int v;

    for(n = 0;n < 100000; n++) {
       v = me.iget(classes[0]);
       v = me.iget(classes[6]);
       v = me.iget(classes[1]);
       v = me.iget(classes[0]);
       v = me.iget(classes[2]);
       v = me.iget(classes[1]);
       v = me.iget(classes[6]);
       v = me.iget(classes[0]);
       v = me.iget(classes[3]);
       v = me.iget(classes[1]);
       v = me.iget(classes[1]);
       v = me.iget(classes[4]);
       v = me.iget(classes[0]);
       v = me.iget(classes[5]);
       v = me.iget(classes[6]);
    }

    System.out.println("ela: "+(System.currentTimeMillis()-t0));


    t0 = System.currentTimeMillis();

    for(n = 0;n < 100000; n++) {
       v = me.h_iget(classes[0]);
       v = me.h_iget(classes[6]);
       v = me.h_iget(classes[1]);
       v = me.h_iget(classes[0]);
       v = me.h_iget(classes[2]);
       v = me.h_iget(classes[1]);
       v = me.h_iget(classes[6]);
       v = me.h_iget(classes[0]);
       v = me.h_iget(classes[3]);
       v = me.h_iget(classes[1]);
       v = me.h_iget(classes[1]);
       v = me.h_iget(classes[4]);
       v = me.h_iget(classes[0]);
       v = me.h_iget(classes[5]);
       v = me.h_iget(classes[6]);
    }

    System.out.println("ela: "+(System.currentTimeMillis()-t0));


  }


}