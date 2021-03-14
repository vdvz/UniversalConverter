package com.example.UniversalConverter;

import java.util.Objects;

public class Unit implements Unit_I{

    private final String Name;

    Unit(String _name) {
        Name = _name;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return Objects.equals(Name, unit.Name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Name);
    }
}
