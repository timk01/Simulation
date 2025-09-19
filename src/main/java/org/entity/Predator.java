package org.entity;

import java.util.Random;

public class Predator extends Creature {

    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 4;
    private static final int MIN_HP = 15;
    private static final int MAX_HP = 25;
    private static final int MIN_ATTACK = 5;
    private static final int MAX_ATTACK = 15;

    private final int attackStrength;

    public Predator(Random random) {
        super(
                random.nextInt(MAX_SPEED - MIN_SPEED + 1) + MIN_SPEED,
                random.nextInt(MAX_HP - MIN_HP + 1) + MIN_HP
        );
        this.attackStrength = random.nextInt(MAX_ATTACK - MIN_ATTACK + 1) + MIN_ATTACK;
    }

    public Predator(int speed, int hp, int attackStrength) {
        super(speed, hp);
        this.attackStrength = attackStrength;
    }

    public int getAttackStrength() {
        return attackStrength;
    }

    @Override
    public void makeMove() {
        // todo
        // 1. если вблизи есть травоядное — атаковать
        // 2. если нет — случайно переместиться
    }
}
