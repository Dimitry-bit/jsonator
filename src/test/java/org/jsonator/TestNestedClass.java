package org.jsonator;

public class TestNestedClass {
    TestClassDefaultCtor nestedClass = new TestClassDefaultCtor();

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        if (other instanceof TestNestedClass t) {
            return nestedClass.equals(t.nestedClass);
        }

        return false;
    }
}
