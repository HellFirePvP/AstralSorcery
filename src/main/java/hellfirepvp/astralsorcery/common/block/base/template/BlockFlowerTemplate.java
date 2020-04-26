/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base.template;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFlowerTemplate
 * Created by HellFirePvP
 * Date: 23.04.2020 / 18:16
 */
public abstract class BlockFlowerTemplate extends FlowerBlock implements CustomItemBlock {

    public BlockFlowerTemplate(Properties properties) {
        super(Effects.INSTANT_HEALTH, 0, properties);
    }

    @Override
    @Nonnull
    public abstract Effect getStewEffect();

    @Override
    public abstract int getStewEffectDuration();
}
