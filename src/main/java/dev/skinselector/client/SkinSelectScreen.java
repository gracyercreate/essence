package dev.skinselector.client;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Окно выбора персонажа в стиле Bedrock Edition.
 * Стрелки < > листают скины, Wide/Slim переключает тип рук, Confirm подтверждает.
 * Esc - отмена (возврат на экран создания мира, мир не создаётся).
 */
public class SkinSelectScreen extends Screen {
    private static final SkinOption[] OPTIONS = SkinOption.values();

    private final Screen parent;
    private final Runnable onConfirm;

    private int index = 0;
    private boolean slim = false;

    private BedrockButton wideButton;
    private BedrockButton slimButton;

    // раскладка (считается в init, поэтому подстраивается под размер окна)
    private int panelL, panelT, panelR, panelB;
    private int previewT, previewB, previewL, previewR;
    private int nameY, scale, charTop;
    private boolean compact;

    public SkinSelectScreen(Screen parent, Runnable onConfirm) {
        super(Text.literal("Choose Your Character"));
        this.parent = parent;
        this.onConfirm = onConfirm;
    }

    @Override
    protected void init() {
        int panelW = Math.min(width - 16, 280);
        int panelH = Math.min(height - 16, 290);
        panelL = (width - panelW) / 2;
        panelT = (height - panelH) / 2;
        panelR = panelL + panelW;
        panelB = panelT + panelH;
        compact = panelH < 170;

        final int btnH = 20;
        final int gap = 4;
        int rowY = panelB - (compact ? 6 : 10) - btnH;
        int inner = panelW - 20;
        int confirmW = Math.max(56, inner / 2);
        int toggleW = Math.max(30, (inner - confirmW - 2 * gap) / 2);
        int rowW = confirmW + gap + toggleW * 2 + gap;
        int rowL = panelL + (panelW - rowW) / 2;

        nameY = rowY - (compact ? 12 : 16);
        previewT = panelT + (compact ? 16 : 26);
        previewB = nameY - 4;
        previewL = panelL + 36;
        previewR = panelR - 36;

        scale = Math.max(1, Math.min(7, (previewB - previewT - 4) / 32));
        charTop = previewT + Math.max(0, (previewB - previewT - 32 * scale) / 2);

        int arrowY = (previewT + previewB) / 2 - 10;
        addDrawableChild(new BedrockButton(panelL + 10, arrowY, 20, 20, Text.literal("<"), b -> step(-1)));
        addDrawableChild(new BedrockButton(panelR - 30, arrowY, 20, 20, Text.literal(">"), b -> step(1)));

        BedrockButton confirm = new BedrockButton(rowL, rowY, confirmW, btnH, Text.literal("Confirm"), b -> confirm());
        confirm.setPrimary(true);
        addDrawableChild(confirm);

        wideButton = new BedrockButton(rowL + confirmW + gap, rowY, toggleW, btnH, Text.literal("Wide"), b -> setSlim(false));
        slimButton = new BedrockButton(rowL + confirmW + gap + toggleW + gap, rowY, toggleW, btnH, Text.literal("Slim"), b -> setSlim(true));
        addDrawableChild(wideButton);
        addDrawableChild(slimButton);
        refreshToggle();
    }

    private void step(int dir) {
        index = (index + dir + OPTIONS.length) % OPTIONS.length;
    }

    private void setSlim(boolean value) {
        slim = value;
        refreshToggle();
    }

    private void refreshToggle() {
        wideButton.setPrimary(!slim);
        slimButton.setPrimary(slim);
    }

    private void confirm() {
        SkinSelection.setPending(OPTIONS[index].fullKey(slim));
        client.setScreen(parent);
        onConfirm.run();
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    // Фон, панель и превью рисуются здесь: renderBackground вызывается до кнопок.
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);

        // главная панель
        BedrockStyle.bevel(context, panelL, panelT, panelR, panelB, 0xFF313233, 0xFF48494A, 0xFF242425, 0xFF1E1E1F);

        // заголовок
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, panelT + (compact ? 5 : 9), 0xFFFFFFFF);

        // рамка превью
        context.fill(previewL, previewT, previewR, previewB, 0xFF0F0F0F);
        context.fill(previewL + 1, previewT + 1, previewR - 1, previewB - 1, 0xFF1B1B1C);

        // тень под ногами
        int cx = (previewL + previewR) / 2;
        int feetY = charTop + 32 * scale;
        context.fill(cx - 7 * scale, feetY, cx + 7 * scale, feetY + Math.max(1, scale / 2), 0x66000000);

        // персонаж
        SkinOption option = OPTIONS[index];
        Identifier tex = Identifier.of(SkinSelection.MOD_ID, "textures/skins/" + option.fullKey(slim) + ".png");
        drawCharacter(context, tex, cx, charTop, scale, slim);

        // имя и номер
        Text label = Text.literal(option.displayName + "  " + (index + 1) + "/" + OPTIONS.length);
        context.drawCenteredTextWithShadow(textRenderer, label, width / 2, nameY, 0xFFFFFFFF);
    }

    /** Вид спереди: голова 8x8, тело 8x12, руки 4x12 (slim: 3x12), ноги 4x12; сначала база, потом второй слой. */
    private static void drawCharacter(DrawContext c, Identifier tex, int cx, int top, int s, boolean slim) {
        int armW = slim ? 3 : 4;
        int bodyX = cx - 4 * s;

        for (int layer = 0; layer < 2; layer++) {
            boolean o = layer == 1;
            // голова / шляпа
            part(c, tex, bodyX, top, s, o ? 40 : 8, 8, 8, 8);
            // тело / куртка
            part(c, tex, bodyX, top + 8 * s, s, 20, o ? 36 : 20, 8, 12);
            // правая рука персонажа (слева на экране) / рукав
            part(c, tex, bodyX - armW * s, top + 8 * s, s, 44, o ? 36 : 20, armW, 12);
            // левая рука персонажа (справа на экране) / рукав
            part(c, tex, bodyX + 8 * s, top + 8 * s, s, o ? 52 : 36, 52, armW, 12);
            // правая нога / штанина
            part(c, tex, bodyX, top + 20 * s, s, 4, o ? 36 : 20, 4, 12);
            // левая нога / штанина
            part(c, tex, bodyX + 4 * s, top + 20 * s, s, o ? 4 : 20, 52, 4, 12);
        }
    }

    private static void part(DrawContext c, Identifier tex, int x, int y, int s, int u, int v, int w, int h) {
        c.drawTexture(RenderPipelines.GUI_TEXTURED, tex, x, y, (float) u, (float) v, w * s, h * s, w, h, 64, 64);
    }
}
