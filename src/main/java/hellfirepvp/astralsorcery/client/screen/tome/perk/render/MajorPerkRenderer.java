/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.perk.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
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

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MajorPerkRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class MajorPerkRenderer<T extends PerkTreePoint<A>, A extends AbstractPerk<?>> extends PerkRenderer<T, A> {

    public static final MajorPerkRenderer<?, ?> MAJOR = new MajorPerkRenderer<>();

    protected MajorPerkRenderer() {}

    @Override
    public FloatRectangle renderPerk(BatchPerkContext drawCtx, GuiGraphics graphics, T point,
                                     PerkAllocationStatus status, float pTicks, float x, float y, float scale) {
        SpriteSheet haloSprite = this.getHaloPerkSprite(status);
        PerkRenderType perkRenderType = this.getHaloPerkRenderType(status);

        VertexConsumer buf = drawCtx.getBuffer(perkRenderType);
        float haloSize = this.getHaloSize(point, scale);
        if (status.isAllocated()) {
            haloSize *= 1.45F;
        }
        UVFrame uv = haloSprite.getUV(this.getEffectTick(point));

        RenderQuadUtil.rect(buf, graphics.pose(), x - haloSize, y - haloSize, haloSize * 2, haloSize * 2)
                .color(1F, 1F, 1F, 0.85F)
                .tex(uv)
                .draw();

        super.renderPerk(drawCtx, graphics, point, status, pTicks, x, y, scale);

        float frameSize = this.getHaloSize(point, scale);
        return new FloatRectangle(-frameSize, -frameSize, frameSize * 2, frameSize * 2);
    }

    protected float getHaloSize(T point, float renderScale) {
        return point.getRenderSize() * renderScale * 0.8F;
    }

    @Override
    public float getSealRenderSize(T point, float scale) {
        return scale;
    }

    protected SpriteSheet getHaloPerkSprite(PerkAllocationStatus status) {
        return switch (status) {
            case GRANTED, ALLOCATED -> SpritesAS.SPRITE_PERK_HALO_ACTIVE;
            case UNLOCKABLE -> SpritesAS.SPRITE_PERK_HALO_ACTIVATABLE;
            default -> SpritesAS.SPRITE_PERK_HALO_INACTIVE;
        };
    }

    protected PerkRenderType getHaloPerkRenderType(PerkAllocationStatus status) {
        return switch (status) {
            case GRANTED, ALLOCATED -> PerkRenderType.Types.PERK_HALO_ACTIVE;
            case UNLOCKABLE -> PerkRenderType.Types.PERK_HALO_ACTIVATABLE;
            default -> PerkRenderType.Types.PERK_HALO_INACTIVE;
        };
    }
}
