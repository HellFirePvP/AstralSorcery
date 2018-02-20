/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.item;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DummyModelLoader
 * Created by HellFirePvP
 * Date: 23.07.2016 / 16:17
 */
public class DummyModelLoader implements ICustomModelLoader {

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return ItemRenderRegistry.isRegistered(modelLocation);
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        return new ItemRendererModelDummy(modelLocation);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public String toString() {
        return "IItemRenderer-DummyModelLoader";
    }
}
