package example;


public
  class Document_bar {


  private Element_bar root;


  public Document_bar(Element_bar root) {
    this.root = root;
  }



  public Element_bar getDocumentElement() {
    return (Element_bar)root;
  }

  public final int getTagIndex() {
    return Element_bar.TAG_INDEX;
  }

}
