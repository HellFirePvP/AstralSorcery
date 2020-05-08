/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry.sets;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataSet;
import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreItemRarityEntry
 * Created by HellFirePvP
 * Date: 31.08.2019 / 23:46
 */
public class OreItemRarityEntry implements ConfigDataSet {

    private final Tag<Item> itemTag;
    private final int weight;

    public OreItemRarityEntry(Tag<Item> itemTag, int weight) {
        this.itemTag = itemTag;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    @Nullable
    public Item getRandomItem(Random rand) {
        return MiscUtils.getRandomEntry(this.itemTag.getAllElements()
                .stream()
                .filter(item -> !GeneralConfig.CONFIG.modidOreBlacklist.get().contains(item.getRegistryName().getNamespace()))
                .collect(Collectors.toList()), rand);
    }

    @Nullable
    public static OreItemRarityEntry deserialize(String str) throws IllegalArgumentException {
        String[] split = str.split(";");
        if (split.length != 2) {
            return null;
        }
        ResourceLocation keyItemTag = new ResourceLocation(split[0]);
        Tag<Item> itemTag = ItemTags.getCollection().get(keyItemTag);
        if (itemTag == null) {
            return null;
        }
        String strWeight = split[1];
        int weight;
        try {
            weight = Integer.parseInt(strWeight);
        } catch (NumberFormatException exc) {
            return null;
        }
        return new OreItemRarityEntry(itemTag, weight);
    }

    @Nonnull
    @Override
    public String serialize() {
        return itemTag.getId().toString() + ";" + weight;
    }
}
