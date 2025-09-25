package org.simulation;

import org.entity.Creature;
import org.entity.Herbivore;
import org.entity.Predator;
import org.map.WorldMap;
import org.simulation.Action.Statistic;

import java.awt.*;

public class ShowReportAction implements FinishAction {
    private final Statistic statistic;
    private final WorldMap map;

    public ShowReportAction(Statistic statistic, WorldMap map) {
        this.statistic = statistic;
        this.map = map;
    }
    @Override
    public void finish(WorldMap map, Renderer renderer) {
        long herbivoresLeft = map.getCells().values().stream()
                .filter(e -> e instanceof Herbivore)
                .count();

        long predatorsLeft = map.getCells().values().stream()
                .filter(e -> e instanceof Predator)
                .count();

        //statistic.printConsistencyCheck(worldMap);

        renderer.showResults(
                herbivoresLeft,
                predatorsLeft,
                statistic.getInitialHerbivores(),
                statistic.getInitialPredators(),
                statistic.getStarvedHerbivores(),
                statistic.getStarvedPredators(),
                statistic.getKilledByPredator(),
                statistic.getKillsByPredator(),
                statistic.getGrassEatenByHerbivore()
        );
    }
}
