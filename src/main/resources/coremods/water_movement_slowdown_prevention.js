function initializeCoreMod() {
    return {
        'water_movement_slowdown_prevention': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.entity.LivingEntity',
                'methodName': 'func_189749_co',
                'methodDesc': '()F'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'water_movement_slowdown_prevention\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var putSkyLight = ASMAPI.findFirstInstructionAfter(method, Opcodes.PUTFIELD, 0);

                var node = method.instructions.getFirst();
                while ((node = ASMAPI.findFirstInstructionAfter(method, Opcodes.FRETURN, method.instructions.indexOf(node))) !== null) {
                    method.instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 0));
                    method.instructions.insertBefore(node, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'getLivingEntityWaterSlowDown',
                        "(FLnet/minecraft/entity/LivingEntity;)F",
                        ASMAPI.MethodType.STATIC));

                    break;
                }

                ASMAPI.log('INFO', 'Added \'water_movement_slowdown_prevention\' ASM patch!');
                return method;
            }
        }
    }
}