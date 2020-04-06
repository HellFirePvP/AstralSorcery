/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.config.registry.OreItemRarityRegistry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
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

    private static final float defaultOreChance = 0.001F;
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

    public KeyVoidTrash(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.config = new Config(name.getPath());
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);

        bus.addListener(this::onDrops);
    }

    @Nullable
    @Override
    protected ConfigEntry addConfig() {
        return this.config;
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, PlayerEntity player) {
        return false; //TODO harvest drops still not functional; don't allow allocation.
    }

    //TODO harvest drops non functional
    private void onDrops(BlockEvent.HarvestDropsEvent event) {
        IWorld world = event.getWorld();
        if (world.isRemote()) {
            return;
        }

        PlayerEntity player = event.getHarvester();
        if (player == null) {
            return;
        }

        PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!prog.hasPerkEffect(this)) {
            return;
        }

        float chance = (float) this.applyMultiplierD(this.config.oreChance.get());
        chance *= PerkAttributeHelper.getOrCreateMap(player, LogicalSide.SERVER)
                .getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT);

        List<ItemStack> drops = event.getDrops();
        List<ItemStack> addedDrops = Lists.newArrayList();
        Iterator<ItemStack> iterator = drops.iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            if (stack.isEmpty()) {
                continue;
            }
            if (this.config.isTrash(stack)) {
                iterator.remove();

                if (rand.nextFloat() < chance) {
                    Item drop = OreItemRarityRegistry.VOID_TRASH_REWARD.getRandomItem(rand);
                    if (drop != null) {
                        addedDrops.add(new ItemStack(drop));
                    }
                }
            }
        }
        drops.addAll(addedDrops);
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

        private boolean isTrash(ItemStack stack) {
            String key = stack.getItem().getRegistryName().toString();
            return this.trashItems.get().contains(key);
        }
    }
}
