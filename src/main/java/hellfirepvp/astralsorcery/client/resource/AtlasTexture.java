/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AtlasTexture
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AtlasTexture extends AbstractRenderTexture.Full {

    private static final AtlasTexture BLOCK_ATLAS = new AtlasTexture(InventoryMenu.BLOCK_ATLAS);
    private static final AtlasTexture LUMEN_ATLAS = new AtlasTexture(TexturesAS.ATLAS_LUMEN);

    private AtlasTexture(ResourceLocation atlasKey) {
        super(atlasKey);
    }

    public static AtlasTexture getBlockAtlas() {
        return BLOCK_ATLAS;
    }

    public static AtlasTexture getLumenAtlas() {
        return LUMEN_ATLAS;
    }

    @Override
    public void bindTexture() {
        RenderSystem.setShaderTexture(0, this.getKey());
    }

    @Override
    public RenderStateShard.TextureStateShard asState() {
        return new RenderStateShard.TextureStateShard(this.getKey(), false, false);
    }

    @Override
    public void invalidateAndReload() {
        // No-op, minecraft reloads atlases
    }
}
