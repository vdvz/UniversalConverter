package com.example.UniversalConverter.RequestRepresentation;

import java.util.Objects;

/**
 * Основная единица в Expression'ах. Хранит степень и имя меры, т.е Еслли пришедшая строка запроса
 * мм*мм*км/мм, то выражение будет содержать 2 Unit'а - мм^2 и км^1
 */
public class Unit {

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

  public String getName() {
    return Name;
  }

  /*Unit'ы эквиваленты если их имена эквивалетны*/
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

  public int getPower() {
    return power;
  }

  public void setPower(int power) {
    this.power = power;
  }

  @Override
  public String toString() {
    return "Unit name '" + Name + " , power = " + power;
  }
}
