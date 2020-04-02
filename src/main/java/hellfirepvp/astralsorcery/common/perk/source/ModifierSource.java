/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModifierSource
 * Created by HellFirePvP
 * Date: 31.03.2020 / 20:45
 */
public interface ModifierSource {

    boolean canApplySource(PlayerEntity player, LogicalSide dist);

    void onRemove(PlayerEntity player, LogicalSide dist);

    void onApply(PlayerEntity player, LogicalSide dist);

    boolean isEqual(ModifierSource other);

    ResourceLocation getProviderName();

}
