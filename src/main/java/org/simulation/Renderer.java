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

    public Renderer(WorldMap map, int cellSize) {
        this.cellSize = 32;
        this.cells = new JLabel[map.getWidth()][map.getHeight()];

        jFrame = new JFrame("Simulation");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jpanel = new JPanel(new GridLayout(map.getWidth(), map.getHeight()));
        jFrame.add(jpanel);

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
                cells[column][row] = cell;
            }
        }
    }

    private ImageIcon loadIcon(String path) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
        Image scaled = icon.getImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public void render(WorldMap map) {
        cleanBoard();

        for (Map.Entry<Location, Entity> cell : map.getCells().entrySet()) {
            Location location = cell.getKey();
            Entity clazz = cell.getValue();
            Icon icon = icons.get(clazz.getClass());
            if (icon != null) {
                cells[location.getX()][location.getY()].setIcon(icon);
            }
        }
        jpanel.revalidate();
        jpanel.repaint();
    }

    private void cleanBoard() {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y].setIcon(null);
            }
        }
    }
}
