package dev.skinselector.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.WorldSavePath;

import java.nio.file.Path;

public class SkinSelectorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SkinSelection.load();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            IntegratedServer server = client.getServer();
            if (server != null) {
                Path root = server.getSavePath(WorldSavePath.ROOT).toAbsolutePath().normalize();
                Path name = root.getFileName();
                SkinSelection.onWorldJoin(name == null ? "world" : name.toString());
            } else {
                SkinSelection.clearActive();
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> SkinSelection.clearActive());
    }
}
