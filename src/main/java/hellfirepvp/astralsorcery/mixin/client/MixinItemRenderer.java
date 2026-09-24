/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.lib.CustomModelsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.FlagExecutor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinItemRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Unique
    private static final ModelResourceLocation astralSorcery_1_21$ASTROLABE_MODEL = ModelResourceLocation.inventory(AstralSorcery.key("astrolabe"));

    @Shadow public abstract ItemModelShaper getItemModelShaper();

    @Shadow public abstract void render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model);

    @Inject(method = "getModel", at = @At(value = "HEAD"), cancellable = true)
    public void getCustomSextantModel(ItemStack stack, Level level, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (stack.is(ItemsAS.ASTROLABE)) {
            BakedModel model = this.getItemModelShaper().getModelManager().getModel(CustomModelsAS.ASTROLABE_IN_HAND);

            ClientLevel clientlevel = level instanceof ClientLevel ? (ClientLevel) level : null;
            model = model.getOverrides().resolve(model, stack, clientlevel, entity, seed);
            cir.setReturnValue(model == null ? this.getItemModelShaper().getModelManager().getMissingModel() : model);
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void replaceRenderModel(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        boolean override = displayContext == ItemDisplayContext.GUI ||
                displayContext == ItemDisplayContext.GROUND ||
                displayContext == ItemDisplayContext.FIXED;
        if (override && itemStack.is(ItemsAS.ASTROLABE)) {
            FlagExecutor.run(FlagExecutor.Flag.RENDER_ASTROLABE_ITEM, () -> {
                BakedModel sextantModel = this.getItemModelShaper().getModelManager().getModel(astralSorcery_1_21$ASTROLABE_MODEL);
                this.render(itemStack, displayContext, leftHand, poseStack, bufferSource, combinedLight, combinedOverlay, sextantModel);
                ci.cancel();
            });
        }
    }
}
