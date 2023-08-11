package com.pixelmoncreate.neoquantum.proxy;

import com.pixelmoncreate.neoquantum.config.Config;
import com.pixelmoncreate.neoquantum.network.Network;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

public class Proxy implements IProxy {
    @Nullable
    private static MinecraftServer server;
    @Nullable
    private static CreativeModeTab creativeModeTab;

    Proxy() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        if (checkClientInstance()) {
            Config.init();
            Network.init();
        }

        modEventBus.addListener(Proxy::commonSetup);
        modEventBus.addListener(Proxy::registerCapabilities);
        modEventBus.addListener(Proxy::imcEnqueue);
        modEventBus.addListener(Proxy::imcProcess);

        MinecraftForge.EVENT_BUS.addListener(Proxy::onAddReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(Proxy::serverStarted);
        MinecraftForge.EVENT_BUS.addListener(Proxy::serverStopping);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {

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

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return server;
    }

    public static class Client extends Proxy {
        public Client() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::clientSetup);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::postSetup);

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