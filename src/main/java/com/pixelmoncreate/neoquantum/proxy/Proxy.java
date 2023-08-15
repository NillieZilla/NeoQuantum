package com.pixelmoncreate.neoquantum.proxy;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.pixelmoncreate.neoquantum.NeoQuantum;
import com.pixelmoncreate.neoquantum.client.ClientTickHandler;
import com.pixelmoncreate.neoquantum.config.Configuration;
import com.pixelmoncreate.neoquantum.event.InitialSpawnItems;
import com.pixelmoncreate.neoquantum.lib.item.NeoItemProperties;
import com.pixelmoncreate.neoquantum.lib.registry.NeoItemRegistry;
import com.pixelmoncreate.neoquantum.lib.registry.NeoSoundRegistry;
import com.pixelmoncreate.neoquantum.lib.registry.NeoTabRegistry;
import com.pixelmoncreate.neoquantum.lib.registry.NeoTriggerRegistry;
import com.pixelmoncreate.neoquantum.module.attribute.AttributesLib;
import com.pixelmoncreate.neoquantum.network.Network;
import com.pixelmoncreate.neoquantum.util.RunnableReloader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.RegisterEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.pixelmoncreate.neoquantum.lib.registry.NeoItemRegistry.quantumonium;
import static com.pixelmoncreate.neoquantum.util.Const.MOD_ID;
import static com.pixelmoncreate.neoquantum.util.Const.MOD_NAME;

/**
 * Moving registration away from the main class.
 * Used for registration and all client, common, and server lifecycle events.
 */
public class Proxy implements IProxy {
    @Nullable
    private static MinecraftServer server;
    public static float localAtkStrength = 1;
    public static File configDir;
    private static final Configuration config;
    private static final boolean enableAtt;
    private static final boolean giveBook;
    private static final boolean quantumonium3dmodel;

    static {
        configDir = new File(FMLPaths.CONFIGDIR.get().toFile(), MOD_NAME);
        config = new Configuration(new File(configDir, MOD_ID + ".cfg"));
        enableAtt = config.getBoolean("Enable Attribute Module", "general", true, "If the attribute module is enabled.");
        giveBook = config.getBoolean("Give Book on First Join", "general", true, "If the Mod Book is given to new players.");
        quantumonium3dmodel = config.getBoolean("Render the book in 3D.", "general", true, "If the Mod Book should be rendered in 3 Dimensions.");
        config.setTitle("Neo Quantum Module Control");
        config.setComment("This file allows individual modules of Neo Quantum to be enabled or disabled.\nChanges will have no effect until the next game restart.\nThis file must match on client and server.");
        if (config.hasChanged()) config.save();
    }

    Proxy() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        if (enableAtt) modEventBus.register(new AttributesLib());

        registryInit();

        if (checkClientInstance()) {
            Network.init();
        }
        if (config.hasChanged()) config.save();
        modEventBus.addListener(Proxy::commonSetup);
        modEventBus.addListener(Proxy::registerCapabilities);
        modEventBus.addListener(Proxy::imcEnqueue);
        modEventBus.addListener(Proxy::imcProcess);
        modEventBus.addListener(Proxy::creativeModeTabSetup);

        MinecraftForge.EVENT_BUS.addListener(this::reloads);
        MinecraftForge.EVENT_BUS.addListener(this::trackCooldown);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
        MinecraftForge.EVENT_BUS.addListener(this::onModelRegister);
        MinecraftForge.EVENT_BUS.addListener(Proxy::onAddReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(Proxy::serverStarted);
        MinecraftForge.EVENT_BUS.addListener(Proxy::serverStopping);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {

        InitialSpawnItems.add(NeoQuantum.getId("starter_book"), p -> {
            if (giveBook)
                return Collections.singleton(quantumonium.asItem().getDefaultInstance());
            return Collections.emptyList();
        });

        event.enqueueWork(NeoTriggerRegistry::init);

    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {

    }

    private static void imcEnqueue(InterModEnqueueEvent event) {

    }

    private static void imcProcess(InterModProcessEvent event) {
    }

    private static void onAddReloadListeners(AddReloadListenerEvent event) {

    }

    private static void serverStarted(ServerStartedEvent event) {

    }

    private static void serverStopping(ServerStoppingEvent event) {
        server = null;
    }

    public static boolean quantumonium3dmodel() {
        return quantumonium3dmodel;
    }

    private static void creativeModeTabSetup(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == NeoTabRegistry.NEO_TAB_KEY) {
            for (Item item : itemsToAddToCreativeTab) {
                event.accept(item);
            }
        }
    }

    @Nullable
    @Override
    public Player getClientPlayer() {
        return null;
    }

    @Nullable
    @Override
    public Level getClientLevel() {
        return null;
    }

    @Override
    public boolean checkClientInstance() {
        return true;
    }

    @Override
    public boolean checkClientConnection() {
        return true;
    }

    private void registryInit() {

        bind(Registries.SOUND_EVENT, NeoSoundRegistry::init);
        bindForItems(NeoItemRegistry::registerItems);
        bind(Registries.CREATIVE_MODE_TAB, consumer -> {
            consumer.accept(CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.neoquantum.neoquantum"))
                            .hideTitle()
                            .icon(() -> new ItemStack(quantumonium))
                            .withTabsBefore(CreativeModeTabs.NATURAL_BLOCKS)
                            .backgroundSuffix("neoquantum.png")
                            .withSearchBar()
                            .build(),
                    NeoTabRegistry.NEO_TAB_KEY.location());
        });

    }

    private static <T> void bind(ResourceKey<Registry<T>> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent event) -> {
            if (registry.equals(event.getRegistryKey())) {
                source.accept((t, rl) -> event.register(registry, rl, () -> t));
            }
        });
    }

    public static Set<Item> itemsToAddToCreativeTab = new LinkedHashSet<>();

    private void bindForItems(Consumer<BiConsumer<Item, ResourceLocation>> source) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent event) -> {
            if (event.getRegistryKey().equals(Registries.ITEM)) {
                source.accept((t, rl) -> {
                    itemsToAddToCreativeTab.add(t);
                    event.register(Registries.ITEM, rl, () -> t);
                });
            }
        });
    }

    public void trackCooldown(AttackEntityEvent event) {
        Player p = event.getEntity();
        localAtkStrength = p.getAttackStrengthScale(0.5F);
    }


    public void reloads(AddReloadListenerEvent event) {
        event.addListener(RunnableReloader.of(() -> MinecraftForge.EVENT_BUS.post(new NeoQuantumReloadEvent())));
    }

    public void commands(RegisterCommandsEvent event) {
        var builder = Commands.literal("neo");
        MinecraftForge.EVENT_BUS.post(new NeoQuantumCommandEvent(builder));
        event.getDispatcher().register(builder);
    }

   public void onModelRegister(ModelEvent.RegisterAdditional event) {
       var resourceManager = Minecraft.getInstance().getResourceManager();
       NeoItemProperties.init((item, id, prop) -> ItemProperties.register(item.asItem(), id, prop));
    }

    public static class NeoQuantumReloadEvent extends Event {}

    public static class NeoQuantumCommandEvent extends Event {

        private final LiteralArgumentBuilder<CommandSourceStack> root;

        public NeoQuantumCommandEvent(LiteralArgumentBuilder<CommandSourceStack> root) {
            this.root = root;
        }

        public LiteralArgumentBuilder<CommandSourceStack> getRoot() {
            return this.root;
        }
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return server;
    }

    public static class Client extends Proxy {
        public Client() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::clientSetup);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::postSetup);

           MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
               if (e.phase == TickEvent.Phase.END) {
                   ClientTickHandler.clientTickEnd(Minecraft.getInstance());
               }
           });

           MinecraftForge.EVENT_BUS.addListener((TickEvent.RenderTickEvent e) -> {
               if (e.phase == TickEvent.Phase.START) {
                   ClientTickHandler.renderTick(e.renderTickTime);
               }
           });

           MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);

            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        }

        private static void clientSetup(FMLClientSetupEvent event) {

        }

        private static void postSetup(FMLLoadCompleteEvent event) {

        }

        private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

        }

        @Nullable
        @Override
        public Player getClientPlayer() {
            return Minecraft.getInstance().player;
        }

        @Nullable
        @Override
        public Level getClientLevel() {
            Minecraft mc = Minecraft.getInstance();
            //noinspection ConstantConditions -- mc can be null during runData and some other circumstances
            return mc != null ? mc.level : null;
        }

        @Override
        public boolean checkClientInstance() {
            //noinspection ConstantConditions -- mc can be null during runData and some other circumstances
            return Minecraft.getInstance() != null;
        }

        @Override
        public boolean checkClientConnection() {
            Minecraft mc = Minecraft.getInstance();
            //noinspection ConstantConditions -- mc can be null during runData and some other circumstances
            return mc != null && mc.getConnection() != null;
        }
    }

    public static class Server extends Proxy {
        public Server() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) {
        }
    }

}