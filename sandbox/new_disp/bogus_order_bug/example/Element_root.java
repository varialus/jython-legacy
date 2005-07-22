package example;


/**
 *  <h4><code>&lt;!ELEMENT root (foo | bar)*&gt;</code></h4>
  **/

public class Element_root extends Element {

  static public class Attr_num {

   public Attr_num(String value) {
   }

  }


  public abstract static class Choice_1 {

    private int altIndex;


    public Choice_1(int altIndex) {
      this.altIndex = altIndex;
    }


    public final boolean isAlt_1() {
      return altIndex == 0;
    }

    public final Element_root.Choice_1_Alt_1 toAlt_1() {
      if (altIndex == 0) {
        return (Element_root.Choice_1_Alt_1)this;
      } else {
        return null;
      }
    }

    public final boolean isAlt_2() {
      return altIndex == 1;
    }

    public final Element_root.Choice_1_Alt_2 toAlt_2() {
      if (altIndex == 1) {
        return (Element_root.Choice_1_Alt_2)this;
      } else {
        return null;
      }
    }

    public int getAltIndex() { return altIndex; }

  }

  public static class Choice_1_Alt_1 extends Element_root.Choice_1 {

    public static final int ALT_INDEX = 0;

    Element_foo elem_1_foo;


    public Choice_1_Alt_1(Element_foo elem_1_foo) {
      super(ALT_INDEX);
      this.elem_1_foo = elem_1_foo;
    }

    public Element_foo getElem_1_foo() {
      return elem_1_foo;
    }

    public Element_foo setElem_1_foo(Element_foo newElem_1_foo) {
      final Element_foo oldElem_1_foo = getElem_1_foo();
      elem_1_foo = newElem_1_foo;
      return oldElem_1_foo;
    }

  }

  public static class Choice_1_Alt_2 extends Element_root.Choice_1 {

    public static final int ALT_INDEX = 1;

    Element_bar elem_1_bar;


    public Choice_1_Alt_2(Element_bar elem_1_bar) {
      super(ALT_INDEX);
      this.elem_1_bar = elem_1_bar;
    }

    public Element_bar getElem_1_bar() {
      return elem_1_bar;
    }

    public Element_bar setElem_1_bar(Element_bar newElem_1_bar) {
      final Element_bar oldElem_1_bar = getElem_1_bar();
      elem_1_bar = newElem_1_bar;
      return oldElem_1_bar;
    }


  }

  static final int TAG_INDEX = 2;

  Attr_num attr_num;

  Choice_1[] choices_1;

  public static final String TAG_NAME = "root";


  public Element_root(String attr_num, Choice_1[] choices_1) {
    super(TAG_NAME);
    this.attr_num = new Attr_num(attr_num);
    this.choices_1 = (Element_root.Choice_1[])choices_1.clone();
  }


  public final int getTagIndex() {
    return TAG_INDEX;
  }

  public Attr_num getAttr_num() {
    return attr_num;
  }

  public Choice_1[] getChoices_1() {
    return choices_1;
  }

  public Choice_1 getChoice_1(int index) {
    return choices_1[index];
  }

  public int countChoices_1() {
    return choices_1.length;
  }

  public Choice_1 setChoice_1(int index, Choice_1 newChoice_1) {
    final Element_root.Choice_1 oldChoice_1 = getChoice_1(index);
    choices_1[index] = newChoice_1;
    return oldChoice_1;
  }

  public Choice_1[] setChoices_1(Choice_1[] newChoices_1) {
    final Element_root.Choice_1[] oldChoices_1 = choices_1;
    choices_1 = newChoices_1;
    return oldChoices_1;
  }


}
