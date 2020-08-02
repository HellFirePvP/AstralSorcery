/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.screen.journal.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.screen.journal.perk.DynamicPerkRender;
import hellfirepvp.astralsorcery.client.screen.journal.perk.PerkRenderGroup;
import hellfirepvp.astralsorcery.client.screen.journal.perk.group.PerkPointHaloRenderGroup;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.AllocationStatus;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
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

    public PerkTreeConstellation(T perk, Point.Float offset, IConstellation associatedConstellation, int perkSpriteSize) {
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
    @OnlyIn(Dist.CLIENT)
    public void renderAt(AllocationStatus status, long spriteOffsetTick, float pTicks, float x, float y, float zLevel, float scale) {
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

        float size = perkSpriteSize * 0.85F * scale;

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();

        RenderingConstellationUtils.renderConstellationIntoGUI(overlay, this.associatedConstellation,
                x - size, y - size, 0,
                size * 2, size * 2, 1.5 * scale,
                () -> 0.75F, true, false);

        RenderSystem.disableBlend();
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public Rectangle.Float renderPerkAtBatch(BatchPerkContext drawCtx,
                                                AllocationStatus status,
                                                long spriteOffsetTick, float pTicks,
                                                float x, float y, float zLevel, float scale) {
        SpriteSheetResource tex = getHaloSprite(status);
        BatchPerkContext.TextureObjectGroup grp = PerkPointHaloRenderGroup.INSTANCE.getGroup(tex);
        if (grp == null) {
            return new Rectangle.Float();
        }
        BufferContext buf = drawCtx.getContext(grp);

        float haloSize = perkSpriteSize * scale;
        if (status == AllocationStatus.ALLOCATED) {
            haloSize *= 1.3F;
        }

        Tuple<Float, Float> frameUV = tex.getUVOffset(spriteOffsetTick);

        RenderingGuiUtils.rect(buf, x - haloSize, y - haloSize, zLevel, haloSize * 2F, haloSize * 2F)
                .color(1F, 1F, 1F, 0.85F)
                .tex(frameUV.getA(), frameUV.getB(), tex.getULength(), tex.getVLength())
                .draw();

        super.renderPerkAtBatch(drawCtx, status, spriteOffsetTick, pTicks, x, y, zLevel, scale);

        float actualSize = perkSpriteSize * scale;
        return new Rectangle.Float(-actualSize, -actualSize, actualSize * 2, actualSize * 2);
    }
}
