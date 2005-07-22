package example;


public abstract
  class Element {

  private String tagName;


  abstract public int getTagIndex();

  public Element(String tagName) {

    this.tagName = tagName;

  }

}
