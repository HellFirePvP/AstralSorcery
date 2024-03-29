/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry.sets;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataSet;
import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreBlockRarityEntry
 * Created by HellFirePvP
 * Date: 01.09.2019 / 00:10
 */
public class OreBlockRarityEntry implements ConfigDataSet {

    private final ITag<Block> blockTag;
    private final ResourceLocation key;
    private final int weight;

    public OreBlockRarityEntry(ITag<Block> blockTag, ResourceLocation key, int weight) {
        this.blockTag = blockTag;
        this.key = key;
        this.weight = weight;
    }

    public OreBlockRarityEntry(ITag.INamedTag<Block> blockTag, int weight) {
        this(blockTag, blockTag.getName(), weight);
    }

    public int getWeight() {
        return weight;
    }

    @Nullable
    public Block getRandomBlock(Random rand) {
        return MiscUtils.getRandomEntry(this.blockTag.getAllElements()
                .stream()
                .filter(item -> !GeneralConfig.CONFIG.modidOreBlacklist.get().contains(item.getRegistryName().getNamespace()))
                .collect(Collectors.toList()), rand);
    }

    @Nullable
    public static OreBlockRarityEntry deserialize(String str) throws IllegalArgumentException {
        String[] split = str.split(";");
        if (split.length != 2) {
            return null;
        }
        ResourceLocation keyBlockTag = new ResourceLocation(split[0]);
        ITag<Block> blockTag = BlockTags.getCollection().get(keyBlockTag);
        if (blockTag == null) {
            return null;
        }
        String strWeight = split[1];
        int weight;
        try {
            weight = Integer.parseInt(strWeight);
        } catch (NumberFormatException exc) {
            return null;
        }
        return new OreBlockRarityEntry(blockTag, keyBlockTag, weight);
    }

    @Nonnull
    @Override
    public String serialize() {
        return String.format("%s;%s", key.toString(), weight);
    }
}
