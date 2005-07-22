package example;


/**
 *  <h4><code>&lt;!ELEMENT bar (foo*)&gt;</code></h4>
  **/

public class Element_bar extends Element {


  static public class Attr_num {

    public Attr_num(String value) {}

  }


  static final int TAG_INDEX = 0;

  Attr_num attr_num;

  Element_foo[] elems_1_foo;

  public static final String TAG_NAME = "bar";


  public Element_bar(String attr_num, Element_foo[] elems_1_foo) {
    super(TAG_NAME);
    this.attr_num = new Attr_num(attr_num);
    this.elems_1_foo = (Element_foo[])elems_1_foo.clone();
  }

  public final int getTagIndex() {
    return TAG_INDEX;
  }

  public Attr_num getAttr_num() {
    return attr_num;
  }

  public Element_foo[] getElems_1_foo() {
    return elems_1_foo;
  }

  public Element_foo getElem_1_foo(int index) {
    return elems_1_foo[index];
  }

  public int countElems_1_foo() {
    return elems_1_foo.length;
  }

  public Element_foo setElem_1_foo(int index, Element_foo newElem_1_foo) {
    final Element_foo oldElem_1_foo = getElem_1_foo(index);
    elems_1_foo[index] = newElem_1_foo;
    return oldElem_1_foo;
  }

  public Element_foo[] setElems_1_foo(Element_foo[] newElems_1_foo) {
    final Element_foo[] oldElems_1_foo = elems_1_foo;
    elems_1_foo = newElems_1_foo;
    return oldElems_1_foo;
  }

}
