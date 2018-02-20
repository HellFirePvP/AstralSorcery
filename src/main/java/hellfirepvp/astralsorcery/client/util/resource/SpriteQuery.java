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

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpriteQuery
 * Created by HellFirePvP
 * Date: 31.03.2017 / 14:29
 */
public class SpriteQuery extends TextureQuery {

    private final int rows, columns;

    private Object spriteResource;

    public SpriteQuery(AssetLoader.TextureLocation location, String name, int rows, int columns) {
        super(location, name);
        this.rows = rows;
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    @SideOnly(Side.CLIENT)
    public SpriteSheetResource resolveSprite() {
        if(spriteResource == null) {
            BindableResource res = resolve();
            spriteResource = new SpriteSheetResource(res, getRows(), getColumns());
        }
        return (SpriteSheetResource) spriteResource;
    }

}
