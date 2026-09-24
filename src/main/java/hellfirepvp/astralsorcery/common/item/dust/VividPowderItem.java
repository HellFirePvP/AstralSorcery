/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.dust;

import hellfirepvp.astralsorcery.common.entity.EntityVividSpark;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VividPowderItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VividPowderItem extends UsableDustItem {

    public VividPowderItem() {
        super(new Properties());
    }

    @Override
    public boolean dispenseItem(BlockSource dispenser) {
        return false;
    }

    @Override
    public boolean useAir(ServerLevel sLevel, ServerPlayer sPlayer, ItemStack dust) {
        return sLevel.addFreshEntity(new EntityVividSpark(sPlayer, sLevel));
    }

    @Override
    public boolean useBlock(ServerLevel sLevel, ServerPlayer sPlayer, UseOnContext ctx) {
        BlockPos at = ctx.getClickedPos().relative(ctx.getClickedFace());
        EntityVividSpark vivid = new EntityVividSpark(sPlayer, sLevel);
        vivid.startGrowing(at.getCenter());
        return sLevel.addFreshEntity(vivid);
    }
}
