package com.example.UniversalConverter;

import java.util.Objects;

public class Unit implements Unit_I{

    private final String Name;

    private int power;

    // "" - безразмерная величина
    //Степень безразмерной величины всегда единица => ""^1 * ""^(-1) = 1
    public Unit(String _name){
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        //For nulls: true if both are null; false if only once
        return Objects.equals(Name, unit.Name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Name);
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "Name='" + Name + '\'' +
                ", power=" + power +
                '}';
    }
}
