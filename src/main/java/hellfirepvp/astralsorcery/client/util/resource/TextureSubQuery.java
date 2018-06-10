/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.resource;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TextureSubQuery
 * Created by HellFirePvP
 * Date: 10.06.2018 / 08:39
 */
public class TextureSubQuery extends TextureQuery {

    private final double uOffset, vOffset;
    private final double uLength, vLength;

    public TextureSubQuery(AssetLoader.TextureLocation location, String name, double uOffset, double vOffset, double uLength, double vLength) {
        super(location, name);
        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.uLength = uLength;
        this.vLength = vLength;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AbstractRenderableTexture resolve() {
        AbstractRenderableTexture res = super.resolve();
        return new AbstractRenderableTexture() {

            @Override
            public void bindTexture() {
                res.bindTexture();
            }

            @Override
            public Point.Double getUVOffset() {
                return new Point2D.Double(uOffset, vOffset);
            }

            @Override
            public double getUWidth() {
                return uLength;
            }

            @Override
            public double getVWidth() {
                return vLength;
            }
        };
    }
}
