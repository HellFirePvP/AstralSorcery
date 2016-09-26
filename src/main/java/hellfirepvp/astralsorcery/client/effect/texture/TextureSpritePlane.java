package hellfirepvp.astralsorcery.client.effect.texture;

import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.util.Axis;
import hellfirepvp.astralsorcery.common.util.data.Tuple;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TextureSpritePlane
 * Created by HellFirePvP
 * Date: 25.09.2016 / 22:52
 */
public class TextureSpritePlane extends TexturePlane {

    private final SpriteSheetResource spriteSheet;

    public TextureSpritePlane(SpriteSheetResource spriteSheet, Axis axis) {
        super(spriteSheet.getResource(), axis);
        this.spriteSheet = spriteSheet;
        this.uLength = spriteSheet.getULength();
        this.vLength = spriteSheet.getVLength();
        this.setMaxAge(spriteSheet.getFrameCount());
    }

    public SpriteSheetResource getSpriteSheet() {
        return spriteSheet;
    }

    @Override
    public void tick() {
        int frame = getAge();
        Tuple<Double, Double> uv = spriteSheet.getUVOffset(frame);

        this.u = uv.key;
        this.v = uv.value;

        super.tick();
    }

}
