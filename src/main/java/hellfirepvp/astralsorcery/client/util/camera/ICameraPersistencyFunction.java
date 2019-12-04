package hellfirepvp.astralsorcery.client.util.camera;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CameraPersistencyDelegate
 * Created by HellFirePvP
 * Date: 02.12.2019 / 20:05
 */
public interface ICameraPersistencyFunction {

    public boolean isExpired();

    public void setExpired();

    public void forceStop();

    public boolean wasForciblyStopped();

}
