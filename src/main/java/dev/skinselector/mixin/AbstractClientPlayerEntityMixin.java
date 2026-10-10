package dev.skinselector.mixin;

import dev.skinselector.client.SkinSelection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Подменяет скин и тип модели (wide/slim) только у локального игрока в одиночной игре. */
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void skinselector$applySkin(CallbackInfoReturnable<SkinTextures> cir) {
        SkinTextures.SkinOverride override = SkinSelection.getActiveOverride();
        if (override == null) return;
        if ((Object) this != MinecraftClient.getInstance().player) return;
        cir.setReturnValue(cir.getReturnValue().withOverride(override));
    }
}
