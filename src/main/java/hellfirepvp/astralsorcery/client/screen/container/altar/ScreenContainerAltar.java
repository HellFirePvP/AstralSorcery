/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.container.altar;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.lib.ShadersAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.base.ScreenContainerMenu;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.component.AttunedConstellationComponent;
import hellfirepvp.astralsorcery.common.container.ContainerAltar;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ColorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScreenContainerAltar
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ScreenContainerAltar<T extends ContainerAltar> extends ScreenContainerMenu<T> {

    public ScreenContainerAltar(T menu, Inventory playerInventory, Component title, int screenWidth, int screenHeight) {
        super(menu, playerInventory, title, screenWidth, screenHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    protected void renderFocusStarfield(GuiGraphics guiGraphics, int x, int y, float partialTick) {
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        TexturesAS.STAR_1.bindTexture();
        RandomSource rand = RandomSource.create(this.getMenu().getTile().getBlockPos().asLong() ^ 0x889581197FF29A92L);

        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, ShadersAS::getPositionColorTexAlphaShader, buf -> {
            for (int i = 0; i < 14; i++) {
                int xx = x + rand.nextInt(54);
                int yy = y + rand.nextInt(54);

                float flickerSpeed = 0.04F + rand.nextFloat() * 0.04F;
                float brightness = 0.3F + EffectUtil.flicker(flickerSpeed, partialTick) * 0.4F;

                RenderQuadUtil.rect(buf, guiGraphics.pose(), this.getGuiLeft() + xx, this.getGuiTop() + yy, 5, 5)
                        .color(brightness, brightness, brightness, brightness)
                        .draw();
            }
        });

        TileAltar altar = this.getMenu().getTile();
        if (altar.getTileData().hasStructure()) {
            altar.getTileData()
                    .getFocusedConstellation()
                    .ifPresent(cst -> {
                        if (!ResearchManager.getClientProgress().hasDiscoveredConstellation(cst)) return;

                        RenderConstellationUtil.drawConstellationUI(cst.getConstellationColor(), cst, guiGraphics.pose(),
                                this.getGuiLeft() + x + 2, this.getGuiTop() + y + 2, 58, 58, 2F,
                                () -> 0.4F + 0.6F * EffectUtil.flicker(0.03F + rand.nextFloat() * 0.03F, partialTick),
                                true, false);
                    });
        }

        RenderSystem.disableBlend();
    }

    protected void renderAltarOutput(GuiGraphics guiGraphics, int x, int y, float partialTick) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        long tick = ClientProxy.getClientTick();
        AltarCraftingInput input = this.getMenu().getTile().createInput(level, null);
        this.getMenu().getTile().findMatchingRecipe(level)
                .map(RecipeHolder::value)
                .map(recipe -> recipe.getOutputs(input, level.registryAccess()))
                .filter(outputs -> !outputs.isEmpty())
                .map(outputs -> outputs.get((int) ((tick / 40) % outputs.size())))
                .ifPresent(output -> {
                    this.drawOutput(guiGraphics, output, x, y, partialTick);
                });
    }

    private void drawOutput(GuiGraphics guiGraphics, ItemStack output, int x, int y, float partialTick) {
        long clientTick = ClientProxy.getClientTick();
        ColorWrapper c1 = ColorWrapper.opaque(0x249CFF);
        ColorWrapper c2 = ColorWrapper.opaque(0xADD5FF);

        float colorPart = (clientTick + partialTick) / 60F;
        float colorPhase = (float) Math.sin(colorPart * Math.PI * 2F) * 0.5F + 0.5F;
        ColorWrapper result = ColorUtil.blendColors(c1, c2, colorPhase);
        result = result.copyWithAlpha(0x99);

        RenderScreenUtil.renderTranslucentItem(guiGraphics, output, this.getGuiLeft() + x, this.getGuiTop() + y,
                partialTick, result, 2F);
    }
}
