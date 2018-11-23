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
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.MajorPerk;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeMajor
 * Created by HellFirePvP
 * Date: 08.07.2018 / 23:40
 */
public class PerkTreeMajor<T extends MajorPerk> extends PerkTreePoint<T> {

    private SpriteQuery queryCstUnAllocated;
    private SpriteQuery queryCstAllocated;
    private SpriteQuery queryCstUnlockable;

    public PerkTreeMajor(T perk, Point offset) {
        super(perk, offset);
        this.setRenderSize((int) (this.getRenderSize() * 1.4));
    }

    public void setQueryMajorPerkHaloUnAllocated(SpriteQuery queryCstUnAllocated) {
        this.queryCstUnAllocated = queryCstUnAllocated;
    }

    public void setQueryMajorPerkHaloUnlockable(SpriteQuery queryCstUnlockable) {
        this.queryCstUnlockable = queryCstUnlockable;
    }

    public void setQueryMajorPerkHaloAllocated(SpriteQuery queryCstAllocated) {
        this.queryCstAllocated = queryCstAllocated;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public Rectangle renderAtCurrentPos(AllocationStatus status, long spriteOffsetTick, float pTicks) {
        if (queryCstUnAllocated == null) {
            queryCstUnAllocated = SpriteQuery.of(SpriteLibrary.spriteHalo4);
        }
        if (queryCstAllocated == null) {
            queryCstAllocated = SpriteQuery.of(SpriteLibrary.spriteHalo5);
        }
        if (queryCstUnlockable == null) {
            queryCstUnlockable = SpriteQuery.of(SpriteLibrary.spriteHalo6);
        }

        int haloRenderSize = (int) (getRenderSize() * 0.8);
        SpriteSheetResource tex;
        switch (status) {
            case UNALLOCATED:
                tex = queryCstUnAllocated.resolveSprite();
                break;
            case ALLOCATED:
                haloRenderSize *= 1.5;
                tex = queryCstAllocated.resolveSprite();
                break;
            case UNLOCKABLE:
                tex = queryCstUnlockable.resolveSprite();
                break;
            default:
                tex = queryCstUnAllocated.resolveSprite();
                break;
        }
        if (tex == null) return null;

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        Vector3 starVec = new Vector3(-haloRenderSize, -haloRenderSize, 0);

        tex.bindTexture();
        double uLength = tex.getULength();
        double vLength = tex.getVLength();
        Tuple<Double, Double> frameUV = tex.getUVOffset(spriteOffsetTick);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone().addX(haloRenderSize * u * 2).addY(haloRenderSize * v * 2);
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(frameUV.key + uLength * u, frameUV.value + vLength * v).endVertex();
        }

        GlStateManager.disableAlpha();
        GlStateManager.color(1, 1, 1, 0.85F);
        tes.draw();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableAlpha();


        return super.renderAtCurrentPos(status, spriteOffsetTick, pTicks);
    }
}
