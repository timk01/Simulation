package org.simulation;

import org.entity.Creature;
import org.entity.Herbivore;
import org.entity.Predator;
import org.map.WorldMap;
import org.simulation.Action.Statistic;

public class ShowReportAction implements FinishAction {
    private final Statistic statistic;

    public ShowReportAction(Statistic statistic) {
        this.statistic = statistic;
    }

    private static long getCount(WorldMap map, Class<? extends Creature> clazz) {
        return map.getCells().values().stream()
                .filter(clazz::isInstance)
                .count();
    }

    @Override
    public void finish(WorldMap map, Renderer renderer) {
        long herbivoresLeft = getCount(map, Herbivore.class);

        long predatorsLeft = getCount(map, Predator.class);

        renderer.showResults(
                herbivoresLeft,
                predatorsLeft,
                statistic.getInitialHerbivores(),
                statistic.getInitialPredators(),
                statistic.getStarvedHerbivores(),
                statistic.getStarvedPredators(),
                statistic.getTotalPredatorKills(),
                statistic.getKillsByPredator(),
                statistic.getGrassEatenByHerbivore()
        );
    }
}
