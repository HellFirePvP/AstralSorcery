function initializeCoreMod() {
    return {
        'add_custom_collision_flag': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.util.math.shapes.VoxelShapeSpliterator'
            },
            'transformer': function(classNode) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'add_custom_collision_flag\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var FieldNode = Java.type('org.objectweb.asm.tree.FieldNode');

                var didCustomCollision = new FieldNode(Opcodes.ACC_PUBLIC, 'as_didCustomCollision', 'Z', '', false);
                classNode.fields.add(didCustomCollision);

                ASMAPI.log('INFO', 'Added \'add_custom_collision_flag\' ASM patch!');
                return classNode;
            }
        }
    }
}