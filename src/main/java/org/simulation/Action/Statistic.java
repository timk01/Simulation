package org.simulation.Action;

import org.entity.Creature;
import org.entity.Herbivore;
import org.entity.Predator;
import org.map.WorldMap;
import org.simulation.InitAction;

import java.util.*;

public class Statistic {
    private int initialHerbivores;
    private int initialPredators;
    private int starvedHerbivores;
    private int starvedPredators;
    private int killedByPredator;
    private final Set<Creature> registeredDead = new HashSet<>();
    private final Map<String, Integer> killsDoneByPredator = new HashMap<>();
    private final Map<String, Integer> grassEatenByHerbivore = new HashMap<>();

    public Statistic(List<InitAction> initActions) {
        int herbivores = 0;
        int predators = 0;

/*        for (InitAction action : initActions) {
            if (action instanceof InitCreatures init) {
                herbivores = init.getCounter();
                predators = init.getCounter();
            }
        }*/
        for (InitAction action : initActions) {
            if (action instanceof InitCreatures init) {
                herbivores = init.getHerbivoreCount();
                predators = init.getPredatorCount();
            }
        }

        this.initialHerbivores = herbivores;
        this.initialPredators = predators;
    }

    public void printConsistencyCheck(WorldMap map) {
        map.getCells().values().forEach(e -> {
            if (e instanceof Herbivore h && h.getDeathReason() != null) {
                System.out.println("[LEFT] " + h.getIdString()
                        + " hp=" + h.getHp()
                        + " reason=" + h.getDeathReason());
            } else if (e instanceof Predator p && p.getDeathReason() != null) {
                System.out.println("[LEFT] " + p.getIdString()
                        + " hp=" + p.getHp()
                        + " reason=" + p.getDeathReason());
            }
        });
        long currentHerbivores = map.getCells().values().stream()
                .filter(e -> e instanceof Herbivore)
                .count();
        long currentPredators = map.getCells().values().stream()
                .filter(e -> e instanceof Predator)
                .count();

        int totalDeadHerbivores = starvedHerbivores + killedByPredator;
        int totalDeadPredators = starvedPredators;

        System.out.println("[CHECK] Herbivores: init=" + initialHerbivores
                + ", left=" + currentHerbivores
                + ", dead=" + totalDeadHerbivores
                + ", sum=" + (currentHerbivores + totalDeadHerbivores));

        System.out.println("[CHECK] Predators: init=" + initialPredators
                + ", left=" + currentPredators
                + ", dead=" + totalDeadPredators
                + ", sum=" + (currentPredators + totalDeadPredators));
    }

    public void deathRegistrator(Creature creature) {
        System.out.println("[STAT] deathRegistrator reason=" + creature.getDeathReason()
                + " class=" + creature.getClass().getSimpleName());
        if (registeredDead.contains(creature)) {
            return;
        }
        registeredDead.add(creature);

        if (creature instanceof Herbivore
                && creature.getDeathReason() == Creature.DeathReason.STARVATION) {
            starvedHerbivores++;
        }

        if (creature instanceof Predator
                && creature.getDeathReason() == Creature.DeathReason.STARVATION) {
            starvedPredators++;
        }

        if (creature instanceof Herbivore
                && creature.getDeathReason() == Creature.DeathReason.KILLED_BY_PREDATOR) {
            killedByPredator++;
        }
    }

    public void registerPredatorKill(Predator predator) {
        killsDoneByPredator.merge(predator.getIdString(), 1, Integer::sum);
    }

    public void registerGrassEaten(Herbivore herbivore) {
        registerGrassEaten(herbivore, 1);
    }

    public void registerGrassEaten(Herbivore herbivore, int count) {
        grassEatenByHerbivore.merge(herbivore.getIdString(), count, Integer::sum);
    }

    public boolean isRegisteredDead(Creature c) {
        return registeredDead.contains(c);
    }

    public void setInitialHerbivores(int initialHerbivores) {
        this.initialHerbivores = initialHerbivores;
    }

    public void setInitialPredators(int initialPredators) {
        this.initialPredators = initialPredators;
    }

    public int getInitialHerbivores() {
        return initialHerbivores;
    }

    public int getInitialPredators() {
        return initialPredators;
    }

    public int getStarvedHerbivores() {
        return starvedHerbivores;
    }

    public int getStarvedPredators() {
        return starvedPredators;
    }

    public void setStarvedHerbivores(int starvedHerbivores) {
        this.starvedHerbivores = starvedHerbivores;
    }

    public void setStarvedPredators(int starvedPredators) {
        this.starvedPredators = starvedPredators;
    }

    public int getKilledByPredator() { return killedByPredator; }
    public Map<String, Integer> getKillsByPredator() { return killsDoneByPredator; }
    public Map<String, Integer> getGrassEatenByHerbivore() { return grassEatenByHerbivore; }

    public int getTotalPredatorKills() {
        return killsDoneByPredator.values().stream().mapToInt(Integer::intValue).sum();
    }
    public int getTotalGrassEaten() {
        return grassEatenByHerbivore.values().stream().mapToInt(Integer::intValue).sum();
    }

}
