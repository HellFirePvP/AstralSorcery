/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.perk.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheet;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.screen.tome.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocationStatus;
import hellfirepvp.astralsorcery.common.util.data.FloatRectangle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkRenderer<T extends PerkTreePoint<A>, A extends AbstractPerk<?>> {

    public static final PerkRenderer<?, ?> DEFAULT = new PerkRenderer<>();

    protected PerkRenderer() {}

    public FloatRectangle renderPerk(BatchPerkContext drawCtx, GuiGraphics graphics, T point,
                                     PerkAllocationStatus status, float pTicks, float x, float y, float scale) {
        SpriteSheet perkSprite = this.getPerkSprite(status);
        PerkRenderType perkRenderType = this.getPerkRenderType(status);

        VertexConsumer buf = drawCtx.getBuffer(perkRenderType);
        float size = this.getStarSize(point, scale);
        UVFrame uv = perkSprite.getUV(this.getEffectTick(point));

        RenderQuadUtil.rect(buf, graphics.pose(), x - size, y - size, size * 2, size * 2)
                .tex(uv)
                .draw();

        return new FloatRectangle(-size, -size, size * 2, size * 2);
    }

    protected float getStarSize(T point, float renderScale) {
        return point.getRenderSize() * renderScale;
    }

    public long getEffectTick(T point) {
        return ClientProxy.getClientTick() + Mth.floor(point.getOffset().x()) + Mth.floor(point.getOffset().y());
    }

    protected SpriteSheet getPerkSprite(PerkAllocationStatus status) {
        return switch (status) {
            case GRANTED, ALLOCATED -> SpritesAS.SPRITE_PERK_ACTIVE;
            case UNLOCKABLE -> SpritesAS.SPRITE_PERK_ACTIVATABLE;
            default -> SpritesAS.SPRITE_PERK_INACTIVE;
        };
    }

    protected PerkRenderType getPerkRenderType(PerkAllocationStatus status) {
        return switch (status) {
            case GRANTED, ALLOCATED -> PerkRenderType.Types.PERK_ACTIVE;
            case UNLOCKABLE -> PerkRenderType.Types.PERK_ACTIVATABLE;
            default -> PerkRenderType.Types.PERK_INACTIVE;
        };
    }

    public float getSealRenderSize(T point, float scale) {
        return scale * 0.75F;
    }

    public boolean needsImmediateRender(PerkTreePoint<?> point) {
        return false;
    }

    public void renderImmediate(GuiGraphics graphics, T point, PerkAllocationStatus status,
                                float pTicks, float x, float y, float scale) {
    }
}
