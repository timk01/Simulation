package simulation.config;

public enum WorldMapPreset {
    SMALL(12, 12),
    MEDIUM(20, 20),
    LARGE(30, 30);

    private final int width;
    private final int height;

    WorldMapPreset(int width, int height) {
        this.width = width;
        this.height = height;
        validateMapSize();
    }

    private void validateMapSize() {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("maps size quantities must be positive");
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
