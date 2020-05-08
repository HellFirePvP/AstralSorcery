/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyVoidTrash
 * Created by HellFirePvP
 * Date: 31.08.2019 / 23:11
 */
public class KeyVoidTrash extends KeyPerk {

    private static final float defaultOreChance = 0.02F;
    private static final List<Item> defaultTrashItems = new ArrayList<Item>() {
        {
            add(Blocks.DIRT.asItem());
            add(Blocks.COBBLESTONE.asItem());
            add(Blocks.ANDESITE.asItem());
            add(Blocks.DIORITE.asItem());
            add(Blocks.GRANITE.asItem());
            add(Blocks.STONE.asItem());
            add(Blocks.GRAVEL.asItem());
        }
    };

    private final Config config;

    /**
     * @see hellfirepvp.astralsorcery.common.loot.global.LootModifierPerkVoidTrash
     */
    public KeyVoidTrash(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.config = new Config(name.getPath());
    }

    @Nullable
    @Override
    protected ConfigEntry addConfig() {
        return this.config;
    }

    public Config getConfig() {
        return config;
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.DoubleValue oreChance;
        private ForgeConfigSpec.ConfigValue<List<String>> trashItems;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.trashItems = cfgBuilder
                    .comment("List items that should count as trash and should be voided.")
                    .translation(translationKey("trashItems"))
                    .define("trashItems", defaultTrashItems.stream()
                            .map(item -> item.getRegistryName().toString())
                            .collect(Collectors.toList()));

            this.oreChance = cfgBuilder
                    .comment("Chance that a voided drop will instead yield a random ore out of the configured ore table.")
                    .translation(translationKey("oreChance"))
                    .defineInRange("oreChance", defaultOreChance, 0F, 1F);
        }

        public boolean isTrash(ItemStack stack) {
            String key = stack.getItem().getRegistryName().toString();
            return this.trashItems.get().contains(key);
        }

        public double getOreChance() {
            return oreChance.get();
        }
    }
}
