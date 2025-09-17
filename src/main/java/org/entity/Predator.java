package org.entity;

public class Predator extends Creature {

    private int attackStrength;

    public Predator(int speed, int hp, int attackStrength) {
        super(speed, hp);
        this.attackStrength = attackStrength;
    }

    public int getAttackStrength() {
        return attackStrength;
    }

    @Override
    public void makeMove() {
        //toDo
        //1. если воблизости есть травоядное - атаковать (хп травоядного уменьшается на количество атаки)
        //2. если нет - радномно переместиться.
    }
}
