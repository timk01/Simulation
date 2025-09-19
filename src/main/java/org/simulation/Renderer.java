package org.simulation;

import org.map.WorldMap;

import javax.swing.*;
import java.awt.*;

public class Renderer {

    private final int size;
    private final int cellSize;

    private JFrame jFrame;
    private JPanel jpanel;

    private final JLabel[][] cells;

    public Renderer(int size) {
        this.size = size;
        this.cellSize = 30;
        this.cells = new JLabel[size][size];

        JFrame frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jpanel = new JPanel(new GridLayout(size, size));
        frame.add(jpanel);

        initGrid();

        frame.pack();
        frame.setVisible(true);
    }

    private void initGrid() {
        for (int column = 0; column < size; column++) {
            for (int row = 0; row < size; row++) {
                JLabel cell = new JLabel();
                cell.setPreferredSize(new Dimension(cellSize, cellSize));
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                cell.setHorizontalAlignment(SwingConstants.CENTER);

                jpanel.add(cell);
                cells[column][row] = cell;
            }
        }
    }

    public void render(WorldMap map) {
    }
}
