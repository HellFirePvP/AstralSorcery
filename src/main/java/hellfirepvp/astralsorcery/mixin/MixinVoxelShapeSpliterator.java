/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2021
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.util.collision.CollisionHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapeSpliterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinVoxelShapeSpliterator
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(VoxelShapeSpliterator.class)
public class MixinVoxelShapeSpliterator {

    private boolean astralSorceryDidCustomCollision = false;

    @Inject(method = "tryAdvance", at = @At("HEAD"), cancellable = true)
    public void addCustomCollision(Consumer<? super VoxelShape> collisionShapeIterator, CallbackInfoReturnable<Boolean> cir) {
        if (!this.astralSorceryDidCustomCollision) {
            VoxelShapeSpliterator iterator = (VoxelShapeSpliterator)(Object) this;
            if (CollisionHelper.onCollision(iterator, collisionShapeIterator)) {
                cir.setReturnValue(true);
                return;
            }
            this.astralSorceryDidCustomCollision = true;
        }
    }
}
