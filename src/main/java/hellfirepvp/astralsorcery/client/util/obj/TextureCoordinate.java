package hellfirepvp.astralsorcery.client.util.obj;

/**
 * HellFirePvP@Admin
 * Date: 15.06.2015 / 00:07
 * on WingsExMod
 * TextureCoordinate
 */
public class TextureCoordinate {
    public float u, v, w;

    public TextureCoordinate(float u, float v) {
        this(u, v, 0F);
    }

    public TextureCoordinate(float u, float v, float w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }
}