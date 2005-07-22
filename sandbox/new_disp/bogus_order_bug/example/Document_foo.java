package example;


public
  class Document_foo {


  private Element_foo root;

  public Document_foo(Element_foo root) {
    this.root = root;
  }

  public Element_foo getDocumentElement() {
    return (Element_foo)root;
  }

  public final int getTagIndex() {
    return Element_foo.TAG_INDEX;
  }

}
