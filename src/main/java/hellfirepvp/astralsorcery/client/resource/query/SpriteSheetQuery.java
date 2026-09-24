/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource.query;

import hellfirepvp.astralsorcery.client.resource.AssetLocation;
import hellfirepvp.astralsorcery.client.resource.SpriteSheet;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SpriteSheetQuery
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SpriteSheetQuery extends TextureQuery {

    private final int rows, columns;

    private Object spriteResource;

    public SpriteSheetQuery(AssetLocation location, int rows, int columns, String... path) {
        super(location, path);
        this.rows = rows;
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public SpriteSheet resolveSprite() {
        if (this.spriteResource == null) {
            this.spriteResource = new SpriteSheet(this.resolve(), getRows(), getColumns());
        }
        return (SpriteSheet) this.spriteResource;
    }
}
