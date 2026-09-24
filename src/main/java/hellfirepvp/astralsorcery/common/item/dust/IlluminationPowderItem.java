/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.dust;

import hellfirepvp.astralsorcery.common.entity.EntityIlluminationSpark;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IlluminationPowderItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class IlluminationPowderItem extends UsableDustItem {

    public IlluminationPowderItem() {
        super(new Properties());
    }

    @Override
    public boolean dispenseItem(BlockSource dispenser) {
        if (!dispenser.state().hasProperty(DirectionalBlock.FACING)) {
            return false;
        }
        Direction dir = dispenser.state().getValue(DirectionalBlock.FACING);
        Vec3 at = dispenser.center().add(Vec3.atLowerCornerOf(dir.getNormal()));
        EntityIlluminationSpark spark = new EntityIlluminationSpark(at.x(), at.y(), at.z(), dispenser.level());
        spark.shoot(dir.getStepX(), dir.getStepY() + 0.1F, dir.getStepZ(), 0.7F, 0F);
        return dispenser.level().addFreshEntity(spark);
    }

    @Override
    public boolean useAir(ServerLevel sLevel, ServerPlayer sPlayer, ItemStack dust) {
        return sLevel.addFreshEntity(new EntityIlluminationSpark(sPlayer, sLevel));
    }

    @Override
    public boolean useBlock(ServerLevel sLevel, ServerPlayer sPlayer, UseOnContext ctx) {
        BlockPos placePos = ctx.getClickedPos();
        if (!BlockUtil.isReplaceable(sLevel, placePos)) {
            placePos = placePos.relative(ctx.getClickedFace());
        }
        if (!BlockUtil.isReplaceable(sLevel, placePos)) {
            return false;
        }

        if (sPlayer.mayUseItemAt(placePos, ctx.getClickedFace(), ctx.getItemInHand()) && !EventHooks.onBlockPlace(sPlayer, BlockSnapshot.create(sLevel.dimension(), sLevel, placePos), ctx.getClickedFace())) {
            return sLevel.setBlockAndUpdate(placePos, BlocksAS.FLARE_LIGHT.get().defaultBlockState());
        }
        return false;
    }
}
