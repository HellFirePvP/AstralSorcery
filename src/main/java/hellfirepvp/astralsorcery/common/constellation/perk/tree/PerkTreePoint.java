/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree;

import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
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

    private Point offset;
    private final T perk;
    private int renderSize;

    private static final int spriteSize = 11;

    public PerkTreePoint(T perk, Point offset) {
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

    public Point getOffset() {
        return offset;
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public Rectangle.Double renderPerkAtBatch(BatchPerkContext drawCtx,
                                              AllocationStatus status, long spriteOffsetTick, float pTicks,
                                              double x, double y, double scale) {
        SpriteSheetResource tex = getFlareSprite(status);
        BatchPerkContext.TextureObjectGroup grp = PerkPointRenderGroup.INSTANCE.getGroup(tex);
        if (grp == null) {
            return new Rectangle.Double();
        }
        BufferBatch buf = drawCtx.getContext(grp);
        BufferBuilder vb = buf.getBuffer();

        double size = renderSize * scale;
        Vector3 starVec = new Vector3(x - size, y - size, 0);

        double uLength = tex.getULength();
        double vLength = tex.getVLength();
        Tuple<Double, Double> frameUV = tex.getUVOffset(spriteOffsetTick);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone().addX(size * u * 2).addY(size * v * 2);
            vb.pos(pos.getX(), pos.getY(), pos.getZ())
                    .tex(frameUV.getA() + uLength * u, frameUV.getB() + vLength * v)
                    .color(1F, 1F, 1F, 1F)
                    .endVertex();
        }

        return new Rectangle.Double(-size, -size, size * 2, size * 2);
    }

    protected SpriteSheetResource getFlareSprite(AllocationStatus status) {
        SpriteSheetResource tex;
        switch (status) {
            case ALLOCATED:
                tex = SpriteLibrary.spritePerkActive;
                break;
            case UNLOCKABLE:
                tex = SpriteLibrary.spritePerkActivateable;
                break;
            case UNALLOCATED:
            default:
                tex = SpriteLibrary.spritePerkInactive;
                break;
        }
        return tex;
    }

    protected SpriteSheetResource getHaloSprite(AllocationStatus status) {
        SpriteSheetResource tex;
        switch (status) {
            case ALLOCATED:
                tex = SpriteLibrary.spriteHalo5;
                break;
            case UNLOCKABLE:
                tex = SpriteLibrary.spriteHalo6;
                break;
            case UNALLOCATED:
            default:
                tex = SpriteLibrary.spriteHalo4;
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

    public static enum AllocationStatus {

        UNALLOCATED,
        ALLOCATED,
        UNLOCKABLE

    }

}
