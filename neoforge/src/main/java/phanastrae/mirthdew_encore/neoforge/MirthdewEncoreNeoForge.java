package phanastrae.mirthdew_encore.neoforge;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.*;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlLevelAttachment;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;
import phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects;
import phanastrae.mirthdew_encore.item.MirthdewEncoreCreativeModeTabs;
import phanastrae.mirthdew_encore.neoforge.fluid.MirthdewEncoreFluidTypes;
import phanastrae.mirthdew_encore.network.MirthdewEncorePayloads;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;
import phanastrae.mirthdew_encore.server.command.MirthdewEncoreCommands;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod(MirthdewEncore.MOD_ID)
public class MirthdewEncoreNeoForge {

    public MirthdewEncoreNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        setupModBusEvents(modEventBus);
        setupGameBusEvents(NeoForge.EVENT_BUS);
    }

    public void setupModBusEvents(IEventBus modEventBus) {
        // registry
        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(this::registerDatapackRegistries);

        // mob effect registry
        DeferredRegister<MobEffect> mobEffectDeferredRegister = DeferredRegister.create(Registries.MOB_EFFECT, MirthdewEncore.MOD_ID);
        mobEffectDeferredRegister.register(modEventBus);
        MirthdewEncoreStatusEffects.init((name, mobEffect) -> mobEffectDeferredRegister.register(name, () -> mobEffect));

        // registry init
        MirthdewEncore.RegistryListenerAdder RLA = new MirthdewEncore.RegistryListenerAdder() {
            @Override
            public <T> void addRegistryListener(Registry<T> registry, Consumer<BiConsumer<ResourceLocation, T>> source) {
                modEventBus.addListener((RegisterEvent event) -> {
                    ResourceKey<? extends Registry<T>> registryKey = registry.key();
                    if(registryKey.equals(event.getRegistryKey())) {
                        source.accept((resourceLocation, t) -> event.register(registryKey, resourceLocation, () -> t));
                    }
                });
            }
        };
        MirthdewEncore.registriesInit(RLA);
        this.neoforgeRegistriesInit(RLA);

        // common init
        modEventBus.addListener(this::commonInit);

        // entity attributes
        modEventBus.addListener(this::entityAttributeCreation);

        // creative tabs
        modEventBus.addListener(this::buildCreativeModeTabContents);

        // register payloads
        modEventBus.addListener(this::registerPayloadHandlers);
    }

    public void setupGameBusEvents(IEventBus gameEventBus) {
        // register commands
        gameEventBus.addListener(this::registerCommands);

        // world tick start
        gameEventBus.addListener(this::tickLevel);

        // add tooltips
        gameEventBus.addListener(this::addTooltips);
    }

    public void registerRegistries(NewRegistryEvent event) {
        MirthdewEncoreRegistries.registerRegistries(event::register);
    }

    public void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        MirthdewEncoreRegistries.registerSynced(new MirthdewEncoreRegistries.SyncedHelper() {
            @Override
            public <T> void register(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
                event.dataPackRegistry(registryKey, codec, codec);
            }
        });
    }

    public void neoforgeRegistriesInit(MirthdewEncore.RegistryListenerAdder registryListenerAdder) {
        registryListenerAdder.addRegistryListener(NeoForgeRegistries.FLUID_TYPES, MirthdewEncoreFluidTypes::init);
    }

    public void commonInit(FMLCommonSetupEvent event) {
        // everything here needs to be multithread safe
        event.enqueueWork(() -> {
            MirthdewEncore.commonInit();
            MirthdewEncoreFluidTypes.registerFluidInteractions();
        });
    }

    public void entityAttributeCreation(EntityAttributeCreationEvent event) {
        MirthdewEncoreEntityTypes.registerEntityAttributes(((entityType, builder) -> event.put(entityType, builder.build())));
    }

    public void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> eventKey = event.getTabKey();
        MirthdewEncoreCreativeModeTabs.setupEntires(new MirthdewEncoreCreativeModeTabs.Helper() {
            @Override
            public void add(ResourceKey<CreativeModeTab> tabKey, ItemLike item) {
                if(eventKey.equals(tabKey)) {
                    event.accept(item);
                }
            }

            @Override
            public void add(ResourceKey<CreativeModeTab> tabKey, ItemLike... items) {
                if(eventKey.equals(tabKey)) {
                    for(ItemLike item : items) {
                        event.accept(item);
                    }
                }
            }
            @Override
            public void add(ResourceKey<CreativeModeTab> tabKey, ItemStack item) {
                if(eventKey.equals(tabKey)) {
                    event.accept(item);
                }
            }

            @Override
            public void add(ResourceKey<CreativeModeTab> tabKey, Collection<ItemStack> items) {
                if(eventKey.equals(tabKey)) {
                    for(ItemStack item : items) {
                        event.accept(item);
                    }
                }
            }

            @Override
            public void addAfter(ItemLike after, ResourceKey<CreativeModeTab> tabKey, ItemLike item) {
                if(eventKey.equals(tabKey)) {
                    event.insertAfter(new ItemStack(after), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }

            @Override
            public void addAfter(ItemStack after, ResourceKey<CreativeModeTab> tabKey, ItemStack item) {
                if(eventKey.equals(tabKey)) {
                    event.insertAfter(after, item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }

            @Override
            public void addAfter(ItemLike after, ResourceKey<CreativeModeTab> tabKey, ItemLike... items) {
                if(eventKey.equals(tabKey)) {
                    for(ItemLike item : items) {
                        event.insertAfter(new ItemStack(after), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                        after = item;
                    }
                }
            }

            @Override
            public void forTabRun(ResourceKey<CreativeModeTab> tabKey, BiConsumer<CreativeModeTab.ItemDisplayParameters, CreativeModeTab.Output> biConsumer) {
                if(eventKey.equals(tabKey)) {
                    biConsumer.accept(event.getParameters(), event);
                }
            }

            @Override
            public boolean operatorTabEnabled() {
                return event.getParameters().hasPermissions();
            }
        });
    }

    public void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        MirthdewEncorePayloads.init(new MirthdewEncorePayloads.Helper() {
            @Override
            public <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> clientCallback) {
                registrar.playToClient(id, codec, (payload, context) -> clientCallback.accept(payload, context.player()));
            }

            @Override
            public <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> serverCallback) {
                registrar.playToServer(id, codec, (payload, context) -> serverCallback.accept(payload, context.player()));
            }
        });
    }

    public void registerCommands(RegisterCommandsEvent event) {
        MirthdewEncoreCommands.registerCommands(
                event.getDispatcher(),
                event.getBuildContext(),
                event.getCommandSelection()
        );
    }

    public void tickLevel(LevelTickEvent.Pre event) {
        DreamtwirlLevelAttachment.tickLevel(event.getLevel());
    }

    public void addTooltips(ItemTooltipEvent event) {
        MirthdewEncore.addTooltips(event.getItemStack(), event.getContext(), event.getToolTip()::add, event.getFlags());
    }
}
