/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree;

import hellfirepvp.astralsorcery.client.gui.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.gui.perk.DynamicPerkRender;
import hellfirepvp.astralsorcery.client.gui.perk.PerkRenderGroup;
import hellfirepvp.astralsorcery.client.gui.perk.group.PerkPointHaloRenderGroup;
import hellfirepvp.astralsorcery.client.util.BufferBatch;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreePointConstellation
 * Created by HellFirePvP
 * Date: 17.06.2018 / 09:32
 */
public class PerkTreePointConstellation<T extends AbstractPerk> extends PerkTreePoint<T> implements DynamicPerkRender {

    public static final int ROOT_SPRITE_SIZE = 50;
    public static final int MINOR_SPRITE_SIZE = 40;

    private final IConstellation associatedConstellation;

    private final int perkSpriteSize;

    public PerkTreePointConstellation(T perk, Point offset, IConstellation associatedConstellation, int perkSpriteSize) {
        super(perk, offset);
        this.associatedConstellation = associatedConstellation;
        this.perkSpriteSize = perkSpriteSize;
        this.setRenderSize(perkSpriteSize / 2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addGroups(Collection<PerkRenderGroup> groups) {
        super.addGroups(groups);
        groups.add(PerkPointHaloRenderGroup.INSTANCE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderAt(AllocationStatus status, long spriteOffsetTick, float pTicks, double x, double y, double scale) {
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
                default:
                    break;
            }

            int size = MathHelper.floor(perkSpriteSize * 0.85 * scale);
            int fX = (int) Math.round(x);
            int fY = (int) Math.round(y);

            RenderConstellation.renderConstellationIntoGUI(overlay, this.associatedConstellation,
                    fX - size, fY - size, 0,
                    size * 2, size * 2, 1.5 * scale,
                    new RenderConstellation.BrightnessFunction() {
                        @Override
                        public float getBrightness() {
                            return 0.75F;
                        }
                    }, true, false);

        }
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public Rectangle.Double renderPerkAtBatch(BatchPerkContext drawCtx,
                                       AllocationStatus status, long spriteOffsetTick, float pTicks,
                                       double x, double y, double scale) {
        SpriteSheetResource tex = getHaloSprite(status);
        BatchPerkContext.TextureObjectGroup grp = PerkPointHaloRenderGroup.INSTANCE.getGroup(tex);
        if (grp == null) {
            return new Rectangle.Double();
        }
        BufferBatch buf = drawCtx.getContext(grp);
        BufferBuilder vb = buf.getBuffer();

        double haloSize = perkSpriteSize * scale;
        if (status == AllocationStatus.ALLOCATED) {
            haloSize *= 1.3;
        }

        Vector3 starVec = new Vector3(x - haloSize, y - haloSize, 0);

        double uLength = tex.getULength();
        double vLength = tex.getVLength();
        Tuple<Double, Double> frameUV = tex.getUVOffset(spriteOffsetTick);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone().addX(haloSize * u * 2).addY(haloSize * v * 2);
            vb.pos(pos.getX(), pos.getY(), pos.getZ())
                    .tex(frameUV.key + uLength * u, frameUV.value + vLength * v)
                    .color(1F, 1F, 1F, 1F).endVertex();
        }

        super.renderPerkAtBatch(drawCtx, status, spriteOffsetTick, pTicks, x, y, scale);

        double actualSize = perkSpriteSize * scale;
        return new Rectangle.Double(-actualSize, -actualSize, actualSize * 2, actualSize * 2);
    }

}
