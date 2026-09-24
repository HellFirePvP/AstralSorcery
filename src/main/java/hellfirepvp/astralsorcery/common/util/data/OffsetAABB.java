/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import net.minecraft.world.phys.AABB;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: OffsetAABB
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class OffsetAABB {

    private final AABB box;
    private AABB movedBox;
    private Vector3 offset = new Vector3();

    public OffsetAABB(AABB box) {
        this.box = box;
        this.updateBox();
    }

    private void updateBox() {
        this.movedBox = this.box.move(this.offset.x, this.offset.y, this.offset.z);
    }

    public void setOffset(Vector3 offset) {
        this.offset = offset;
        this.updateBox();
    }

    public void move(Vector3 offset) {
        this.offset.add(offset);
        this.updateBox();
    }

    public AABB getMovedBox() {
        return this.movedBox;
    }
}
