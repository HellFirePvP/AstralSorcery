package hellfirepvp.astralsorcery.client.render.item;

import hellfirepvp.astralsorcery.client.render.entity.RenderEntityGrindstone;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntityTelescope;
import hellfirepvp.astralsorcery.client.util.item.IItemRenderer;
import hellfirepvp.astralsorcery.common.entities.EntityGrindstone;
import hellfirepvp.astralsorcery.common.entities.EntityTelescope;
import hellfirepvp.astralsorcery.common.item.ItemEntityPlacer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderItemEntityPlacer
 * Created by HellFirePvP
 * Date: 31.07.2016 / 18:00
 */
public class RenderItemEntityPlacer implements IItemRenderer {

    private static final Render<? super EntityTelescope> renderTelescope = new RenderEntityTelescope.Factory().createRenderFor(Minecraft.getMinecraft().getRenderManager());
    private static final Render<? super EntityGrindstone> renderGrindstone = new RenderEntityGrindstone.Factory().createRenderFor(Minecraft.getMinecraft().getRenderManager());

    private static final EntityTelescope telescopeDummy = new EntityTelescope(Minecraft.getMinecraft().theWorld);
    private static final EntityGrindstone grindstoneDummy = new EntityGrindstone(Minecraft.getMinecraft().theWorld);

    @Override
    public void render(ItemStack stack) {
        int meta = stack.getItemDamage();
        if(meta < 0 || meta >= ItemEntityPlacer.PlacerType.values().length) {
            return;
        }
        ItemEntityPlacer.PlacerType type = ItemEntityPlacer.PlacerType.values()[meta];
        switch (type) {
            case TELESCOPE:
                renderTelescope.doRender(telescopeDummy, 0, 0, 0, 0, Minecraft.getMinecraft().getRenderPartialTicks());
                break;
            case GRINDSTONE:
                renderGrindstone.doRender(grindstoneDummy, 0, 0, 0, 0, Minecraft.getMinecraft().getRenderPartialTicks());
                break;
        }
    }

}
