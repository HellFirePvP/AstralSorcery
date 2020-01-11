/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.screen.journal.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.screen.journal.perk.DynamicPerkRender;
import hellfirepvp.astralsorcery.client.screen.journal.perk.PerkRenderGroup;
import hellfirepvp.astralsorcery.client.screen.journal.perk.group.PerkPointHaloRenderGroup;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.AllocationStatus;
import hellfirepvp.astralsorcery.common.perk.node.GemSlotPerk;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeGem
 * Created by HellFirePvP
 * Date: 25.08.2019 / 18:33
 */
public class PerkTreeGem<T extends AbstractPerk & GemSlotPerk> extends PerkTreePoint<T> implements DynamicPerkRender {

    public PerkTreeGem(T perk, Point offset) {
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
    public void renderAt(AllocationStatus status, long spriteOffsetTick, float pTicks, double x, double y, double scale) {
        ItemStack stack = this.getPerk().getContainedItem(Minecraft.getInstance().player, LogicalSide.CLIENT);
        if (!stack.isEmpty()) {
            int posX = (int) Math.round(x - (8 * scale));
            int posY = (int) Math.round(y - (8 * scale));

            GlStateManager.pushMatrix();
            GlStateManager.translated(posX, posY, 0);
            GlStateManager.scaled(scale, scale, scale);
            RenderingUtils.renderItemStack(Minecraft.getInstance().getItemRenderer(), stack, 0, 0, null);
            GlStateManager.popMatrix();
        }
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
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

        double haloSize = getRenderSize() * 0.8 * scale;
        if (status == AllocationStatus.ALLOCATED) {
            haloSize *= 1.5;
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
                    .color(1F, 1F, 1F, 0.85F).endVertex();
        }

        super.renderPerkAtBatch(drawCtx, status, spriteOffsetTick, pTicks, x, y, scale);

        double actualSize = getRenderSize() * scale;
        return new Rectangle.Double(-actualSize, -actualSize, actualSize * 2, actualSize * 2);
    }
}
