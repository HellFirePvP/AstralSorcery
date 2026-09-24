/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.resource.SpriteSheet;
import hellfirepvp.astralsorcery.common.item.AstrolabeItem;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.mixin.client.AccessorGameRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VFXFocalLightBeam
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VFXFocalLightBeam extends VFXLightBeam {

    private boolean displayWithoutAstrolabe = false;

    public VFXFocalLightBeam(Vector3 pos, SpriteSheet lightBeamSprite) {
        super(pos, lightBeamSprite);
    }

    public VFXLightBeam setup(Vector3 to, double fromSize, double toSize, boolean displayWithoutAstrolabe) {
        this.displayWithoutAstrolabe = displayWithoutAstrolabe;
        return super.setup(to, fromSize, toSize);
    }

    @Override
    public <T extends EntityVisualFX> T alpha(FXAlphaFunction<?> alphaFunction) {
        return super.alpha(alphaFunction.andThen(this.astrolabeFovDistanceAlpha()));
    }

    private <T extends EntityVisualFX> FXAlphaFunction<T> astrolabeFovDistanceAlpha() {
        return (fx, alphaIn, pTicks) -> {
            if (this.displayWithoutAstrolabe) return alphaIn;
            AccessorGameRenderer gameRendererAccess = (AccessorGameRenderer) Minecraft.getInstance().gameRenderer;
            float currentFov = Mth.lerp(pTicks, gameRendererAccess.getOldFov(), gameRendererAccess.getFov());
            return alphaIn * Mth.clamp(1 - Math.abs(AstrolabeItem.FOV_MODIFIER - currentFov) * 5, 0, 1);
        };
    }

    @Override
    public void render(EffectTemplate<?> ctx, Camera renderInfo, VertexConsumer vb, float pTicks) {
        Player player = Minecraft.getInstance().player;
        if ((Minecraft.getInstance().options.getCameraType().isFirstPerson() && AstrolabeItem.isUsingAstrolabe(player)) || this.displayWithoutAstrolabe) {
            super.render(ctx, renderInfo, vb, pTicks);
        }
    }
}
