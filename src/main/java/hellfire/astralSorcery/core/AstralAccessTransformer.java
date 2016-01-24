package hellfire.astralSorcery.core;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 20:17
 */
public class AstralAccessTransformer extends AccessTransformer {

    public AstralAccessTransformer() throws IOException {}

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        //Because we can and we don't have anything to do yet.
        if(true) return super.transform(name, transformedName, bytes);

        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);

        try {
            FMLLog.info("[AstralAccessTransformer] Transforming " + name + "/" + transformedName);
        } catch (Throwable tr) {} //Fck you load-order :P

        //Transformation

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        bytes = writer.toByteArray();
        return super.transform(name, transformedName, bytes);
    }
}
