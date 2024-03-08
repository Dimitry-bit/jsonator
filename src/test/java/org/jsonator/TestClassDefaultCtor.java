package org.jsonator;

public class TestClassDefaultCtor {
    int number;

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        if (other instanceof TestClassDefaultCtor t) {
            return (this.number == t.number);
        }

        return false;
    }
}
