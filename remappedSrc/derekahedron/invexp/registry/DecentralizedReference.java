package derekahedron.invexp.registry;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import org.jetbrains.annotations.NotNull;

/**
 * Special reference that does not need to have the same registry in memory to be encoded.
 * This exists so the SackInsertableManager can be static on both client and server, which
 * makes the code significantly more readable and manageable. This implementation would become
 * unnecessary if item component defaults were modifiable via Datapack, so I left this hacky
 * implementation in as I hope to remove it someday.
 *
 * @param <T> Object type of the reference
 */
public class DecentralizedReference<T> extends RegistryEntry.Reference<T> {
    public final int rawId;

    /**
     * Create a new decentralized reference from an existing reference and the
     * registry it comes from
     *
     * @param reference     reference to create new reference from
     * @param registry      registry this reference is attached to
     */
    public DecentralizedReference(@NotNull Reference<T> reference, @NotNull Registry<T> registry) {
        super(net.minecraft.registry.entry.RegistryEntry.Reference.Type.STAND_ALONE, registry, reference.registryKey(), reference.value());
        rawId = registry.getRawIdOrThrow(reference.value());
    }

    /**
     * We want to assume that all owners that have the same registry id are equal so this
     * can be encoded locally
     */
    @Override
    public boolean ownerEquals(RegistryEntryOwner<T> owner) {
        if (owner instanceof SimpleRegistry<T> registry) {
            return registryKey().getRegistry().equals(registry.getKey().getValue());
        }
        return false;
    }

    /**
     * Test if two references have the same key. This is used when testing if sack types
     * are equal in the components of sacks
     *
     * @param left      first reference to test
     * @param right     second reference to test
     * @param <T>       type that the reference belongs to
     * @return          true if the references are equal. false otherwise
     */
    public static <T> boolean referencesEqual(RegistryKey<T> left, RegistryKey<T> right) {
        return left.equals(right);
//        if (left == right) {
//            return true;
//        }
//        if (left instanceof RegistryEntry.Reference<T> leftReference && right instanceof RegistryEntry.Reference<T> rightReference) {
//            return leftReference.matchesKey(rightReference.registryKey());
//        }
//        return false;
    }

    /**
     * Create an entry packet codec that can use decentralized references
     *
     * @param registry      the registry key to create the codec for
     * @param directCodec   original packet codec to create from
     * @param <T>           type of codec
     * @return              an entry packet codec that can use decentralized references
     */
    public static <T> @NotNull PacketCodec<RegistryByteBuf, RegistryEntry<T>> entryPacketCodec(@NotNull final RegistryKey<? extends Registry<T>> registry, @NotNull final PacketCodec<? super RegistryByteBuf, T> directCodec) {
        // Create a general entry packet codec to base implementation off of
        PacketCodec<RegistryByteBuf, RegistryEntry<T>> codec = PacketCodecs.registryEntry(registry, directCodec);

        return new PacketCodec<>() {

            @Override
            public RegistryEntry<T> decode(RegistryByteBuf registryByteBuf) {
                return codec.decode(registryByteBuf);
            }

            @Override
            public void encode(RegistryByteBuf registryByteBuf, RegistryEntry<T> registryEntry) {
                // If the entry is a decentralized reference, use the prefetched raw id
                if (registryEntry instanceof DecentralizedReference<T> entry) {
                    // TODO Possibly add a check here to confirm that the entry with the existing raw id matches
                    VarInts.write(registryByteBuf, entry.rawId + 1);
                }
                else {
                    codec.encode(registryByteBuf, registryEntry);
                }
            }
        };
    }
}
