package dev.skinselector.mixin;

import dev.skinselector.client.SkinSelectScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Перехватывает нажатие "Create New World": сначала показываем выбор персонажа. */
@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Unique
    private boolean skinselector$confirmed = false;

    @Shadow
    protected abstract void createLevel();

    @Inject(method = "createLevel", at = @At("HEAD"), cancellable = true)
    private void skinselector$askForSkin(CallbackInfo ci) {
        if (skinselector$confirmed) {
            return; // скин уже выбран - создаём мир как обычно
        }
        ci.cancel();
        Screen self = (Screen) (Object) this;
        MinecraftClient.getInstance().setScreen(new SkinSelectScreen(self, () -> {
            this.skinselector$confirmed = true;
            this.createLevel();
        }));
    }
}
