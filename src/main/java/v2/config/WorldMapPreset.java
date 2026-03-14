package v2.config;

public enum WorldMapPreset {
    SMALL(12, 12),
    MEDIUM(20, 20),
    LARGE(30, 30);

    private int width;
    private int height;

    WorldMapPreset(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
