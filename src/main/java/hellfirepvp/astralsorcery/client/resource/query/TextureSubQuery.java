/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource.query;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TextureSubQuery
 * Created by HellFirePvP
 * Date: 10.06.2018 / 08:39
 */
public class TextureSubQuery extends TextureQuery {

    private final float uOffset, vOffset;
    private final float uLength, vLength;

    public TextureSubQuery(AssetLoader.TextureLocation location, String name, float uOffset, float vOffset, float uLength, float vLength) {
        super(location, name);
        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.uLength = uLength;
        this.vLength = vLength;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AbstractRenderableTexture resolve() {
        AbstractRenderableTexture res = super.resolve();
        return new AbstractRenderableTexture(res.getKey()) {

            @Override
            public void bindTexture() {
                res.bindTexture();
            }

            @Override
            public RenderState.TextureState asState() {
                return res.asState();
            }

            @Override
            public Tuple<Float, Float> getUVOffset() {
                return new Tuple<>(uOffset, vOffset);
            }

            @Override
            public float getUWidth() {
                return uLength;
            }

            @Override
            public float getVWidth() {
                return vLength;
            }

            @Override
            public boolean equals(Object obj) {
                return res.equals(obj);
            }

            @Override
            public int hashCode() {
                return res.hashCode();
            }
        };
    }
}
