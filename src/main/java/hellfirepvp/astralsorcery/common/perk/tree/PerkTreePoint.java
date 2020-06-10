/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree;

import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.screen.journal.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.screen.journal.perk.PerkRender;
import hellfirepvp.astralsorcery.client.screen.journal.perk.PerkRenderGroup;
import hellfirepvp.astralsorcery.client.screen.journal.perk.group.PerkPointRenderGroup;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.AllocationStatus;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;
import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreePoint
 * Created by HellFirePvP
 * Date: 02.06.2019 / 02:03
 */
public class PerkTreePoint<T extends AbstractPerk> implements PerkRender {

    private Point.Float offset;
    private final T perk;
    private int renderSize;

    private static final int spriteSize = 11;

    public PerkTreePoint(T perk, Point.Float offset) {
        this.offset = offset;
        this.perk = perk;
        this.renderSize = spriteSize;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addGroups(Collection<PerkRenderGroup> groups) {
        groups.add(PerkPointRenderGroup.INSTANCE);
    }

    public void setRenderSize(int renderSize) {
        this.renderSize = renderSize;
    }

    public int getRenderSize() {
        return renderSize;
    }

    public T getPerk() {
        return perk;
    }

    public Point.Float getOffset() {
        return offset;
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public Rectangle.Float renderPerkAtBatch(BatchPerkContext drawCtx,
                                              AllocationStatus status, long spriteOffsetTick, float pTicks,
                                              float x, float y, float zLevel, float scale) {
        SpriteSheetResource tex = getFlareSprite(status);
        BatchPerkContext.TextureObjectGroup grp = PerkPointRenderGroup.INSTANCE.getGroup(tex);
        if (grp == null) {
            return new Rectangle.Float();
        }
        BufferBuilder buf = drawCtx.getContext(grp);

        float size = renderSize * scale;
        Tuple<Float, Float> frameUV = tex.getUVOffset(spriteOffsetTick);

        RenderingGuiUtils.rect(buf, x - size, y - size, zLevel, size * 2F, size * 2F)
                .tex(frameUV.getA(), frameUV.getB(), tex.getULength(), tex.getVLength())
                .draw();
        return new Rectangle.Float(-size, -size, size * 2, size * 2);
    }

    @OnlyIn(Dist.CLIENT)
    protected SpriteSheetResource getFlareSprite(AllocationStatus status) {
        SpriteSheetResource tex;
        switch (status) {
            case ALLOCATED:
                tex = SpritesAS.SPR_PERK_ACTIVE;
                break;
            case UNLOCKABLE:
                tex = SpritesAS.SPR_PERK_ACTIVATEABLE;
                break;
            case UNALLOCATED:
            default:
                tex = SpritesAS.SPR_PERK_INACTIVE;
                break;
        }
        return tex;
    }

    @OnlyIn(Dist.CLIENT)
    protected SpriteSheetResource getHaloSprite(AllocationStatus status) {
        SpriteSheetResource tex;
        switch (status) {
            case ALLOCATED:
                tex = SpritesAS.SPR_PERK_HALO_ACTIVE;
                break;
            case UNLOCKABLE:
                tex = SpritesAS.SPR_PERK_HALO_ACTIVATEABLE;
                break;
            case UNALLOCATED:
            default:
                tex = SpritesAS.SPR_PERK_HALO_INACTIVE;
                break;
        }
        return tex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkTreePoint that = (PerkTreePoint) o;
        return Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset);
    }

}
