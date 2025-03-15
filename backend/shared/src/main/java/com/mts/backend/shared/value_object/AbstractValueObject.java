package com.mts.backend.shared.value_object;

import java.util.Objects;

public abstract class AbstractValueObject implements IValueObject {
    /**
     * Returns the components that make up the identity of this value object.
     * @return An iterable of objects representing the value object's components.
     */
    protected abstract Iterable<Object> getEqualityComponents();

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        AbstractValueObject valueObject = (AbstractValueObject) obj;

        return iterableEquals(getEqualityComponents(), valueObject.getEqualityComponents());
    }

    @Override
    public int hashCode() {
        int hash = 1;

        for (Object component : getEqualityComponents()) {
            hash = 23 * hash + (component != null ? component.hashCode() : 0);
        }

        return hash;
    }

    /**
     * Helper method to check if two Iterables contain equal elements in the same order.
     * This mimics C#'s SequenceEqual behavior.
     */
    private boolean iterableEquals(Iterable<Object> first, Iterable<Object> second) {
        java.util.Iterator<Object> it1 = first.iterator();
        java.util.Iterator<Object> it2 = second.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            Object o1 = it1.next();
            Object o2 = it2.next();

            if (!Objects.equals(o1, o2)) {
                return false;
            }
        }

        // Both iterators should be exhausted if the sequences are equal
        return !it1.hasNext() && !it2.hasNext();
    }

    /**
     * Java doesn't support operator overloading, so we provide explicit methods instead.
     */
    public static boolean areEqual(AbstractValueObject left, AbstractValueObject right) {
        if (left == null && right == null)
            return true;

        if (left == null || right == null)
            return false;

        return left.equals(right);
    }

    public static boolean areNotEqual(AbstractValueObject left, AbstractValueObject right) {
        return !areEqual(left, right);
    }
}
