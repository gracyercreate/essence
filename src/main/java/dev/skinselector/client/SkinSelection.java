package dev.skinselector.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Хранит выбор скина: для каждого мира (по имени папки) своя запись в config/skinselector.json.
 * Только что выбранный скин ("pending") привязывается к первому миру, в который вы зайдёте.
 */
public final class SkinSelection {
    public static final String MOD_ID = "skinselector";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, String> WORLD_SKINS = new LinkedHashMap<>();

    private static String pendingKey = null;
    private static volatile SkinTextures.SkinOverride activeOverride = null;

    private SkinSelection() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json");
    }

    public static void load() {
        WORLD_SKINS.clear();
        Path f = file();
        if (!Files.exists(f)) return;
        try {
            String text = Files.readString(f, StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(text).getAsJsonObject();
            JsonObject worlds = root.has("worlds") ? root.getAsJsonObject("worlds") : new JsonObject();
            for (Map.Entry<String, JsonElement> e : worlds.entrySet()) {
                String v = e.getValue().getAsString();
                if (SkinOption.isValidKey(v)) WORLD_SKINS.put(e.getKey(), v);
            }
        } catch (Exception ex) {
            System.err.println("[SkinSelector] Could not read config: " + ex);
        }
    }

    private static void save() {
        try {
            JsonObject root = new JsonObject();
            JsonObject worlds = new JsonObject();
            for (Map.Entry<String, String> e : WORLD_SKINS.entrySet()) worlds.addProperty(e.getKey(), e.getValue());
            root.add("worlds", worlds);
            Files.createDirectories(file().getParent());
            Files.writeString(file(), GSON.toJson(root), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.err.println("[SkinSelector] Could not save config: " + ex);
        }
    }

    /** Вызывается, когда игрок нажал Confirm в окне выбора. */
    public static void setPending(String fullKey) {
        pendingKey = SkinOption.isValidKey(fullKey) ? fullKey : null;
    }

    /** Вызывается при входе в одиночный мир (folderName - имя папки мира в saves). */
    public static void onWorldJoin(String folderName) {
        if (pendingKey != null) {
            WORLD_SKINS.put(folderName, pendingKey);
            pendingKey = null;
            save();
        }
        String key = WORLD_SKINS.get(folderName);
        activeOverride = key == null ? null : buildOverride(key);
    }

    /** Мультиплеер / выход из мира: ванильный скин. */
    public static void clearActive() {
        activeOverride = null;
    }

    public static SkinTextures.SkinOverride getActiveOverride() {
        return activeOverride;
    }

    private static SkinTextures.SkinOverride buildOverride(String fullKey) {
        boolean slim = fullKey.endsWith("_slim");
        // id "skinselector:skins/sunny_wide" -> файл "skinselector:textures/skins/sunny_wide.png"
        Identifier id = Identifier.of(MOD_ID, "skins/" + fullKey);
        return new SkinTextures.SkinOverride(
                Optional.of(new AssetInfo.TextureAssetInfo(id)),
                Optional.empty(),
                Optional.empty(),
                Optional.of(slim ? PlayerSkinType.SLIM : PlayerSkinType.WIDE)
        );
    }
}
