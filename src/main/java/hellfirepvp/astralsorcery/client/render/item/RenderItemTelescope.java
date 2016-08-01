package hellfirepvp.astralsorcery.client.render.item;

import hellfirepvp.astralsorcery.client.render.entity.RenderEntityTelescope;
import hellfirepvp.astralsorcery.client.util.item.IItemRenderer;
import hellfirepvp.astralsorcery.common.entities.EntityTelescope;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderItemTelescope
 * Created by HellFirePvP
 * Date: 31.07.2016 / 18:00
 */
public class RenderItemTelescope implements IItemRenderer {

    private static final Render<? super EntityTelescope> renderTelescope = new RenderEntityTelescope.Factory().createRenderFor(Minecraft.getMinecraft().getRenderManager());
    private static final EntityTelescope dummy = new EntityTelescope(Minecraft.getMinecraft().theWorld);

    @Override
    public void render(ItemStack stack) {
        renderTelescope.doRender(dummy, 0, 0, 0, 0, Minecraft.getMinecraft().getRenderPartialTicks());
    }

}
