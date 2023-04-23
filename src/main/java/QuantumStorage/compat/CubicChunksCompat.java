package QuantumStorage.compat;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import io.github.opencubicchunks.cubicchunks.core.server.CubeWatcher;
import io.github.opencubicchunks.cubicchunks.core.server.PlayerCubeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Loader;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.*;

/**
 * @author DaPorkchop_
 */
public final class CubicChunksCompat {
    private static final boolean CC;

    private static final MethodHandle CUBEWATCHER_PLAYERS_GETTER;

    static {
        boolean cc;
        try {
            Class.forName("io.github.opencubicchunks.cubicchunks.core.CubicChunks");
            cc = true;
        } catch (ClassNotFoundException e) {
            cc = false;
        }
        CC = cc;

        if (CC) {
            try {
                Field playersField = CubeWatcher.class.getDeclaredField("players");
                playersField.setAccessible(true);
                CUBEWATCHER_PLAYERS_GETTER = MethodHandles.publicLookup().unreflectGetter(playersField);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new AssertionError(e);
            }
        } else {
            CUBEWATCHER_PLAYERS_GETTER = null;
        }
    }

    public static void init() {
        boolean loaderCC = Loader.isModLoaded("cubicchunks");
        checkState(loaderCC == CC, "CC (%b) != Loader.isModLoaded(\"cubicchunks\") (%b)", CC, loaderCC);
    }

    private static ObjectArrayList<EntityPlayerMP> players(CubeWatcher cubeWatcher) {
        try {
            //noinspection unchecked
            return (ObjectArrayList<EntityPlayerMP>) CUBEWATCHER_PLAYERS_GETTER.invokeExact(cubeWatcher);
        } catch (Throwable t) {
            throw new AssertionError(t);
        }
    }

    public static List<EntityPlayerMP> findPlayersTrackingPosition(WorldServer world, BlockPos pos) {
        PlayerChunkMap playerChunkMap = world.getPlayerChunkMap();
        if (CC && playerChunkMap instanceof PlayerCubeMap) {
            CubeWatcher cubeWatcher = ((PlayerCubeMap) playerChunkMap).getCubeWatcher(CubePos.fromBlockCoords(pos));
            if (cubeWatcher != null) {
                return players(cubeWatcher);
            }
        } else {
            PlayerChunkMapEntry chunkMapEntry = playerChunkMap.getEntry(pos.getX() >> 4, pos.getZ() >> 4);
            if (chunkMapEntry != null) {
                return chunkMapEntry.getWatchingPlayers();
            }
        }

        return Collections.emptyList();
    }
}
