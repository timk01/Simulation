package org.entity;

public abstract class Creature extends Entity {

    private int speed;
    private int hp;

    public Creature(int speed, int hp) {
        this.speed = speed;
        this.hp = hp;
    }

    public abstract void makeMove();
}
