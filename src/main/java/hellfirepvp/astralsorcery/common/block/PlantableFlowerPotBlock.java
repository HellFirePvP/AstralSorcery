/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PlantableFlowerPotBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PlantableFlowerPotBlock extends FlowerPotBlock {

    public PlantableFlowerPotBlock(DeferredBlock<?> flower, Properties properties) {
        super(null, flower, properties);
        if (Blocks.FLOWER_POT instanceof FlowerPotBlock flowerPotBlock) {
            flowerPotBlock.addPlant(flower.getId(), () -> this);
        }
    }
}
