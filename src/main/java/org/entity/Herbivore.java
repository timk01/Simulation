package org.entity;

public class Herbivore extends Creature {
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
