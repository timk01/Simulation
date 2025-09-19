package org.entity;

import java.util.Random;

public class Herbivore extends Creature {

    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 3;
    private static final int MIN_HP = 10;
    private static final int MAX_HP = 20;

    public Herbivore(Random random) {
        super(
                random.nextInt(MAX_SPEED - MIN_SPEED + 1) + MIN_SPEED,
                random.nextInt(MAX_HP - MIN_HP + 1) + MIN_HP);
    }

    public Herbivore(int speed, int hp) {
        super(speed, hp);
    }

    @Override
    public void makeMove() {
        //todo
        //1. убегает от хищника,
        //2. ест траву рядом
        //3. иначе шагает случайно.
        //4. (сильно позже) - добавить Вижн у существ.
    }
}
