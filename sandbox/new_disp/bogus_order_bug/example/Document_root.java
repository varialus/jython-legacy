package example;


public
  class Document_root {

  private Element_root root;

  public Document_root(Element_root root) {
    this.root = root;
  }

  public Element_root getDocumentElement() {
    return (Element_root)root;
  }

  public final int getTagIndex() {
    return Element_root.TAG_INDEX;
  }

}
