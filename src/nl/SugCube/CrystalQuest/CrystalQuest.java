package nl.SugCube.CrystalQuest;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;

import nl.SugCube.CrystalQuest.API.CrystalQuestAPI;
import nl.SugCube.CrystalQuest.Economy.Economy;
import nl.SugCube.CrystalQuest.Game.Ability;
import nl.SugCube.CrystalQuest.Game.Arena;
import nl.SugCube.CrystalQuest.Game.ArenaManager;
import nl.SugCube.CrystalQuest.Game.Classes;
import nl.SugCube.CrystalQuest.Game.InventoryManager;
import nl.SugCube.CrystalQuest.Game.Protection;
import nl.SugCube.CrystalQuest.IO.LoadData;
import nl.SugCube.CrystalQuest.IO.SaveData;
import nl.SugCube.CrystalQuest.InventoryMenu.PickTeam;
import nl.SugCube.CrystalQuest.InventoryMenu.SelectClass;
import nl.SugCube.CrystalQuest.InventoryMenu.SpectateArena;
import nl.SugCube.CrystalQuest.Items.ItemHandler;
import nl.SugCube.CrystalQuest.Items.ItemListener;
import nl.SugCube.CrystalQuest.Items.Wand;
import nl.SugCube.CrystalQuest.Listeners.ArenaListener;
import nl.SugCube.CrystalQuest.Listeners.DeathMessages;
import nl.SugCube.CrystalQuest.Listeners.EntityListener;
import nl.SugCube.CrystalQuest.Listeners.InventoryListener;
import nl.SugCube.CrystalQuest.Listeners.PlayerListener;
import nl.SugCube.CrystalQuest.Listeners.ProjectileListener;
import nl.SugCube.CrystalQuest.Listeners.SignListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrystalQuest extends JavaPlugin {
	
	public ArenaManager am;
	public PickTeam menuPT;
	public InventoryManager im;
	public StringHandler sh;
	public SelectClass menuSC;
	public SpectateArena menuSA;
	public CrystalQuestCommandExecutor commandExecutor;
	public Broadcast broadcast;
	public SaveData saveData;
	public LoadData loadData;
	public SignHandler signHandler;
	public ItemHandler itemHandler;
	public ParticleHandler particleHandler;
	public Classes classes;
	public Economy economy;
	public Ability ab;
	
	public PluginManager pm;
	public Protection prot;
	public DeathMessages deathListener;
	public EntityListener entityL;
	public InventoryListener inventoryL;
	public PlayerListener playerL;
	public SignListener signL;
	public ItemListener ppiL;
	public ProjectileListener projL;
	public ArenaListener arenaL;
	public Wand wand;
	
	public void onEnable() {
		/*
		 * Load config.yml and data.yml
		 */
		File file = new File(getDataFolder() + File.separator + "config.yml");
		if (!file.exists()) {
			try {
				getConfig().options().copyDefaults(true);
				saveConfig();
				this.getLogger().info("Generated config.yml succesfully!");
			} catch (Exception e) {
				this.getLogger().info("Failed to generate config.yml!");
			}
		}

		File df = new File(getDataFolder() + File.separator + "data.yml");
		if (!df.exists()) {
			try {
				reloadData();
				saveData();
				this.getLogger().info("Generated data.yml succesfully!");
			} catch (Exception e) {
				this.getLogger().info("Failed to generate data.yml!");
			}
		}
		
		File lf = new File(getDataFolder() + File.separator + "lang.yml");
		if (!lf.exists()) {
			try {
				getLang().options().copyDefaults(true);
				saveLang();
				this.getLogger().info("Generated lang.yml succesfully!");
			} catch (Exception e) {
				this.getLogger().info("Failed to generate lang.yml!");
			}
		}

		am = new ArenaManager(this);
		menuPT = new PickTeam(this);
		im = new InventoryManager(this);
		sh = new StringHandler(this);
		menuSC = new SelectClass(this);
		menuSA = new SpectateArena(this);
		commandExecutor = new CrystalQuestCommandExecutor(this);
		broadcast = new Broadcast(this);
		saveData = new SaveData(this);
		loadData = new LoadData(this);
		signHandler = new SignHandler(this, am);
		itemHandler = new ItemHandler(this);
		particleHandler = new ParticleHandler(this);
		classes = new Classes(this);
		prot = new Protection(this);
		deathListener = new DeathMessages(this);
		entityL = new EntityListener(this);
		inventoryL = new InventoryListener(this);
		playerL = new PlayerListener(this);
		signL = new SignListener(this);
		ppiL = new ItemListener(this);
		projL = new ProjectileListener(this);
		arenaL = new ArenaListener(this);
		wand = new Wand(this);
		PluginDescriptionFile pdfFile = this.getDescription();
		this.pm = getServer().getPluginManager();
		this.economy = new Economy(this, pm);
		this.ab = new Ability(this);
		
		/*
		 * Registering Events:
		 */
		pm.registerEvents(entityL, this);
		pm.registerEvents(inventoryL, this);
		pm.registerEvents(playerL, this);
		pm.registerEvents(signL, this);
		pm.registerEvents(ppiL, this);
		pm.registerEvents(projL, this);
		pm.registerEvents(deathListener, this);
		pm.registerEvents(prot, this);
		pm.registerEvents(ab, this);
		pm.registerEvents(wand, this);
		pm.registerEvents(arenaL, this);
		this.economy.registerEvents(pm);
		
		/*
		 * Registering Commands
		 */
		this.getCommand("cq").setExecutor(commandExecutor);

		this.getLogger().info("CrystalQuest v" + pdfFile.getVersion() + " has been enabled!");
		
		/*
		 * Starting the game-loops
		 * Initialize all arenas
		 */
		am.registerGameLoop();
		am.registerCrystalSpawningSequence();
		am.registerItemSpawningSequence();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ParticleHandler(this), 1L, 1L);
		signHandler.startSignUpdater(15);
		
		/*
		 * Loading Data
		 */
		LoadData.loadArenas();		//ARENAS
		LoadData.loadLobbySpawn();	//LOBBYSPAWN
		
		/*
		 * Pass plugin instance to the API
		 */
		CrystalQuestAPI.setPlugin(this);
		
		/*
		 * Check for updates
		 */
		if (this.getConfig().getBoolean("updates.check-for-updates")) {
			Update uc = new Update(69421 ,this.getDescription().getVersion());
			if (uc.query()) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CrystalQuest] <> A new version of CrystalQuest is " +
						"avaiable! Get it at the BukkitDev page! <>");
			} else {
				Bukkit.getConsoleSender().sendMessage("[CrystalQuest] CrystalQuest is up-to-date!");
			}
		}
		
		/*
		 * Resets all arenas and makes sure everything is ready to go.
		 */
		for (Arena a : am.getArenas()) {
			a.resetArena(true);
		}
		
		LoadData.loadSigns();		//Load the lobby-signs
		
		Broadcast.setMessages();	
		itemHandler.addAllItems();
		
	}
	
	public void onDisable() {
		
		PluginDescriptionFile pdfFile = this.getDescription();
		
		/*
		 * Kicks players from game on reload.
		 */
		for (Arena a : am.arena) {			
			a.removePlayers();
		}
		
		/*
		 * Reset all arenas
		 */
		for (Arena a : am.getArenas()) {
			a.resetArena(false);
		}
		
		/*
		 * Saves data
		 */
		SaveData.saveArenas();		//ARENAS
		SaveData.saveSigns();		//SIGNS
		SaveData.saveLobbySpawn();	//LOBBYSPAWN
		
		this.getLogger().info("[CrystalQuest] CrystalQuest v" + pdfFile.getVersion() + " has been disabled!");
		
	}
	
	/*
	 * Use a file (data.yml) to store data
	 */
	private FileConfiguration data = null;
	private File dataFile = null;
	
	/**
	 * Reloads the data-file
	 * @param void
	 * @return void
	 */
	public void reloadData() {
	    if (dataFile == null) {
	    	dataFile = new File(getDataFolder(), "data.yml");
	    }
	    data = YamlConfiguration.loadConfiguration(dataFile);
	 
	    InputStream defStream = this.getResource("data.yml");
	    if (defStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defStream);
	        data.setDefaults(defConfig);
	    }
	}
	
	/**
	 * Gets the data.yml
	 * @param void
	 * @return (FileConfiguration) Data.yml
	 */
	public FileConfiguration getData() {
	    if (data == null) {
	        this.reloadData();
	    }
	    return data;
	}
	
	/**
	 * Saves the data.yml
	 * @param void
	 * @return void
	 */
	public void saveData() {
	    if (data == null || dataFile == null) {
	    	return;
	    }
	    try {
	        getData().save(dataFile);
	    } catch (Exception ex) {
	        this.getLogger().log(Level.SEVERE, "Could not save config to " + dataFile, ex);
	    }
	}
	
	/*
	 * Use a file (language.yml) to store localized messages
	 */
	private FileConfiguration lang = null;
	private File langFile = null;
	
	/**
	 * Reloads the lang-file
	 * @param void
	 * @return void
	 */
	public void reloadLang() {
	    if (langFile == null) {
	    	langFile = new File(getDataFolder(), "lang.yml");
	    }
	    lang = YamlConfiguration.loadConfiguration(langFile);
	 
	    InputStream defStream = this.getResource("lang.yml");
	    if (defStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defStream);
	        lang.setDefaults(defConfig);
	    }
	}
	
	/**
	 * Gets the lang.yml
	 * @param void
	 * @return (FileConfiguration) lang.yml
	 */
	public FileConfiguration getLang() {
	    if (lang == null) {
	        this.reloadLang();
	    }
	    return lang;
	}
	
	/**
	 * Saves the lang.yml
	 * @param void
	 * @return void
	 */
	public void saveLang() {
	    if (lang == null || langFile == null) {
	    	return;
	    }
	    try {
	        getLang().save(langFile);
	    } catch (Exception ex) {
	        this.getLogger().log(Level.SEVERE, "Could not save " + langFile, ex);
	    }
	}
	
	/**
	 * Gets the InventoryManager of the plugin
	 * @param void
	 * @return (InventoryManager) The plugin's inventory manager.
	 */
	public InventoryManager getInventoryManager() {
		return this.im;
	}
	
	/**
	 * Gets the ArenaManager of the plugin
	 * @param void
	 * @return (ArenaManager) The plugin's arena manager.
	 */
	public ArenaManager getArenaManager() {
		return this.am;
	}
	
}