/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.container.ContainerAltarBase;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipeContext;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenContainerAltar
 * Created by HellFirePvP
 * Date: 15.08.2019 / 17:08
 */
public abstract class ScreenContainerAltar<T extends ContainerAltarBase> extends ScreenCustomContainer<T> {

    public ScreenContainerAltar(T screenContainer, PlayerInventory inv, ITextComponent name, int width, int height) {
        super(screenContainer, inv, name, width, height);
    }

    @Nullable
    public SimpleAltarRecipe findRecipe(boolean ignoreStarlightRequirement) {
        TileAltar ta = getContainer().getTileEntity();
        return RecipeTypesAS.TYPE_ALTAR.findRecipe(new SimpleAltarRecipeContext(Minecraft.getInstance().player, LogicalSide.CLIENT, ta)
                .setIgnoreStarlightRequirement(ignoreStarlightRequirement));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.renderGuiBackground(partialTicks, mouseX, mouseY);
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    protected void renderStarlightBar(int offsetX, int offsetZ, int width, int height) {
        TileAltar altar = this.getContainer().getTileEntity();

        RenderSystem.disableAlphaTest();

        TexturesAS.TEX_BLACK.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, guiLeft + offsetX, guiTop + offsetZ, this.getBlitOffset(), width, height).draw();
        });

        float percFilled;
        Color barColor;
        if (altar.hasMultiblock()) {
            percFilled = altar.getAmbientStarlightPercent();
            barColor = Color.WHITE;
        } else {
            percFilled = 1.0F;
            barColor = Color.RED;
        }

        if (percFilled > 0) {
            SpriteSheetResource spriteStarlight = SpritesAS.SPR_STARLIGHT_STORE;
            spriteStarlight.getResource().bindTexture();

            int tick = altar.getTicksExisted();
            Tuple<Float, Float> uvOffset = spriteStarlight.getUVOffset(tick);
            RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                RenderingGuiUtils.rect(buf, guiLeft + offsetX, guiTop + offsetZ, this.getBlitOffset(), (int) (width * percFilled), height)
                        .tex(uvOffset.getA(), uvOffset.getB(), spriteStarlight.getULength() * percFilled, spriteStarlight.getVLength())
                        .color(barColor)
                        .draw();
            });

            if (altar.hasMultiblock()) {
                SimpleAltarRecipe aar = findRecipe(true);
                if (aar != null) {
                    int req = aar.getStarlightRequirement();
                    int has = altar.getStoredStarlight();
                    if (has < req) {
                        int max = altar.getAltarType().getStarlightCapacity();
                        float percReq = (float) (req - has) / (float) max;
                        int from = (int) (width * percFilled);
                        int to = (int) (width * percReq);

                        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                            RenderingGuiUtils.rect(buf, guiLeft + offsetX + from, guiTop + offsetZ, this.getBlitOffset(), to, height)
                                    .tex(uvOffset.getA() + spriteStarlight.getULength() * percFilled, uvOffset.getB(), spriteStarlight.getULength() * percReq, spriteStarlight.getVLength())
                                    .color(0.2F, 0.5F, 1.0F, 0.4F)
                                    .draw();
                        });
                    }
                }
            }
        }
        RenderSystem.enableAlphaTest();
    }

    public abstract void renderGuiBackground(float partialTicks, int mouseX, int mouseY);
}
