package example;


/**
 *  <h4><code>&lt;!ELEMENT foo (#PCDATA | foo | bar)*&gt;</code></h4>
  **/

public class Element_foo extends Element {

  public static class Attr_num  {


    public Attr_num(String value) {
    }

  }

  public static class Content  {

    public static final int ALT_Element_foo = 0;

    public static final int ALT_Element_bar = 1;

    public static final int ALT_PCDATA = -1;

    private int altIndex;
    private Element content;
    private String pcdata;


    public Content(String pcdata) {
      this.altIndex = ALT_PCDATA;

      this.pcdata = pcdata;
    }


    private Content(int altIndex, Element element) {
      this.altIndex = altIndex;
      this.content = element;
    }


    public int getAltIndex() {
      return altIndex;

    }

    public final String toPCData() {
      if (altIndex == ALT_PCDATA) {
        return pcdata;
      } else {
        return null;
      }
    }


    public Content(Element_foo elem_foo) {
      this(0, elem_foo);
    }

    public Content(Element_bar elem_bar) {
      this(1, elem_bar);
    }

    public final int getAltCount() {
      return 2;
    }

    public final boolean isElement_foo() {
      return altIndex == ALT_Element_foo;
    }

    public final Element_foo toElement_foo() {
      if (altIndex == ALT_Element_foo) {
        return (Element_foo)this.content;
      } else {
        return null;
      }
    }

    public final boolean isElement_bar() {
      return altIndex == ALT_Element_bar;
    }

    public final Element_bar toElement_bar() {
      if (altIndex == ALT_Element_bar) {
        return (Element_bar)this.content;
      } else {
        return null;
      }
    }
  }


  static final int TAG_INDEX = 1;

  Attr_num attr_num;

  final java.util.List content = new java.util.ArrayList();

  public static final
    Content[] EMPTY_CONTENT =
    new Content[] {
    };

  public static final String TAG_NAME = "foo";


  public Element_foo(String attr_num, Content[] content) {
    super(TAG_NAME);
    this.attr_num = new Attr_num(attr_num);
    this.content.addAll(java.util.Arrays.asList(content));
  }

  public final int getTagIndex() {
    return TAG_INDEX;
  }

  public Attr_num getAttr_num() {
    return attr_num;
  }

  public int countContent() {
    return content.size();
  }

  public Content getContent(int index) {
    return (Content)content.get(index);
  }

}
