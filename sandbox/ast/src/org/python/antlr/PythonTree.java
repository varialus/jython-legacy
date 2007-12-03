package org.python.antlr;

import org.antlr.runtime.tree.BaseTree;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.Token;

import java.io.DataOutputStream;
import java.io.IOException;

import org.antlr.runtime.RecognitionException;

public class PythonTree extends CommonTree {

    public PythonTree(Token token) {
        super(token);
    }

    public String toString() {
		if ( isNil() ) {
			return "None";
		}
		return token.getText().toLowerCase();
	}

    public String toStringTree() {
		if ( children==null || children.size()==0 ) {
			return this.toString();
		}
		StringBuffer buf = new StringBuffer();
		if ( !isNil() ) {
			buf.append("(");
			buf.append(this.toString());
			buf.append(' ');
		}
		for (int i = 0; children!=null && i < children.size(); i++) {
			BaseTree t = (BaseTree) children.get(i);
			if ( i>0 ) {
				buf.append(' ');
			}
			buf.append(t.toStringTree());
		}
		if ( !isNil() ) {
			buf.append(")");
		}
		return buf.toString();
	}
}
