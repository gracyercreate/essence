package dev.skinselector.client;

/** Девять официальных скинов. Файлы: assets/skinselector/textures/skins/<key>_wide.png и <key>_slim.png */
public enum SkinOption {
    SUNNY("sunny", "Sunny"),
    KAI("kai", "Kai"),
    MAKENA("makena", "Makena"),
    STEVE("steve", "Steve"),
    ALEX("alex", "Alex"),
    ZURI("zuri", "Zuri"),
    EFE("efe", "Efe"),
    ARI("ari", "Ari"),
    NOOR("noor", "Noor");

    public final String key;
    public final String displayName;

    SkinOption(String key, String displayName) {
        this.key = key;
        this.displayName = displayName;
    }

    /** Например "sunny_wide" или "kai_slim". */
    public String fullKey(boolean slim) {
        return key + (slim ? "_slim" : "_wide");
    }

    public static boolean isValidKey(String fullKey) {
        if (fullKey == null) return false;
        for (SkinOption o : values()) {
            if (fullKey.equals(o.fullKey(false)) || fullKey.equals(o.fullKey(true))) return true;
        }
        return false;
    }
}
