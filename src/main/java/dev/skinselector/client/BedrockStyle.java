package dev.skinselector.client;

import net.minecraft.client.gui.DrawContext;

final class BedrockStyle {
    private BedrockStyle() {}

    /** Плоский прямоугольник с чёрной рамкой, светлой верхне-левой и тёмной нижне-правой фаской. */
    static void bevel(DrawContext c, int x1, int y1, int x2, int y2, int base, int light, int dark, int border) {
        c.fill(x1, y1, x2, y2, border);
        c.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, base);
        c.fill(x1 + 1, y1 + 1, x2 - 1, y1 + 2, light);
        c.fill(x1 + 1, y1 + 1, x1 + 2, y2 - 1, light);
        c.fill(x1 + 1, y2 - 2, x2 - 1, y2 - 1, dark);
        c.fill(x2 - 2, y1 + 1, x2 - 1, y2 - 1, dark);
    }
}
