package dev.skinselector.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Кнопка в стиле Bedrock Edition: плоская, с фаской. Зелёная (primary) или серая. */
public class BedrockButton extends ButtonWidget {
    private boolean primary;

    public BedrockButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        boolean hover = isHovered();

        int base;
        int light;
        int dark;
        if (primary) {
            base = hover ? 0xFF4C9F34 : 0xFF3C8527;
            light = 0xFF6BBF4A;
            dark = 0xFF1F4F14;
        } else {
            base = hover ? 0xFF6E6F70 : 0xFF58595A;
            light = 0xFF808182;
            dark = 0xFF39393A;
        }
        BedrockStyle.bevel(context, x, y, x + w, y + h, base, light, dark, 0xFF1E1E1F);

        context.drawCenteredTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                getMessage(), x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }
}
