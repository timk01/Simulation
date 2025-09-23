package org.simulation;

import org.entity.*;
import org.map.Location;
import org.map.WorldMap;

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

    public Renderer(WorldMap map) {
        this.cellSize = 32;
        this.cells = new JLabel[map.getHeight()][map.getWidth()];

        jFrame = new JFrame("Simulation");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        jpanel = new JPanel(new GridLayout(map.getHeight(), map.getWidth()));
        jFrame.add(jpanel);

        JPanel statsPanel = new JPanel(new FlowLayout());
        herbivoreCountLabel = new JLabel("Herbivores: 0");
        predatorCountLabel = new JLabel("Predators: 0");
        turnLabel = new JLabel("Turn: 0");
        statsPanel.add(herbivoreCountLabel);
        statsPanel.add(predatorCountLabel);
        statsPanel.add(turnLabel);
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

        int herbivoreCount = 0;
        int predatorCount = 0;

        for (Map.Entry<Location, Entity> cell : map.getCells().entrySet()) {
            Location location = cell.getKey();
            Entity clazz = cell.getValue();
            Icon icon = icons.get(clazz.getClass());
            if (icon != null) {
                cells[location.y()][location.x()].setIcon(icon);
            }

            if (clazz instanceof Herbivore) herbivoreCount++;
            if (clazz instanceof Predator) predatorCount++;
        }
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
}
