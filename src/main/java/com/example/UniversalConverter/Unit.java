package com.example.UniversalConverter;

import java.util.Objects;

public class Unit implements Unit_I {

    private final String Name;

    private int power;

    public Unit(String _name) {
        Name = _name;
        power = 1;
    }

    public Unit(String _name, Integer _power) {
        Name = _name;
        power = _power;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Unit unit = (Unit) o;
        return Objects.equals(Name, unit.Name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Name);
    }

    @Override
    public int getPower() {
        return power;
    }

    @Override
    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "Unit name '" + Name + " , power = " + power;
    }
}
