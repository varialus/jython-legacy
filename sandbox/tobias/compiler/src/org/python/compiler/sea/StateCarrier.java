package org.python.compiler.sea;

import org.thobe.compiler.sea.NodeSuccession;
import org.thobe.compiler.sea.Value;

public class StateCarrier {
    private final Value payload;

    StateCarrier(Value payload) {
        this.payload = payload;
    }

    Value payload() {
        // TODO Auto-generated method stub
        return payload;
    }

    NodeSuccession onTrue() {
        // TODO Auto-generated method stub
        return null;
    }

    NodeSuccession onFalse() {
        // TODO Auto-generated method stub
        return null;
    }
}