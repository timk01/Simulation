package org.simulation;

import org.entity.*;
import org.map.Location;
import org.map.WorldMap;
import org.simulation.Action.Statistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Renderer {
    private static final Logger log = LoggerFactory.getLogger(Renderer.class);


    private final Map<Class<? extends Entity>, Icon> icons = new HashMap<>();
    private int cellSize;
    private final JLabel[][] cells;
    private final JFrame jFrame;
    private final JPanel gridPanel;
    private JScrollPane scroll;
    private final JLabel herbivoreCountLabel;
    private final JLabel predatorCountLabel;
    private final JLabel grassCountLabel;
    private final JLabel turnLabel;

    private final JLabel starvedHerbivoresLabel;
    private final JLabel starvedPredatorsLabel;
    private final JLabel killedByPredatorsLabel;
    private final JLabel grassEatenTotalLabel;
    private Runnable onClose;

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    private ImageIcon loadIcon(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            throw new IllegalStateException("Missing icon resource: " + path);
        }
        ImageIcon icon = new ImageIcon(url);
        Image scaled = icon.getImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void reloadIcons() {
        icons.put(Grass.class, loadIcon("/icons/grass.png"));
        icons.put(Rock.class, loadIcon("/icons/rock.png"));
        icons.put(Tree.class, loadIcon("/icons/tree.png"));
        icons.put(Herbivore.class, loadIcon("/icons/herbivore.png"));
        icons.put(Predator.class, loadIcon("/icons/predator.png"));
    }

    private void initGrid(WorldMap map) {
        for (int column = 0; column < map.getWidth(); column++) {
            for (int row = 0; row < map.getHeight(); row++) {
                JLabel cell = new JLabel();
                cell.setPreferredSize(new Dimension(cellSize, cellSize));
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                cell.setHorizontalAlignment(SwingConstants.CENTER);

                gridPanel.add(cell);
                cells[row][column] = cell;
            }
        }
    }

    private void addZoomControls() {
        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JButton minus = new JButton("−");
        JButton plus = new JButton("+");

        minus.addActionListener(e -> setCellSize(cellSize - 4));
        plus.addActionListener(e -> setCellSize(cellSize + 4));

        zoomPanel.add(new JLabel("Zoom:"));
        zoomPanel.add(minus);
        zoomPanel.add(plus);

        jFrame.add(zoomPanel, BorderLayout.NORTH);
    }

    private void setCellSize(int newSize) {
        int clamped = Math.max(8, Math.min(64, newSize));
        if (clamped == cellSize) {
            return;
        }

        cellSize = clamped;

        for (JLabel[] row : cells) {
            for (JLabel lbl : row) {
                lbl.setPreferredSize(new Dimension(cellSize, cellSize));
            }
        }

        reloadIcons();

        gridPanel.revalidate();
        gridPanel.repaint();
        scroll.getVerticalScrollBar().setUnitIncrement(cellSize);
        scroll.getHorizontalScrollBar().setUnitIncrement(cellSize);
        jFrame.pack();
    }

    public Renderer(WorldMap map) {
        this.cellSize = 32;
        this.cells = new JLabel[map.getHeight()][map.getWidth()];

        jFrame = new JFrame("Simulation");
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        gridPanel = new JPanel(new GridLayout(map.getHeight(), map.getWidth()));
        scroll = new JScrollPane(gridPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(cellSize);
        scroll.getHorizontalScrollBar().setUnitIncrement(cellSize);
        jFrame.add(scroll, BorderLayout.CENTER);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        herbivoreCountLabel = new JLabel("Herbivores: 0");
        predatorCountLabel = new JLabel("Predators: 0");
        grassCountLabel = new JLabel("Grass: 0");
        turnLabel = new JLabel("Turn: 0");

        JLabel sep1 = new JLabel("  |  ");
        JLabel sep2 = new JLabel("  |  ");
        JLabel sep3 = new JLabel("  |  ");

        row1.add(herbivoreCountLabel);
        row1.add(sep1);
        row1.add(predatorCountLabel);
        row1.add(sep2);
        row1.add(grassCountLabel);
        row1.add(sep3);
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

        jFrame.add(statsPanel, BorderLayout.SOUTH);

        reloadIcons();

        initGrid(map);

        addZoomControls();

        jFrame.pack();
        jFrame.setAutoRequestFocus(false);
        dockRightSide();
        jFrame.setAlwaysOnTop(true);
        jFrame.setVisible(true);
        log.info("Renderer window created: map {}x{}, cellSize={}, bounds={}",
                map.getWidth(), map.getHeight(), cellSize, jFrame.getBounds());

        jFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                log.info("Renderer windowClosing event");
                if (onClose != null) {
                    onClose.run();
                }
            }
        });
    }

    private void dockRightSide() {
        Rectangle s = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        int w = Math.min(jFrame.getWidth(), s.width / 2);
        int h = Math.min(jFrame.getHeight(), s.height - 40);
        jFrame.setBounds(s.x + s.width - w, s.y + 20, w, h);
    }

    private void cleanBoard() {
        for (JLabel[] cell : cells) {
            for (JLabel jLabel : cell) {
                jLabel.setIcon(null);
            }
        }
    }

    private void updateBoard(WorldMap map) {
        for (Map.Entry<Location, Entity> cell : map.getCells().entrySet()) {
            Location location = cell.getKey();
            Entity clazz = cell.getValue();
            Icon icon = icons.get(clazz.getClass());
            if (icon != null) {
                cells[location.y()][location.x()].setIcon(icon);
            }
        }
    }

    private void updateCounters(WorldMap map, int turn) {
        int herbivoreCount = (int) map.getCells().values().stream()
                .filter(e -> e instanceof Herbivore)
                .count();
        int predatorCount = (int) map.getCells().values().stream()
                .filter(e -> e instanceof Predator)
                .count();
        int grassCount = (int) map.getCells().values().stream()
                .filter(e -> e instanceof Grass)
                .count();

        herbivoreCountLabel.setText("Herbivores: " + herbivoreCount);
        predatorCountLabel.setText("Predators: " + predatorCount);
        grassCountLabel.setText("Grass: " + grassCount);
        turnLabel.setText("Turn: " + turn);
    }

    private void updateStats(Statistic stat) {
        starvedHerbivoresLabel.setText("Herbivores starved: " + stat.getStarvedHerbivores());
        starvedPredatorsLabel.setText("Predators starved: " + stat.getStarvedPredators());
        killedByPredatorsLabel.setText("Herbivores killed: " + stat.getKilledByPredator());
        grassEatenTotalLabel.setText("Grass eaten: " + stat.getTotalGrassEaten());
    }

    public void render(WorldMap map, int turn, Statistic stat) {
        cleanBoard();

        updateBoard(map);
        updateCounters(map, turn);
        updateStats(stat);

        gridPanel.repaint();

        log.debug("Render frame: turn={}", turn);
    }

    public void dispose() {
        log.info("Renderer dispose requested");
        SwingUtilities.invokeLater(jFrame::dispose);
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
        log.info("==== SIMULATION REPORT ====");
        log.info("Herbivores: init={}, left={}, starved={}, killedByPredator={}",
                initialHerbivores, herbivoresLeft, starvedHerbivores, killedByPredator);
        log.info("Predators: init={}, left={}, starved={}",
                initialPredators, predatorsLeft, starvedPredators);

        log.info("--- Predator kills ---");
        if (killsByPredator.isEmpty()) {
            log.info("none");
        } else {
            killsByPredator.forEach((id, kills) ->
                    log.info("{} killed {} herbivores", id, kills));
        }

        log.info("--- Herbivores eaten grass ---");
        if (grassEatenByHerbivore.isEmpty()) {
            log.info("none");
        } else {
            grassEatenByHerbivore.forEach((id, count) ->
                    log.info("{} ate {} grasses", id, count));
        }
        log.info("===========================");
    }
}
