package hellfirepvp.astralsorcery.client.resource;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAtlasTexture
 * Created by HellFirePvP
 * Date: 05.06.2020 / 21:54
 */
public class BlockAtlasTexture extends AbstractRenderableTexture.Full {

    private static final BlockAtlasTexture INSTANCE = new BlockAtlasTexture();

    private BlockAtlasTexture() {
        super(AstralSorcery.key("block_atlas_reference"));
    }

    public static BlockAtlasTexture getInstance() {
        return INSTANCE;
    }

    @Override
    public void bindTexture() {
        TextureManager mgr = Minecraft.getInstance().getTextureManager();
        mgr.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
    }

    @Override
    public RenderState.TextureState asState() {
        return new RenderState.TextureState(AtlasTexture.LOCATION_BLOCKS_TEXTURE, false, false);
    }
}
