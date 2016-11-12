package hellfirepvp.astralsorcery.client.gui.journal.page;

import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttenuationRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageAttenuationRecipe
 * Created by HellFirePvP
 * Date: 27.10.2016 / 01:10
 */
public class JournalPageAttenuationRecipe implements IJournalPage {

    private final AttenuationRecipe recipe;

    public JournalPageAttenuationRecipe(AttenuationRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render(recipe);
    }

    public static class Render implements IGuiRenderablePage {

        private static final BindableResource texGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridAtt");

        private final AttenuationRecipe recipe;

        public Render(AttenuationRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.apply();
            texGrid.bind();
            drawRect(offsetX + 25, offsetY, 129, 202, zLevel);

            GL11.glColor4f(1F, 1F, 1F, 1F);
            TextureHelper.refreshTextureBindState();

            RenderHelper.enableGUIStandardItemLighting();
            ItemStack out = recipe.getOutputForRender();
            GL11.glPushMatrix();
            GL11.glTranslated(offsetX + 78, offsetY + 25, zLevel + 60);
            GL11.glScaled(1.4, 1.4, 1.4);
            drawItemStack(out, 0, 0, 0);
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();

            double offX = offsetX + 55;
            double offY = offsetY + 103;
            IAccessibleRecipe rNative = recipe.getNativeRecipe();
            for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {
                ItemStack expected = rNative.getExpectedStack(srs);
                if(expected == null) expected = rNative.getExpectedStack(srs.rowMultipler, srs.columnMultiplier);
                if(expected == null) continue;
                TextureHelper.refreshTextureBindState();
                GL11.glPushMatrix();
                GL11.glTranslated(offX + (srs.columnMultiplier * 25), offY + (srs.rowMultipler * 25), zLevel + 60);
                GL11.glScaled(1.13, 1.13, 1.13);
                drawItemStack(expected, 0, 0, 0);
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();

            AttenuationRecipe.AltarSlot as = AttenuationRecipe.AltarSlot.UPPER_LEFT;
            ItemStack stack = recipe.getItem(as);
            if(stack != null) {
                TextureHelper.refreshTextureBindState();
                GL11.glPushMatrix();
                GL11.glTranslated(offsetX + 30, offsetY + 78, zLevel + 60);
                GL11.glScaled(1.1, 1.1, 1.1);
                drawItemStack(stack, 0, 0, 0);
                GL11.glPopMatrix();
            }
            as = AttenuationRecipe.AltarSlot.UPPER_RIGHT;
            stack = recipe.getItem(as);
            if(stack != null) {
                TextureHelper.refreshTextureBindState();
                GL11.glPushMatrix();
                GL11.glTranslated(offsetX + 132, offsetY + 78, zLevel + 60);
                GL11.glScaled(1.1, 1.1, 1.1);
                drawItemStack(stack, 0, 0, 0);
                GL11.glPopMatrix();
            }
            as = AttenuationRecipe.AltarSlot.LOWER_LEFT;
            stack = recipe.getItem(as);
            if(stack != null) {
                TextureHelper.refreshTextureBindState();
                GL11.glPushMatrix();
                GL11.glTranslated(offsetX + 30, offsetY + 178, zLevel + 60);
                GL11.glScaled(1.1, 1.1, 1.1);
                drawItemStack(stack, 0, 0, 0);
                GL11.glPopMatrix();
            }
            as = AttenuationRecipe.AltarSlot.LOWER_RIGHT;
            stack = recipe.getItem(as);
            if(stack != null) {
                TextureHelper.refreshTextureBindState();
                GL11.glPushMatrix();
                GL11.glTranslated(offsetX + 132, offsetY + 178, zLevel + 60);
                GL11.glScaled(1.1, 1.1, 1.1);
                drawItemStack(stack, 0, 0, 0);
                GL11.glPopMatrix();
            }

            if(recipe.getPassiveStarlightRequired() > 0) {
                GL11.glPushMatrix();
                GL11.glTranslated(0, 0, 200);
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                Blending.DEFAULT.apply();
                String displReq = getDescriptionFromStarlightAmount(recipe.getPassiveStarlightRequired());
                displReq = I18n.translateToLocal(displReq);
                String dsc = I18n.translateToLocal("astralsorcery.journal.recipe.amt.desc");
                dsc = String.format(dsc, displReq);

                TextureHelper.refreshTextureBindState();
                getStandardFontRenderer().drawString(dsc, offsetX + 5F, offsetY + 210F, Color.LIGHT_GRAY.getRGB(), false);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                Blending.DEFAULT.apply();
                GL11.glPopMatrix();
            }

            /*if(recipe instanceof IAltarUpgradeRecipe) {
                TileAltar.AltarLevel to = ((IAltarUpgradeRecipe) recipe).getLevelUpgradingTo();
                BlockAltar.AltarType type = to.getCorrespondingAltarType();
                String typeDsc = "tile.BlockAltar." + type.getName() + ".name";
                String toDsc = I18n.translateToLocal("astralsorcery.journal.recipe.upgrade.desc");
                String dsc = String.format(toDsc, I18n.translateToLocal(typeDsc));
                int width = getStandardFontRenderer().getStringWidth(dsc) / 4;
                GL11.glPushMatrix();
                GL11.glTranslated(offsetX + 90 - width, offsetY + 56, 200);
                GL11.glScaled(0.5, 0.5, 0.5);
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);

                SpecialTextureLibrary.refreshTextureBindState();
                getStandardFontRenderer().drawString(dsc, 0, 0, Color.LIGHT_GRAY.getRGB(), false);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                BlendingHelper.DEFAULT.apply();
                GL11.glPopMatrix();
            }*/

            GL11.glDisable(GL11.GL_BLEND);
            RenderHelper.disableStandardItemLighting();

            GL11.glPopAttrib();
        }

    }

}
