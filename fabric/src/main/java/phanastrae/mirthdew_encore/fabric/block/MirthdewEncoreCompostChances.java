package phanastrae.mirthdew_encore.fabric.block;

import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.world.level.ItemLike;

import static phanastrae.mirthdew_encore.item.MirthdewEncoreItems.*;

public class MirthdewEncoreCompostChances {

    public static void init() {
        add(0.2F, CLINKERA_SCRAPS);

        add(0.3F, DECIDRHEUM_LEAVES);
        add(0.3F, DECIDRHEUM_SAPLING);
        add(0.3F, RHEUMBRISTLES);
        add(0.3F, ORANGE_FOGHAIR);
        add(0.3F, LIME_FOGHAIR);
        add(0.3F, CYAN_FOGHAIR);
        add(0.3F, MAGNETA_FOGHAIR);

        add(0.65F, SOULSPOT_MUSHRHEUM);

        add(1.0F, DREAMSEED);
    }

    private static void add(float chance, ItemLike item) {
        CompostingChanceRegistry.INSTANCE.add(item, chance);
    }
}
