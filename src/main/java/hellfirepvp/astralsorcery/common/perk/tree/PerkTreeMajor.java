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
import hellfirepvp.astralsorcery.client.screen.journal.perk.PerkRenderGroup;
import hellfirepvp.astralsorcery.client.screen.journal.perk.group.PerkPointHaloRenderGroup;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.common.perk.AllocationStatus;
import hellfirepvp.astralsorcery.common.perk.node.MajorPerk;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeMajor
 * Created by HellFirePvP
 * Date: 09.08.2019 / 07:21
 */
public class PerkTreeMajor<T extends MajorPerk> extends PerkTreePoint<T> {

    public PerkTreeMajor(T perk, Point.Float offset) {
        super(perk, offset);
        this.setRenderSize((int) (this.getRenderSize() * 1.4));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addGroups(Collection<PerkRenderGroup> groups) {
        super.addGroups(groups);
        groups.add(PerkPointHaloRenderGroup.INSTANCE);
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public Rectangle.Float renderPerkAtBatch(BatchPerkContext drawCtx,
                                              AllocationStatus status, long spriteOffsetTick, float pTicks,
                                              float x, float y, float zLevel, float scale) {
        SpriteSheetResource tex = getHaloSprite(status);
        BatchPerkContext.TextureObjectGroup grp = PerkPointHaloRenderGroup.INSTANCE.getGroup(tex);
        if (grp == null) {
            return new Rectangle.Float();
        }
        BufferBuilder buf = drawCtx.getContext(grp);

        float haloSize = getRenderSize() * 0.8F * scale;
        if (status == AllocationStatus.ALLOCATED) {
            haloSize *= 1.5;
        }

        Tuple<Float, Float> frameUV = tex.getUVOffset(spriteOffsetTick);

        RenderingGuiUtils.rect(buf, x - haloSize, y - haloSize, zLevel, haloSize * 2F, haloSize * 2F)
                .color(1F, 1F, 1F, 0.85F)
                .tex(frameUV.getA(), frameUV.getB(), tex.getULength(), tex.getVLength())
                .draw();

        super.renderPerkAtBatch(drawCtx, status, spriteOffsetTick, pTicks, x, y, zLevel, scale);

        float actualSize = getRenderSize() * scale;
        return new Rectangle.Float(-actualSize, -actualSize, actualSize * 2, actualSize * 2);
    }

}
