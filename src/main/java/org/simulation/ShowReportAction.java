package org.simulation;

import org.entity.Creature;
import org.entity.Herbivore;
import org.entity.Predator;
import org.map.WorldMap;
import org.simulation.Action.Statistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowReportAction implements FinishAction {
    private static final Logger log = LoggerFactory.getLogger(ShowReportAction.class);

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
        log.info("[Finish] Emitting final report");
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
