/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlockProperties;
import hellfirepvp.astralsorcery.common.item.*;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedRockCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemRockCrystal;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;

import java.util.List;

import static hellfirepvp.astralsorcery.common.lib.ItemsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryItems
 * Created by HellFirePvP
 * Date: 01.06.2019 / 13:57
 */
public class RegistryItems {

    public static final ItemGroup ITEM_GROUP_AS = new ItemGroup(AstralSorcery.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.APPLE);
        }
    };
    public static final ItemGroup ITEM_GROUP_AS_PAPERS = new ItemGroup(AstralSorcery.MODID + ".papers") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(CONSTELLATION_PAPER);
        }
    };
    public static final ItemGroup ITEM_GROUP_AS_CRYSTALS = new ItemGroup(AstralSorcery.MODID + ".crystals") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ROCK_CRYSTAL);
        }
    };
    public static final Rarity RARITY_CELESTIAL = Rarity.create("AS_CELESTIAL", TextFormatting.BLUE);
    public static final Rarity RARITY_RELIC = Rarity.create("AS_RELIC", TextFormatting.GOLD);

    private static List<ItemDynamicColor> colorItems = Lists.newArrayList();

    private RegistryItems() {}

    public static void registerItems() {
        AQUAMARINE = registerItem(new ItemAquamarine());
        RESONATING_GEM = registerItem(new ItemResonatingGem());
        GLASS_LENS = registerItem(new ItemGlassLens());
        PARCHMENT = registerItem(new ItemParchment());
        STARMETAL_INGOT = registerItem(new ItemStarmetalIngot());
        STARDUST = registerItem(new ItemStardust());

        CONSTELLATION_PAPER = registerItem(new ItemConstellationPaper());

        ROCK_CRYSTAL = registerItem(new ItemRockCrystal());
        ATTUNED_ROCK_CRYSTAL = registerItem(new ItemAttunedRockCrystal());
        CELESTIAL_CRYSTAL = registerItem(new ItemCelestialCrystal());
        ATTUNED_CELESTIAL_CRYSTAL = registerItem(new ItemAttunedCelestialCrystal());
    }

    public static void registerItemBlocks() {
        RegistryBlocks.defaultItemBlocks.forEach(RegistryItems::registerItemBlock);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerColors(ColorHandlerEvent.Item itemColorEvent) {
        colorItems.forEach(item -> itemColorEvent.getItemColors().register(item::getColor, (Item) item));
    }

    private static void registerItemBlock(CustomItemBlock block) {
        BlockItem itemBlock = block.createItemBlock(buildItemBlockProperties((Block) block));
        itemBlock.setRegistryName(itemBlock.getBlock().getRegistryName());
        AstralSorcery.getProxy().getRegistryPrimer().register(itemBlock);
    }

    private static <T extends Item> T registerItem(T item) {
        ResourceLocation name = createItemName(item);
        item.setRegistryName(name);
        AstralSorcery.getProxy().getRegistryPrimer().register(item);
        if (item instanceof ItemDynamicColor) {
            colorItems.add((ItemDynamicColor) item);
        }
        return item;
    }

    private static ResourceLocation createItemName(Item item) {
        String name = item.getClass().getSimpleName();
        if (name.startsWith("Item")) {
            name = name.substring(4);
        }
        name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
        return new ResourceLocation(AstralSorcery.MODID, name);
    }

    private static Item.Properties buildItemBlockProperties(Block block) {
        Item.Properties props = new Item.Properties();
        props.group(ITEM_GROUP_AS);
        if (block instanceof CustomItemBlockProperties) {
            ItemGroup group = ((CustomItemBlockProperties) block).getItemGroup();
            if (group != null) {
                props.group(group);
            }
            if (!((CustomItemBlockProperties) block).canItemBeRepaired()) {
                props.setNoRepair();
            }

            props.rarity(((CustomItemBlockProperties) block).getItemRarity());
            props.maxStackSize(((CustomItemBlockProperties) block).getItemMaxStackSize());
            props.defaultMaxDamage(((CustomItemBlockProperties) block).getItemMaxDamage());
            props.containerItem(((CustomItemBlockProperties) block).getContainerItem());
            props.setTEISR(((CustomItemBlockProperties) block).getItemTEISR());

            ((CustomItemBlockProperties) block).getItemToolLevels().forEach(props::addToolType);
        }
        return props;
    }

}
