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
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.GemSlotPerk;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeGem
 * Created by HellFirePvP
 * Date: 17.11.2018 / 18:47
 */
public class PerkTreeGem<T extends AbstractPerk & GemSlotPerk> extends PerkTreePoint<T> implements DynamicPerkRender {

    public PerkTreeGem(T perk, Point offset) {
        super(perk, offset);
        this.setRenderSize((int) (this.getRenderSize() * 1.4));
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
        ItemStack stack = this.getPerk().getContainedItem(Minecraft.getMinecraft().player, Side.CLIENT);
        if (!stack.isEmpty()) {
            int posX = (int) Math.round(x - (8 * scale));
            int posY = (int) Math.round(y - (8 * scale));
            FontRenderer fr = stack.getItem().getFontRenderer(stack);
            if (fr == null) fr = Minecraft.getMinecraft().fontRenderer;
            GlStateManager.pushMatrix();
            GlStateManager.translate(posX, posY, 0);
            GlStateManager.scale(scale, scale, scale);
            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(fr, stack, 0, 0, null);
            GlStateManager.popMatrix();
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
                    .tex(frameUV.key + uLength * u, frameUV.value + vLength * v)
                    .color(1F, 1F, 1F, 0.85F).endVertex();
        }

        super.renderPerkAtBatch(drawCtx, status, spriteOffsetTick, pTicks, x, y, scale);

        double actualSize = getRenderSize() * scale;
        return new Rectangle.Double(-actualSize, -actualSize, actualSize * 2, actualSize * 2);
    }

}
