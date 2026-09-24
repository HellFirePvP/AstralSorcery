/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderSpriteUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderSpriteUtil {

    public static TextureAtlasSprite getTexture(ItemStack stack) {
        ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
        BakedModel model = ir.getModel(stack, null, null, 0);
        return model.getParticleIcon(ModelData.EMPTY);
    }

    public static TextureAtlasSprite getTexture(FluidStack stack) {
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(stack.getFluidType());
        ResourceLocation stillTextureId = ext.getStillTexture(stack);
        if (stillTextureId == null) {
            stillTextureId = MissingTextureAtlasSprite.getLocation();
        }
        return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(stillTextureId);
    }

    public static ColorWrapper getColorOverlay(FluidStack stack) {
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(stack.getFluidType());
        return ColorWrapper.transparent(ext.getTintColor(stack));
    }
}
