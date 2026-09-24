/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinLevelChunk
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(LevelChunk.class)
public class MixinLevelChunk {

    @Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;setRemoved()V"))
    public void additionalRemoval(BlockPos pos, CallbackInfo ci, @Local BlockEntity toRemove) {
        if (toRemove instanceof TileEntitySynchronized<?> tile) {
            tile.onTileEntityRemove(tile.getLevel(), pos);
        }
    }

    @Inject(method = "setBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;setRemoved()V"))
    public void overwriteBlockEntityAdditionalRemoval(BlockEntity newBlockEntity, CallbackInfo ci, @Local(ordinal = 1) BlockEntity toRemove) {
        if (toRemove instanceof TileEntitySynchronized<?> tile) {
            tile.onTileEntityRemove(newBlockEntity.getLevel(), newBlockEntity.getBlockPos());
        }
    }
}
