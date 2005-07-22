package example;


public class Visitor extends Object {

  public void visit(String pcdata) {
  }

  /**
   *  <code>&lt;!ELEMENT root (foo | bar)*&gt;</code>
    **/
  public void visit(Element_root element) {
	  System.out.println("visit(Element_root element)");
    visit(element.getAttr_num());
    for (int i = 0, n = element.choices_1.length ; i < n ; i++) {
      visit(element.choices_1[i]);
    }
  }

  /**
   *  <code>&lt;!ELEMENT foo (#PCDATA | foo | bar)*&gt;</code>
    **/
  public void visit(Element_foo element) {
	  System.out.println("visit(Element_foo element)");
    visit(element.getAttr_num());
    for (int i = 0, n = element.countContent() ; i < n ; i++) {
      visit(element.getContent(i));
    }
  }

  /**
   *  <code>&lt;!ELEMENT bar (foo*)&gt;</code>
    **/
  public void visit(Element_bar element) {
	  System.out.println("visit(Element_bar element)");
    visit(element.getAttr_num());
    for (int i = 0, n = element.elems_1_foo.length ; i < n ; i++) {
      visit(element.elems_1_foo[i]);
    }
  }

  public void visit(Element_root.Attr_num attr_num) {
	  System.out.println("visit(Element_root.Attr_num attr_num)");
  }

  public void visit(Element_root.Choice_1 choice) {
System.out.println("visit(Element_root.Choice_1)");
    switch (choice.getAltIndex()) {
    case Element_root.Choice_1_Alt_1.ALT_INDEX:
      visit(choice.toAlt_1());
      break;
    case Element_root.Choice_1_Alt_2.ALT_INDEX:
      visit(choice.toAlt_2());
      break;
    }
  }

  public void visit(Element_root.Choice_1_Alt_1 alt) {
	  System.out.println("visit(Element_root.Choice_1_Alt_1 alt)");
    visit(alt.elem_1_foo);
  }

  public void visit(Element_root.Choice_1_Alt_2 alt) {
	  System.out.println("visit(Element_root.Choice_1_Alt_2 alt)");
    visit(alt.elem_1_bar);
  }

  public void visit(Document_root document) {
    visit(document.getDocumentElement());
  }

	public void visit(Element_foo.Attr_num attr_num) {
	  System.out.println("visit(Element_foo.Attr_num attr_num)");
  }

  public void visit(Element_foo.Content content) {
	  System.out.println("visit(Element_foo.Content content)");
    switch (content.getAltIndex()) {
    case Element_foo.Content.ALT_PCDATA:
      visit(content.toPCData());
      break;
    case Element_foo.Content.ALT_Element_foo:
      visit(content.toElement_foo());
      break;
    case Element_foo.Content.ALT_Element_bar:
      visit(content.toElement_bar());
      break;
    }
  }

  public void visit(Document_foo document) {
    visit(document.getDocumentElement());
  }

  public void visit(Element_bar.Attr_num attr_num) {
	  System.out.println("visit(Element_bar.Attr_num attr_num)");
  }

  public void visit(Document_bar document) {
    visit(document.getDocumentElement());
  }

  public void visit(Element element) {
	  System.out.println("visit(Element element)");
    switch (element.getTagIndex()) {
    case Element_foo.TAG_INDEX:
      visit((Element_foo)element);
      break;
    case Element_bar.TAG_INDEX:
      visit((Element_bar)element);
      break;
    case Element_root.TAG_INDEX:
      visit((Element_root)element);
      break;
    }
  }
}
