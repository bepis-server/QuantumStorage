package QuantumStorage.utils;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author DaPorkchop_
 */
public interface INBTSerializableIntoExisting<T extends NBTBase> {
    static void trySerializeIntoOrMerge(NBTTagCompound compound, INBTSerializable<NBTTagCompound> serializable) {
        if (serializable instanceof INBTSerializableIntoExisting) { //porkman was here: try to do faster serialization without having to clone and merge the entire NBT tree
            //noinspection unchecked
            ((INBTSerializableIntoExisting<NBTTagCompound>) serializable).serializeNBT(compound);
        } else { //fall back to the old implementation
            compound.merge(serializable.serializeNBT());
        }
    }

    /**
     * Works like {@link INBTSerializable#serializeNBT()}, except that it puts values into the given {@link T tag} rather than creating a new one.
     * <p>
     * The existing tag's contents may be silently overwritten.
     *
     * @param nbt the tag to write into
     */
    void serializeNBT(T nbt);
}
