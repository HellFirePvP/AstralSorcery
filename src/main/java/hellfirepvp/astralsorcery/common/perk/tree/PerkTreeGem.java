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
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.AllocationStatus;
import hellfirepvp.astralsorcery.common.perk.node.GemSlotPerk;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeGem
 * Created by HellFirePvP
 * Date: 25.08.2019 / 18:33
 */
public class PerkTreeGem<T extends AbstractPerk & GemSlotPerk> extends PerkTreePoint<T> implements DynamicPerkRender {

    public PerkTreeGem(T perk, Point.Float offset) {
        super(perk, offset);
        this.setRenderSize((int) (this.getRenderSize() * 1.4));
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
        ItemStack stack = this.getPerk().getContainedItem(Minecraft.getInstance().player, LogicalSide.CLIENT);
        if (!stack.isEmpty()) {
            float posX = x - (8 * scale);
            float posY = y - (8 * scale);

            RenderSystem.pushMatrix();
            RenderSystem.translated(posX, posY, zLevel);
            RenderSystem.scaled(scale, scale, scale);
            RenderingUtils.renderItemStack(Minecraft.getInstance().getItemRenderer(), stack, 0, 0, null);
            RenderSystem.popMatrix();
        }
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
