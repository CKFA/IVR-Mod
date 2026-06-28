package net.hulan;

import net.minecraft.util.Tuple;

public class Tuples<X, Y, Z> {

    private X x;
    private Y y;
    private Z z;

    public static <X, Y, Z> Tuples<X, Integer, Z> fromTuple(Tuple<X, Z> tuple) {
        return new Tuples<>(tuple.getA(), 0, tuple.getB());
    }

    public Tuples(X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public X getX() {
        return x;
    }

    public Y getY() {
        return y;
    }

    public Z getZ() {
        return z;
    }

    public void setX(X x) {
        this.x = x;
    }

    public void setY(Y y) {
        this.y = y;
    }

    public void setZ(Z z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "Tuples[X=" + x + ", Y=" + y + ", Z=" + z + "]";
    }

    public Tuple<X, Z> toTuple() {
        return new Tuple<>(x, z);
    }

    public Tuple<X, Y> toTupleWithXY() {
        return new Tuple<>(x, y);
    }

    public Tuple<X, Z> toTupleWithXZ() {
        return toTuple();
    }

    public Tuple<Z, Y> toTupleWithZY() {
        return new Tuple<>(z, y);
    }
}
