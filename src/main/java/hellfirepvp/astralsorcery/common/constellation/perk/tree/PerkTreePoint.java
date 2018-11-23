/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree;

import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.SpriteQuery;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreePoint
 * Created by HellFirePvP
 * Date: 17.06.2018 / 09:32
 */
public class PerkTreePoint<T extends AbstractPerk> {

    private Point offset;
    private final T perk;
    private int renderSize;

    private static final int spriteSize = 11;
    private SpriteQuery querySpriteUnallocated;
    private SpriteQuery querySpriteAllocated;
    private SpriteQuery querySpriteUnlockable;
    private SpriteQuery querySpriteSeal;

    public PerkTreePoint(T perk, Point offset) {
        this.offset = offset;
        this.perk = perk;
        this.renderSize = spriteSize;
    }

    public void setRenderSize(int renderSize) {
        this.renderSize = renderSize;
    }

    public int getRenderSize() {
        return renderSize;
    }

    public AbstractPerk getPerk() {
        return perk;
    }

    public Point getOffset() {
        return offset;
    }

    public void setQuerySpriteAllocated(SpriteQuery querySpriteAllocated) {
        this.querySpriteAllocated = querySpriteAllocated;
    }

    public void setQuerySpriteUnallocated(SpriteQuery querySpriteUnallocated) {
        this.querySpriteUnallocated = querySpriteUnallocated;
    }

    public void setQuerySpriteUnlockable(SpriteQuery querySpriteUnlockable) {
        this.querySpriteUnlockable = querySpriteUnlockable;
    }

    public void setQuerySpriteSeal(SpriteQuery querySpriteSeal) {
        this.querySpriteSeal = querySpriteSeal;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public Rectangle renderAtCurrentPos(AllocationStatus status, long spriteOffsetTick, float pTicks) {
        if (querySpriteUnallocated == null) {
            querySpriteUnallocated = SpriteQuery.of(SpriteLibrary.spritePerkInactive);
        }
        if (querySpriteAllocated == null) {
            querySpriteAllocated = SpriteQuery.of(SpriteLibrary.spritePerkActive);
        }
        if (querySpriteUnlockable == null) {
            querySpriteUnlockable = SpriteQuery.of(SpriteLibrary.spritePerkActivateable);
        }

        SpriteSheetResource tex;
        switch (status) {
            case UNALLOCATED:
                tex = querySpriteUnallocated.resolveSprite();
                break;
            case ALLOCATED:
                tex = querySpriteAllocated.resolveSprite();
                break;
            case UNLOCKABLE:
                tex = querySpriteUnlockable.resolveSprite();
                break;
            default:
                tex = querySpriteUnallocated.resolveSprite();
                break;
        }
        if (tex == null) return null;

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        GlStateManager.disableAlpha();

        Vector3 starVec = new Vector3(-renderSize, -renderSize, 0);

        tex.bindTexture();
        double uLength = tex.getULength();
        double vLength = tex.getVLength();
        Tuple<Double, Double> frameUV = tex.getUVOffset(spriteOffsetTick);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone().addX(renderSize * u * 2).addY(renderSize * v * 2);
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(frameUV.key + uLength * u, frameUV.value + vLength * v).endVertex();
        }
        tes.draw();

        GlStateManager.enableAlpha();
        return new Rectangle(-renderSize, -renderSize, renderSize * 2, renderSize * 2);
    }

    @SideOnly(Side.CLIENT)
    public void renderSealAtCurrentPos(double radius, long spriteOffsetTick, float pTicks) {
        if (querySpriteSeal == null) {
            querySpriteSeal = SpriteQuery.of(SpriteLibrary.spritePerkSeal);
        }
        SpriteSheetResource tex = querySpriteSeal.resolveSprite();
        if (tex == null) {
            return;
        }

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        GlStateManager.disableAlpha();

        tex.bindTexture();
        double uLength = tex.getULength();
        double vLength = tex.getVLength();
        Tuple<Double, Double> frameUV = tex.getUVOffset(spriteOffsetTick);
        Vector3 starVec = new Vector3(-radius, -radius, 0);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone().addX(radius * u * 2).addY(radius * v * 2);
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(frameUV.key + uLength * u, frameUV.value + vLength * v).endVertex();
        }
        tes.draw();

        GlStateManager.enableAlpha();
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
