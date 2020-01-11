/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MaterialBuilderAS
 * Created by HellFirePvP
 * Date: 30.05.2019 / 23:00
 */
public class MaterialBuilderAS {

    private final MaterialColor color;
    private PushReaction pushReaction = PushReaction.NORMAL;
    private boolean blocksMovement = true;
    private boolean canBurn = false;
    private boolean requiresNoTool = true;
    private boolean isLiquid = false;
    private boolean isReplaceable = false;
    private boolean isSolid = true;
    private boolean isOpaque = true;

    public MaterialBuilderAS(MaterialColor color) {
        this.color = color;
    }

    public MaterialBuilderAS liquid() {
        this.isLiquid = true;
        return this;
    }

    public MaterialBuilderAS notSolid() {
        this.isSolid = false;
        return this;
    }

    public MaterialBuilderAS doesNotBlockMovement() {
        this.blocksMovement = false;
        return this;
    }

    public MaterialBuilderAS notOpaque() {
        this.isOpaque = false;
        return this;
    }

    public MaterialBuilderAS requiresTool() {
        this.requiresNoTool = false;
        return this;
    }

    public MaterialBuilderAS flammable() {
        this.canBurn = true;
        return this;
    }

    public MaterialBuilderAS replaceable() {
        this.isReplaceable = true;
        return this;
    }

    public MaterialBuilderAS pushDestroys() {
        this.pushReaction = PushReaction.DESTROY;
        return this;
    }

    public MaterialBuilderAS pushBlocks() {
        this.pushReaction = PushReaction.BLOCK;
        return this;
    }

    public Material build() {
        return new Material(
                this.color,
                this.isLiquid,
                this.isSolid,
                this.blocksMovement,
                this.isOpaque,
                this.requiresNoTool,
                this.canBurn,
                this.isReplaceable,
                this.pushReaction);
    }
}
