package phanastrae.mirthdew_encore.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreLogStripping;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlLevelAttachment;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;
import phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects;
import phanastrae.mirthdew_encore.fabric.block.MirthdewEncoreCompostChances;
import phanastrae.mirthdew_encore.fabric.fluid.MirthdewEncoreFluidVariantAttributes;
import phanastrae.mirthdew_encore.item.MirthdewEncoreCreativeModeTabs;
import phanastrae.mirthdew_encore.network.MirthdewEncorePayloads;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;
import phanastrae.mirthdew_encore.server.command.MirthdewEncoreCommands;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MirthdewEncoreFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		// registry
		MirthdewEncoreRegistries.registerRegistries(new MirthdewEncoreRegistries.Helper() {
			@Override
			public <T> void register(WritableRegistry<T> registry) {
				FabricRegistryBuilder.from(registry).buildAndRegister();
			}
		});
		MirthdewEncoreRegistries.registerSynced((DynamicRegistries::registerSynced));

		// mob effect registry
		MirthdewEncoreStatusEffects.init((name, mobEffect) -> Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, MirthdewEncore.id(name), mobEffect));

		// registry init
		MirthdewEncore.registriesInit(new MirthdewEncore.RegistryListenerAdder() {
			@Override
			public <T> void addRegistryListener(Registry<T> registry, Consumer<BiConsumer<ResourceLocation, T>> source) {
				source.accept((rl, t) -> Registry.register(registry, rl, t));
			}
		});

		// register fluid attributes
		MirthdewEncoreFluidVariantAttributes.init();

		// common init
		MirthdewEncore.commonInit();

		// entity attributes
		MirthdewEncoreEntityTypes.registerEntityAttributes((FabricDefaultAttributeRegistry::register));

		// creative tabs
		setupCreativeTabs();

		// register payloads
		registerServerPayloads();

		// setup composting chances
		MirthdewEncoreCompostChances.init();

		// setup log stripping
		MirthdewEncoreLogStripping.MIRTHDEW_STRIPPABLES.forEach(StrippableBlockRegistry::register);

		// register commands
		CommandRegistrationCallback.EVENT.register(MirthdewEncoreCommands::registerCommands);

		// world tick start
		ServerTickEvents.START_WORLD_TICK.register(DreamtwirlLevelAttachment::tickLevel);
	}

	public void setupCreativeTabs() {
		MirthdewEncoreCreativeModeTabs.setupEntires(new MirthdewEncoreCreativeModeTabs.Helper() {
			@Override
			public void add(ResourceKey<CreativeModeTab> tabKey, ItemLike item) {
				ItemGroupEvents.modifyEntriesEvent(tabKey).register(entries -> entries.accept(item));
			}

			@Override
			public void add(ResourceKey<CreativeModeTab> tabKey, ItemLike... items) {
				ItemGroupEvents.modifyEntriesEvent(tabKey).register(entries -> {
					for(ItemLike item : items) {
						entries.accept(item);
					}
				});
			}

			@Override
			public void add(ResourceKey<CreativeModeTab> tabKey, ItemStack item) {
				ItemGroupEvents.modifyEntriesEvent(tabKey).register(entries -> entries.accept(item));
			}

			@Override
			public void add(ResourceKey<CreativeModeTab> tabKey, Collection<ItemStack> items) {
				ItemGroupEvents.modifyEntriesEvent(tabKey).register(entries -> {
					for(ItemStack item : items) {
						entries.accept(item);
					}
				});
			}

			@Override
			public void addAfter(ItemLike after, ResourceKey<CreativeModeTab> tabKey, ItemLike item) {
				ItemGroupEvents.modifyEntriesEvent(tabKey).register(entries -> entries.addAfter(after, item));
			}

			@Override
			public void addAfter(ItemStack after, ResourceKey<CreativeModeTab> tabKey, ItemStack item) {
				ItemGroupEvents.modifyEntriesEvent(tabKey).register(entries -> entries.addAfter(after, item));
			}

			@Override
			public void addAfter(ItemLike after, ResourceKey<CreativeModeTab> tabKey, ItemLike... items) {
				ItemGroupEvents.modifyEntriesEvent(tabKey).register(entries -> entries.addAfter(after, items));
			}

			@Override
			public void forTabRun(ResourceKey<CreativeModeTab> tabKey, BiConsumer<CreativeModeTab.ItemDisplayParameters, CreativeModeTab.Output> biConsumer) {
				ItemGroupEvents.modifyEntriesEvent(tabKey).register(entries -> {
					CreativeModeTab.ItemDisplayParameters displayContext = entries.getContext();
					biConsumer.accept(displayContext, entries);
				});
			}

			@Override
			public boolean operatorTabEnabled() {
				// fabric seems to hide the operator tab automatically, so we can just return true here
				return true;
			}
		});
	}

	public void registerServerPayloads() {
		MirthdewEncorePayloads.init(new MirthdewEncorePayloads.Helper() {
			@Override
			public <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> clientCallback) {
				PayloadTypeRegistry.playS2C().register(id, codec);
			}

			@Override
			public <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> serverCallback) {
				PayloadTypeRegistry.playC2S().register(id, codec);
				ServerPlayNetworking.registerGlobalReceiver(id, (payload, context) -> serverCallback.accept(payload, context.player()));
			}
		});
	}
}