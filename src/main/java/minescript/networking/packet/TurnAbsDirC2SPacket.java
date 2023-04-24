package minescript.networking.packet;

import interpreter.types.MSAbsDir;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class TurnAbsDirC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        ServerWorld world = player.getWorld();

        BlockPos pos = buf.readBlockPos();
        MSAbsDir.Direction direction = MSAbsDir.Direction.values()[buf.readInt()];
        BlockState state = world.getBlockState(pos);
        Direction facing = state.get(Properties.HORIZONTAL_FACING);

        switch (direction) {
            case NORTH -> facing = Direction.NORTH;
            case EAST -> facing = Direction.EAST;
            case SOUTH -> facing = Direction.SOUTH;
            case WEST -> facing = Direction.WEST;
        }

        world.setBlockState(pos, state.with(Properties.HORIZONTAL_FACING, facing), Block.NOTIFY_ALL);
    }
}
