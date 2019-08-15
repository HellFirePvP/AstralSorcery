/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.container;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.screen.base.ScreenContainerAltar;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.container.ContainerAltarAttunement;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenContainerAltarAttunement
 * Created by HellFirePvP
 * Date: 15.08.2019 / 17:26
 */
public class ScreenContainerAltarAttunement extends ScreenContainerAltar<ContainerAltarAttunement> {

    public ScreenContainerAltarAttunement(ContainerAltarAttunement screenContainer, PlayerInventory inv, ITextComponent name) {
        super(screenContainer, inv, name, 256, 202);
    }

    @Override
    public AbstractRenderableTexture getBackgroundTexture() {
        return TexturesAS.TEX_CONTAINER_ALTAR_ATTUNEMENT;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GlStateManager.enableBlend();

        SimpleAltarRecipe recipe = this.findRecipe();
        if (recipe != null) {
            ItemStack out = recipe.getOutputForRender();
            this.blitOffset = 10;
            GlStateManager.pushMatrix();
            GlStateManager.translated(190, 35, 0);
            GlStateManager.scaled(2.5, 2.5, 2.5);

            RenderingUtils.renderItemStack(Minecraft.getInstance().getItemRenderer(), out, 0, 0, null);

            GlStateManager.popMatrix();
            this.blitOffset = 0;
        }
    }

    @Override
    public void renderGuiBackground(float partialTicks, int mouseX, int mouseY) {
        TileAltar altar = this.getContainer().getTileEntity();
        GlStateManager.pushMatrix();
        GlStateManager.disableAlphaTest();

        TexturesAS.TEX_BLACK.bindTexture();
        drawRect(guiLeft + 11, guiTop + 104, 232, 10);

        float percFilled;
        if (altar.hasMultiblock()) {
            percFilled = altar.getAmbientStarlightPercent();
        } else {
            GlStateManager.color4f(1.0F, 0F, 0F, 1.0F);
            percFilled = 1.0F;
        }

        if (percFilled > 0) {
            SpriteSheetResource spriteStarlight = SpritesAS.SPR_STARLIGHT_STORE;
            spriteStarlight.getResource().bindTexture();

            int tick = altar.getTicksExisted();
            Tuple<Double, Double> uvOffset = spriteStarlight.getUVOffset(tick);
            drawRect(guiLeft + 11, guiTop + 104, (int) (232 * percFilled), 10,
                    uvOffset.getA(), uvOffset.getB(),
                    spriteStarlight.getULength() * percFilled, spriteStarlight.getVLength());

            SimpleAltarRecipe aar = findRecipe();
            if (aar != null) {
                int req = aar.getStarlightRequirement();
                int has = altar.getStoredStarlight();
                if (has < req) {
                    int max = altar.getAltarType().getStarlightCapacity();
                    float percReq = (float) (req - has) / (float) max;
                    int from = (int) (232 * percFilled);
                    int to = (int) (232 * percReq);
                    GlStateManager.color4f(0.2F, 0.5F, 1.0F, 0.4F);

                    drawRect(guiLeft + 11 + from, guiTop + 103, to, 10,
                            uvOffset.getA() + spriteStarlight.getULength() * percFilled, uvOffset.getB(),
                            spriteStarlight.getULength() * percReq, spriteStarlight.getVLength());
                }
            }
        }

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
