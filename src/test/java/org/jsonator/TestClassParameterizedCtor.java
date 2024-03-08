package org.jsonator;

import org.jsonator.Annotations.JsonConstructor;

public class TestClassParameterizedCtor {
    public int number;

    @JsonConstructor(parameters = { "number" })
    public TestClassParameterizedCtor(int number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        if (other instanceof TestClassParameterizedCtor t) {
            return (this.number == t.number);
        }

        return false;
    }
}
