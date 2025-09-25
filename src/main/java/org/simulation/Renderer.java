package org.simulation;

import org.entity.*;
import org.map.Location;
import org.map.WorldMap;
import org.simulation.Action.Statistic;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Renderer {

    private final Map<Class<? extends Entity>, Icon> icons = new HashMap<>();
    private final int cellSize;
    private final JLabel[][] cells;
    private JFrame jFrame;
    private JPanel jpanel;
    private JLabel herbivoreCountLabel;
    private JLabel predatorCountLabel;
    private JLabel turnLabel;

    private JLabel starvedHerbivoresLabel;
    private JLabel starvedPredatorsLabel;
    private JLabel killedByPredatorsLabel;
    private JLabel grassEatenTotalLabel;
    public Renderer(WorldMap map) {
        this.cellSize = 32;
        this.cells = new JLabel[map.getHeight()][map.getWidth()];

        jFrame = new JFrame("Simulation");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        jpanel = new JPanel(new GridLayout(map.getHeight(), map.getWidth()));
        jFrame.add(jpanel);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        herbivoreCountLabel = new JLabel("Herbivores: 0");
        predatorCountLabel = new JLabel("Predators: 0");
        turnLabel = new JLabel("Turn: 0");

        JLabel sep1 = new JLabel("  |  ");
        JLabel sep2 = new JLabel("  |  ");

        row1.add(herbivoreCountLabel);
        row1.add(sep1);
        row1.add(predatorCountLabel);
        row1.add(sep2);
        row1.add(turnLabel);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        starvedHerbivoresLabel = new JLabel("Herbivores starved: 0");
        starvedPredatorsLabel = new JLabel("Predators starved: 0");
        killedByPredatorsLabel = new JLabel("Herbivores killed: 0");
        grassEatenTotalLabel = new JLabel("Grass eaten: 0");

        row2.add(starvedHerbivoresLabel);
        row2.add(new JLabel("  |  "));
        row2.add(starvedPredatorsLabel);
        row2.add(new JLabel("  |  "));
        row2.add(killedByPredatorsLabel);
        row2.add(new JLabel("  |  "));
        row2.add(grassEatenTotalLabel);

        statsPanel.add(row1);
        statsPanel.add(row2);

/*        statsPanel.add(new JSeparator(SwingConstants.VERTICAL));
        statsPanel.add(starvedHerbivoresLabel);
        statsPanel.add(starvedPredatorsLabel);
        statsPanel.add(killedByPredatorsLabel);
        statsPanel.add(grassEatenTotalLabel);

        statsPanel.add(herbivoreCountLabel);
        statsPanel.add(predatorCountLabel);
        statsPanel.add(turnLabel);*/
        jFrame.add(statsPanel, BorderLayout.SOUTH);

        icons.put(Grass.class, loadIcon("/icons/grass.png"));
        icons.put(Rock.class, loadIcon("/icons/rock.png"));
        icons.put(Tree.class, loadIcon("/icons/tree.png"));
        icons.put(Herbivore.class, loadIcon("/icons/herbivore.png"));
        icons.put(Predator.class, loadIcon("/icons/predator.png"));

        initGrid(map);

        jFrame.pack();
        jFrame.setVisible(true);
    }

    public void render(WorldMap map, int turn, Statistic stat) {
        render(map, turn);

        starvedHerbivoresLabel.setText("Herbivores starved: " + stat.getStarvedHerbivores());
        starvedPredatorsLabel.setText("Predators starved: " + stat.getStarvedPredators());
        killedByPredatorsLabel.setText("Herbivores killed: " + stat.getKilledByPredator());
        grassEatenTotalLabel.setText("Grass eaten: " + stat.getTotalGrassEaten());

        jpanel.repaint();
    }

    private void initGrid(WorldMap map) {
        for (int column = 0; column < map.getWidth(); column++) {
            for (int row = 0; row < map.getHeight(); row++) {
                JLabel cell = new JLabel();
                cell.setPreferredSize(new Dimension(cellSize, cellSize));
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                cell.setHorizontalAlignment(SwingConstants.CENTER);

                jpanel.add(cell);
                cells[row][column] = cell;
            }
        }
    }

    private ImageIcon loadIcon(String path) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
        Image scaled = icon.getImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public void render(WorldMap map, int turn) {
        cleanBoard();

/*        int herbivoreCount = 0;
        int predatorCount = 0;*/

        for (Map.Entry<Location, Entity> cell : map.getCells().entrySet()) {
            Location location = cell.getKey();
            Entity clazz = cell.getValue();
            Icon icon = icons.get(clazz.getClass());
            if (/*(location.y() >= 0 && location.y() < map.getHeight()
                    && location.x() >= 0 && location.x() < map.getWidth())
                    &&*/ icon != null) {
                cells[location.y()][location.x()].setIcon(icon);
            }

/*            if (clazz instanceof Herbivore) herbivoreCount++;
            if (clazz instanceof Predator) predatorCount++;
            if (clazz instanceof Grass) predatorCount++;*/
        }

        int herbivoreCount = (int) map.getCells().values().stream()
                .filter(e -> e instanceof Herbivore)
                .count();
        int predatorCount = (int) map.getCells().values().stream()
                .filter(e -> e instanceof Predator)
                .count();

        herbivoreCountLabel.setText("Herbivores: " + herbivoreCount);
        predatorCountLabel.setText("Predators: " + predatorCount);
        turnLabel.setText("Turn: " + turn);

        jpanel.repaint();
    }

    private void cleanBoard() {
        for (JLabel[] cell : cells) {
            for (JLabel jLabel : cell) {
                jLabel.setIcon(null);
            }
        }
    }

    public void showResults(
            long herbivoresLeft,
            long predatorsLeft,
            int initialHerbivores,
            int initialPredators,
            int starvedHerbivores,
            int starvedPredators,
            int killedByPredator,
            Map<String, Integer> killsByPredator,
            Map<String, Integer> grassEatenByHerbivore
    ) {
        System.out.println("==== SIMULATION REPORT ====");

        System.out.println("Herbivores: init=" + initialHerbivores
                + ", left=" + herbivoresLeft
                + ", starved=" + starvedHerbivores
                + ", killedByPredator=" + killedByPredator);

        System.out.println("Predators: init=" + initialPredators
                + ", left=" + predatorsLeft
                + ", starved=" + starvedPredators);

        System.out.println("--- Predator kills ---");
        if (killsByPredator.isEmpty()) {
            System.out.println("none");
        } else {
            killsByPredator.forEach((id, kills) ->
                    System.out.println(id + " killed " + kills + " herbivores"));
        }

        System.out.println("--- Herbivores eaten grass ---");
        if (grassEatenByHerbivore.isEmpty()) {
            System.out.println("none");
        } else {
            grassEatenByHerbivore.forEach((id, count) ->
                    System.out.println(id + " ate " + count + " grasses"));
        }

        System.out.println("===========================");
    }
}
