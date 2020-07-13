/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.tag;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.Tag;

import static hellfirepvp.astralsorcery.common.lib.TagsAS.Blocks.MARBLE;
import static hellfirepvp.astralsorcery.common.lib.TagsAS.Blocks.ORES;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralBlockTagsProvider
 * Created by HellFirePvP
 * Date: 06.03.2020 / 21:23
 */
public class AstralBlockTagsProvider extends BlockTagsProvider {

    public AstralBlockTagsProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerTags() {
        this.tag(MARBLE)
                .add(BlocksAS.MARBLE_RAW)
                .add(BlocksAS.MARBLE_ARCH)
                .add(BlocksAS.MARBLE_BRICKS)
                .add(BlocksAS.MARBLE_CHISELED)
                .add(BlocksAS.MARBLE_ENGRAVED)
                .add(BlocksAS.MARBLE_PILLAR)
                .add(BlocksAS.MARBLE_RUNED);

        this.tag(ORES)
                .add(BlocksAS.STARMETAL_ORE)
                .add(BlocksAS.AQUAMARINE_SAND_ORE)
                .add(BlocksAS.ROCK_CRYSTAL_ORE);
    }

    private Tag.Builder<Block> tag(Tag<Block> tag) {
        return this.getBuilder(tag);
    }
}
