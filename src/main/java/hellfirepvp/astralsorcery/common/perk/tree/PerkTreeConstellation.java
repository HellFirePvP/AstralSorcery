/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree;

import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.screen.journal.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.screen.journal.perk.DynamicPerkRender;
import hellfirepvp.astralsorcery.client.screen.journal.perk.PerkRenderGroup;
import hellfirepvp.astralsorcery.client.screen.journal.perk.group.PerkPointHaloRenderGroup;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.AllocationStatus;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeConstellation
 * Created by HellFirePvP
 * Date: 25.08.2019 / 18:36
 */
public class PerkTreeConstellation<T extends AbstractPerk> extends PerkTreePoint<T> implements DynamicPerkRender {

    public static final int ROOT_SPRITE_SIZE = 50;
    public static final int MINOR_SPRITE_SIZE = 40;

    private final IConstellation associatedConstellation;

    private final int perkSpriteSize;

    public PerkTreeConstellation(T perk, Point offset, IConstellation associatedConstellation, int perkSpriteSize) {
        super(perk, offset);
        this.associatedConstellation = associatedConstellation;
        this.perkSpriteSize = perkSpriteSize;
        this.setRenderSize(perkSpriteSize / 2);
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addGroups(Collection<PerkRenderGroup> groups) {
        super.addGroups(groups);
        groups.add(PerkPointHaloRenderGroup.INSTANCE);
    }

    @Override
    public void renderAt(AllocationStatus status, long spriteOffsetTick, float pTicks, double x, double y, double scale) {
        if (this.associatedConstellation == null) {
            return;
        }

        PlayerProgress prog = ResearchHelper.getClientProgress();
        if (!prog.hasConstellationDiscovered(this.associatedConstellation)) {
            return;
        }

        Color overlay = Color.WHITE;
        switch (status) {
            case UNALLOCATED:
                overlay = ColorsAS.PERK_UNALLOCATED;
                break;
            case ALLOCATED:
                overlay = ColorsAS.PERK_ALLOCATED;
                break;
            case UNLOCKABLE:
                overlay = ColorsAS.PERK_UNLOCKABLE;
                break;
            default:
                break;
        }

        int size = MathHelper.floor(perkSpriteSize * 0.85 * scale);
        int fX = (int) Math.round(x);
        int fY = (int) Math.round(y);

        RenderingConstellationUtils.renderConstellationIntoGUI(overlay, this.associatedConstellation,
                fX - size, fY - size, 0,
                size * 2, size * 2, 1.5 * scale,
                () -> 0.75F, true, false);
    }

    @Nullable
    @Override
    public Rectangle2D.Double renderPerkAtBatch(BatchPerkContext drawCtx,
                                                AllocationStatus status,
                                                long spriteOffsetTick, float pTicks,
                                                double x, double y, double scale) {
        SpriteSheetResource tex = getHaloSprite(status);
        BatchPerkContext.TextureObjectGroup grp = PerkPointHaloRenderGroup.INSTANCE.getGroup(tex);
        if (grp == null) {
            return new Rectangle.Double();
        }
        BufferContext vb = drawCtx.getContext(grp);

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
                    .tex(frameUV.getA() + uLength * u, frameUV.getB() + vLength * v)
                    .color(1F, 1F, 1F, 1F).endVertex();
        }

        super.renderPerkAtBatch(drawCtx, status, spriteOffsetTick, pTicks, x, y, scale);

        double actualSize = perkSpriteSize * scale;
        return new Rectangle.Double(-actualSize, -actualSize, actualSize * 2, actualSize * 2);
    }
}
