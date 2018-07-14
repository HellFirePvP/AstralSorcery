/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree;

import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.SpriteQuery;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeOffset
 * Created by HellFirePvP
 * Date: 17.06.2018 / 09:32
 */
public class PerkTreeOffset extends PerkTreePoint {

    private final IConstellation associatedConstellation;

    private static final int haloSpriteSize = 45;
    private SpriteQuery queryCstUnAllocated = new SpriteQuery(AssetLoader.TextureLocation.EFFECT, "halo4", 4, 8);
    private SpriteQuery queryCstAllocated = new SpriteQuery(AssetLoader.TextureLocation.EFFECT, "halo5", 4, 8);
    private SpriteQuery queryCstUnlockable = new SpriteQuery(AssetLoader.TextureLocation.EFFECT, "halo6", 4, 8);

    public PerkTreeOffset(AbstractPerk perk, Point offset, IConstellation associatedConstellation) {
        super(perk, offset);
        this.associatedConstellation = associatedConstellation;
        this.setRenderSize(haloSpriteSize / 2);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public Rectangle renderAtCurrentPos(AllocationStatus status, long spriteOffsetTick, float pTicks) {
        GlStateManager.color(1, 1, 1, 1);
        super.renderAtCurrentPos(status, spriteOffsetTick, pTicks);

        int haloRenderSize = haloSpriteSize;
        SpriteSheetResource tex;
        switch (status) {
            case UNALLOCATED:
                tex = queryCstUnAllocated.resolveSprite();
                break;
            case ALLOCATED:
                haloRenderSize *= 1.3;
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
        tes.draw();
        GlStateManager.enableAlpha();

        if (this.associatedConstellation != null) {
            Color overlay = Color.WHITE;
            switch (status) {
                case UNALLOCATED:
                    overlay = new Color(0x3333FF);
                    break;
                case ALLOCATED:
                    overlay = new Color(0xEEEE00);
                    break;
                case UNLOCKABLE:
                    overlay = new Color(0xC920DD);
                    break;
            }

            int size = MathHelper.floor(haloSpriteSize * 0.85);

            RenderConstellation.renderConstellationIntoGUI(overlay, this.associatedConstellation,
                    -size, -size, 0,
                    size * 2, size * 2, 1.5,
                    new RenderConstellation.BrightnessFunction() {
                        @Override
                        public float getBrightness() {
                            return 0.75F;
                        }
                    }, true, false);

        }

        return new Rectangle(-haloSpriteSize, -haloSpriteSize, haloSpriteSize * 2, haloSpriteSize * 2);
    }
}
