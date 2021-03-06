package tw.oresplus;

import java.io.File;
import java.util.Map;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLModIdMappingEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import tw.oresplus.api.OresPlusAPI;
import tw.oresplus.blocks.BlockCore;
import tw.oresplus.blocks.Blocks;
import tw.oresplus.blocks.TileEntityCracker;
import tw.oresplus.blocks.TileEntityGrinder;
import tw.oresplus.core.Config;
import tw.oresplus.core.GuiHandler;
import tw.oresplus.core.IMCHandler;
import tw.oresplus.core.IProxy;
import tw.oresplus.core.OreEventHandler;
import tw.oresplus.core.OreGenClass;
import tw.oresplus.core.OreLog;
import tw.oresplus.core.TickHandler;
import tw.oresplus.core.helpers.AppEngHelper;
import tw.oresplus.core.helpers.BCHelper;
import tw.oresplus.enums.OreGenerators;
import tw.oresplus.fluids.Fluids;
import tw.oresplus.items.ItemCore;
import tw.oresplus.items.Items;
import tw.oresplus.recipes.RecipeManager;
import tw.oresplus.worldgen.WorldGenCore;
import tw.oresplus.worldgen.WorldGenOre;

@Mod(modid = OresPlus.MOD_ID, name = OresPlus.MOD_NAME, version = OresPlus.MOD_VERSION)
public class OresPlus {
	
	@SidedProxy(clientSide="tw.oresplus.client.ClientProxy", serverSide="tw.oresplus.core.ServerProxy") 
	public static IProxy proxy;
	
    public static final String MOD_ID = "OresPlus";
    public static final String MOD_NAME = "OresPlus";
    public static final String MOD_VERSION = "0.2.10 Beta";
    
	@Instance(OresPlus.MOD_ID)
	public static OresPlus instance;

    public static OreLog log;
    public static Config config;
    
    public static String regenKeyOre = "DISABLED";
    public static String regenKeyOil = "DISABLED";
    
    public static WorldGenCore worldGen = new WorldGenCore();
    public static OreEventHandler eventHandler = new OreEventHandler();
    public static TickHandler tickHandler = new TickHandler();
    public static IMCHandler imcHandler = new IMCHandler();
    public static GuiHandler guiHandler = new GuiHandler();
    
    public static BCHelper bcHelper = new BCHelper();
    public static AppEngHelper appEngHelper = new AppEngHelper();
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	log.init();
    	config.init(event);
		config.load();
		
    	Blocks.init();
    	Items.init();
    	Fluids.init();
    	
    	//Register Ore Generators
    	log.info("Registering Ore Generators");
    	for (OreGenerators oreGen : OreGenerators.values()) {
    		OreGenClass ore = Config.getOreGen(oreGen.getDefaultConfig());
    		if (ore.enabled && (OresPlusAPI.getBlock(ore.oreName) != null)) 
   				new WorldGenOre(ore);
    	}
		config.save();
		
    	//GameData.dumpRegistry(new File(event.getModConfigurationDirectory() + "registryDump.log"));
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	RecipeManager.initRecipes();
    	
    	GameRegistry.registerTileEntity(TileEntityGrinder.class, "TileEntityGrinder");
    	GameRegistry.registerTileEntity(TileEntityCracker.class, "TileEntityCracker");
    	
    	MinecraftForge.EVENT_BUS.register(eventHandler);
    	MinecraftForge.ORE_GEN_BUS.register(eventHandler);
    	FMLCommonHandler.instance().bus().register(tickHandler);
    	
    	NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
    	
    	/* OreDictionaty dump
    	for (String ore : OreDictionary.getOreNames()) {
    		log.info(ore);
    	}
    	*/
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	bcHelper.init();
    }
    
    @EventHandler
	public void recieveIMC(FMLInterModComms.IMCEvent event) {
    	imcHandler.recieveIMC(event);
    }
    
    public void handleMissingMaps(FMLMissingMappingsEvent event) {
    	Blocks.handleMissingMaps(event);
    }
    
    public void handRemaps(FMLModIdMappingEvent event) {
    	Blocks.handleRemaps(event);
    }
    
}
