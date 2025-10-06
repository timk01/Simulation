package org.simulation.Action;

import org.entity.Creature;
import org.entity.Herbivore;
import org.entity.Predator;
import org.map.WorldMap;
import org.simulation.InitAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Statistic {
    private static final Logger log = LoggerFactory.getLogger(Statistic.class);

    private int initialHerbivores;
    private int initialPredators;
    private int starvedHerbivores;
    private int starvedPredators;
    private int killedByPredator;
    private final Set<Creature> registeredDead = new HashSet<>();
    private final Map<String, Integer> killsDoneByPredator = new HashMap<>();
    private final Map<String, Integer> grassEatenByHerbivore = new HashMap<>();

    public Statistic() {
    }

    public void captureInitial(WorldMap map) {
        this.initialHerbivores = (int) map.getCells().values().stream()
                .filter(e -> e instanceof Herbivore)
                .count();

        this.initialPredators = (int) map.getCells().values().stream()
                .filter(e -> e instanceof Predator)
                .count();

        log.info("[INIT] counts: herbivores={}, predators={}", initialHerbivores, initialPredators);
    }

    public void printConsistencyCheck(WorldMap map) {
        map.getCells().values().forEach(e -> {
            if (e instanceof Herbivore h && h.getDeathReason() != null) {
                log.debug("[LEFT] {} hp={} reason={}", h.getIdString(), h.getHp(), h.getDeathReason());
            } else if (e instanceof Predator p && p.getDeathReason() != null) {
                log.debug("[LEFT] {} hp={} reason={}", p.getIdString(), p.getHp(), p.getDeathReason());
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

        log.debug("[CHECK] Herbivores: init={}, left={}, dead={}, sum={}",
                initialHerbivores, currentHerbivores, totalDeadHerbivores, (currentHerbivores + totalDeadHerbivores));
        log.debug("[CHECK] Predators: init={}, left={}, dead={}, sum={}",
                initialPredators, currentPredators, totalDeadPredators, (currentPredators + totalDeadPredators));

        int sumKills = getTotalPredatorKills();
        if (sumKills != killedByPredator) {
            log.warn("[CHECK] kills-by-predator sum={} != killedByPredator={}", sumKills, killedByPredator);
        }

        int sumGrass = getTotalGrassEaten();
        if (sumGrass > 0) {
            log.debug("[CHECK] Grass eaten total={}", sumGrass);
        }
    }

    public void deathRegistrator(Creature creature) {
        if (registeredDead.contains(creature)) {
            return;
        }
        registeredDead.add(creature);

        log.debug("[STAT] deathRegistrator reason={} class={}",
                creature.getDeathReason(), creature.getClass().getSimpleName());

        if (creature instanceof Herbivore
                && creature.getDeathReason() == Creature.DeathReason.STARVATION) {
            starvedHerbivores++;
        } else if (creature instanceof Predator
                && creature.getDeathReason() == Creature.DeathReason.STARVATION) {
            starvedPredators++;
        } else if (creature instanceof Herbivore
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
