package org.entity;

import org.map.Location;
import org.map.WorldMap;

public abstract class Creature extends Entity {

    private int speed;
    private int hp;
    private int turn;

    public Creature(int speed, int hp) {
        this.speed = speed;
        this.hp = hp;
    }

    public boolean tick() {
        turn++;
        boolean isDead = false;
        if (turn >= 5) {
            this.hp--;
            isDead = this.hp <= 0;
        }
        return isDead;
    }

    public abstract Location makeMove(WorldMap map, Location location);

    public abstract boolean canMoveInto(Entity target);

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}
