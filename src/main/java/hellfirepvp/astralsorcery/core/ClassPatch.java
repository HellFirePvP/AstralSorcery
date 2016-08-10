package hellfirepvp.astralsorcery.core;

import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClassPatch
 * Created by HellFirePvP
 * Date: 04.08.2016 / 00:39
 */
public abstract class ClassPatch {

    private final String className;//, classNameObf;

    public ClassPatch(String className) {
        this.className = className;
    }

    public byte[] transform(byte[] bytes) {
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);

        if(!patch(node)) {
            FMLLog.warning("[AstralSorcery] Failed to apply " + getClass().getSimpleName().toUpperCase() + " ClassPatch!");
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        bytes = writer.toByteArray();

        try {
            File f = new File("C:/ASTestClasses/" + getClass().getSimpleName() + ".class");
            f.getParentFile().mkdirs();
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            out.write(bytes);
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public abstract boolean patch(ClassNode cn);

    public String getClassName() {
        return className;
    }

    /*public String getClassName() {
        return FMLForgePlugin.RUNTIME_DEOBF ? classNameObf : classNameDeobf;
    }*/

    @Nullable
    public MethodNode getMethod(ClassNode cn, String deobf, String obf, String sig) {
        String name = FMLForgePlugin.RUNTIME_DEOBF ? obf : deobf;
        for (MethodNode m : cn.methods) {
            if (m.name.equals(name) && m.desc.equals(sig)) {
                return m;
            }
        }
        return null;
    }

}
