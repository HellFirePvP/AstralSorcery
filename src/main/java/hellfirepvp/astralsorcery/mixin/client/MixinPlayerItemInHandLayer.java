/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinPlayerItemInHandLayer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(PlayerItemInHandLayer.class)
public abstract class MixinPlayerItemInHandLayer {

    @Shadow protected abstract void renderArmWithSpyglass(LivingEntity entity, ItemStack stack, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int combinedLight);

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    public void alsoRenderAstrolabeHandLayer(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (stack.is(ItemsAS.ASTROLABE) && entity.getUseItem() == stack && entity.swingTime == 0) {
            this.renderArmWithSpyglass(entity, stack, arm, poseStack, buffer, packedLight);
            ci.cancel();
        }
    }
}
