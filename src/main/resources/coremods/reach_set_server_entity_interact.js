function initializeCoreMod() {
    return {
        'reach_set_server_entity_interact': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.network.play.ServerPlayNetHandler',
                'methodName': 'func_147340_a',
                'methodDesc': '(Lnet/minecraft/network/play/client/CUseEntityPacket;)V'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'reach_set_server_entity_interact\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');

                var playerField = ASMAPI.mapField('field_147369_b');

                var loadSqReach = ASMAPI.findFirstInstruction(method, Opcodes.LDC);
                while (loadSqReach.cst.doubleValue() != 36.0) {
                    loadSqReach = ASMAPI.findFirstInstructionAfter(method, Opcodes.LDC, method.instructions.indexOf(loadSqReach));
                }

                var prevLoadSqReach = loadSqReach.getPrevious();
                method.instructions.remove(loadSqReach);
                method.instructions.insert(prevLoadSqReach, ASMAPI.buildMethodCall(
                                    'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                                    'getOverriddenSeenEntityReachMaximum',
                                    '(Lnet/minecraft/entity/player/PlayerEntity;)D',
                                    ASMAPI.MethodType.STATIC));
                method.instructions.insert(prevLoadSqReach, new FieldInsnNode(Opcodes.GETFIELD,
                                    'net/minecraft/network/play/ServerPlayNetHandler',
                                    playerField,
                                    'Lnet/minecraft/entity/player/ServerPlayerEntity;'));
                method.instructions.insert(prevLoadSqReach, new VarInsnNode(Opcodes.ALOAD, 0));


                ASMAPI.log('INFO', 'Added \'reach_set_server_entity_interact\' ASM patch!');
                return method;
            }
        }
    }
}