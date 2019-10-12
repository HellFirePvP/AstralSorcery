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
import hellfirepvp.astralsorcery.client.screen.base.ScreenCustomContainer;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.container.ContainerAltarDiscovery;
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
 * Class: ScreenContainerAltarDiscovery
 * Created by HellFirePvP
 * Date: 15.08.2019 / 17:06
 */
public class ScreenContainerAltarDiscovery extends ScreenContainerAltar<ContainerAltarDiscovery> {

    public ScreenContainerAltarDiscovery(ContainerAltarDiscovery screenContainer, PlayerInventory inv, ITextComponent name) {
        super(screenContainer, inv, name, 176, 166);
    }

    @Override
    public AbstractRenderableTexture getBackgroundTexture() {
        return TexturesAS.TEX_CONTAINER_ALTAR_DISCOVERY;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GlStateManager.enableBlend();

        SimpleAltarRecipe recipe = this.findRecipe(false);
        if (recipe != null) {
            ItemStack out = recipe.getOutputForRender(this.getContainer().getTileEntity().getInventory());
            this.blitOffset = 10;
            GlStateManager.pushMatrix();
            GlStateManager.translated(130, 20, 0);
            GlStateManager.scaled(1.7, 1.7, 1.7);

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
        drawRect(guiLeft + 6, guiTop + 69, 165, 10);

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
            drawRect(guiLeft + 6, guiTop + 69, (int) (165 * percFilled), 10,
                    uvOffset.getA(), uvOffset.getB(),
                    spriteStarlight.getULength() * percFilled, spriteStarlight.getVLength());

            SimpleAltarRecipe aar = findRecipe(true);
            if (aar != null) {
                int req = aar.getStarlightRequirement();
                int has = altar.getStoredStarlight();
                if (has < req) {
                    int max = altar.getAltarType().getStarlightCapacity();
                    float percReq = (float) (req - has) / (float) max;
                    int from = (int) (165 * percFilled);
                    int to = (int) (165 * percReq);
                    GlStateManager.color4f(0.2F, 0.5F, 1.0F, 0.4F);

                    drawRect(guiLeft + 6 + from, guiTop + 69, to, 10,
                            uvOffset.getA() + spriteStarlight.getULength() * percFilled, uvOffset.getB(),
                            spriteStarlight.getULength() * percReq, spriteStarlight.getVLength());
                }
            }
        }

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
