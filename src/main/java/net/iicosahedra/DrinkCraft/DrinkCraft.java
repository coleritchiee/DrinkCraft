package net.iicosahedra.DrinkCraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

public class DrinkCraft extends JavaPlugin implements Listener {

    private List<String> completedAchievements;
    private Set<String> sipAchievements;
    private Set<String> shotAchievements;
    private File completedAchievementsFile;
    private File sipAchievementsFile;
    private File shotAchievementsFile;

    private static final List<String> DEFAULT_SHOT_ACHIEVEMENTS = Arrays.asList(
            "minecraft:story/enter_the_nether",
            "minecraft:story/enter_the_end",
            "minecraft:end/kill_dragon",
            "minecraft:end/dragon_egg"
    );

    private static final List<String> DEFAULT_SIP_ACHIEVEMENTS = Arrays.asList(
            "minecraft:story/mine_stone","minecraft:story/upgrade_tools","minecraft:story/smelt_iron","minecraft:story/obtain_armor","minecraft:story/lava_bucket","minecraft:story/iron_tools","minecraft:story/deflect_arrow","minecraft:story/form_obsidian","minecraft:story/mine_diamond","minecraft:story/shiny_gear","minecraft:story/enchant_item","minecraft:story/cure_zombie_villager","minecraft:story/follow_ender_eye","minecraft:nether/return_to_sender","minecraft:nether/find_bastion","minecraft:nether/obtain_ancient_debris","minecraft:nether/fast_travel","minecraft:nether/find_fortress","minecraft:nether/obtain_crying_obsidian","minecraft:nether/distract_piglin","minecraft:nether/ride_strider","minecraft:nether/uneasy_alliance","minecraft:nether/loot_bastion","minecraft:nether/use_lodestone","minecraft:nether/netherite_armor","minecraft:nether/get_wither_skull","minecraft:nether/obtain_blaze_rod","minecraft:nether/charge_respawn_anchor","minecraft:nether/ride_strider_in_overworld_lava","minecraft:nether/explore_nether","minecraft:nether/summon_wither","minecraft:nether/brew_potion","minecraft:nether/create_beacon","minecraft:nether/all_potions","minecraft:nether/create_full_beacon","minecraft:nether/all_effects","minecraft:end/enter_end_gateway","minecraft:end/respawn_dragon","minecraft:end/dragon_breath","minecraft:end/find_end_city","minecraft:end/elytra","minecraft:end/levitate","minecraft:adventure/voluntary_exile","minecraft:adventure/spyglass_at_parrot","minecraft:adventure/kill_a_mob","minecraft:adventure/read_power_of_chiseled_bookshelf","minecraft:adventure/trade","minecraft:adventure/trim_with_any_armor_pattern","minecraft:adventure/honey_block_slide","minecraft:adventure/ol_betsy","minecraft:adventure/lightning_rod_with_villager_no_fire","minecraft:adventure/fall_from_world_height","minecraft:adventure/salvage_sherd","minecraft:adventure/avoid_vibration","minecraft:adventure/sleep_in_bed","minecraft:adventure/hero_of_the_village","minecraft:adventure/spyglass_at_ghast","minecraft:adventure/throw_trident","minecraft:adventure/kill_mob_near_sculk_catalyst","minecraft:adventure/shoot_arrow","minecraft:adventure/kill_all_mobs","minecraft:adventure/totem_of_undying","minecraft:adventure/summon_iron_golem","minecraft:adventure/trade_at_world_height","minecraft:adventure/trim_with_all_exclusive_armor_patterns","minecraft:adventure/two_birds_one_arrow","minecraft:adventure/whos_the_pillager_now","minecraft:adventure/arbalistic","minecraft:adventure/craft_decorated_pot_using_only_sherds","minecraft:adventure/adventuring_time","minecraft:adventure/play_jukebox_in_meadows","minecraft:adventure/walk_on_powder_snow_with_leather_boots","minecraft:adventure/spyglass_at_dragon","minecraft:adventure/very_very_frightening","minecraft:adventure/sniper_duel","minecraft:adventure/bullseye","minecraft:husbandry/safely_harvest_honey","minecraft:husbandry/breed_an_animal","minecraft:husbandry/allay_deliver_item_to_player","minecraft:husbandry/ride_a_boat_with_a_goat","minecraft:husbandry/tame_an_animal","minecraft:husbandry/make_a_sign_glow","minecraft:husbandry/fishy_business","minecraft:husbandry/silk_touch_nest","minecraft:husbandry/tadpole_in_a_bucket","minecraft:husbandry/obtain_sniffer_egg","minecraft:husbandry/plant_seed","minecraft:husbandry/wax_on","minecraft:husbandry/bred_all_animals","minecraft:husbandry/allay_deliver_cake_to_note_block","minecraft:husbandry/complete_catalogue","minecraft:husbandry/tactical_fishing","minecraft:husbandry/leash_all_frog_variants","minecraft:husbandry/feed_snifflet","minecraft:husbandry/balanced_diet","minecraft:husbandry/obtain_netherite_hoe","minecraft:husbandry/wax_off","minecraft:husbandry/axolotl_in_a_bucket","minecraft:husbandry/froglights","minecraft:husbandry/plant_any_sniffer_seed","minecraft:husbandry/kill_axolotl_target"
    );

    @Override
    public void onEnable() {
        completedAchievementsFile = new File(getDataFolder(), "CompletedAchievements.txt");
        sipAchievementsFile = new File(getDataFolder(), "SipAchievements.txt");
        shotAchievementsFile = new File(getDataFolder(), "ShotAchievements.txt");

        completedAchievements = new ArrayList<>();
        sipAchievements = new HashSet<>();
        shotAchievements = new HashSet<>();

        loadAchievements();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        saveCompletedAchievements();
    }

    private void loadAchievements() {
        loadCompletedAchievements();
        loadConfigAchievements(sipAchievementsFile, sipAchievements, "Sip", DEFAULT_SIP_ACHIEVEMENTS);
        loadConfigAchievements(shotAchievementsFile, shotAchievements, "Shot", DEFAULT_SHOT_ACHIEVEMENTS);
    }

    private void loadCompletedAchievements() {
        if (!completedAchievementsFile.exists()) {
            try {
                completedAchievementsFile.getParentFile().mkdirs();
                completedAchievementsFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Could not create completed achievements file: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(completedAchievementsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                completedAchievements.add(line.trim());
            }
        } catch (IOException e) {
            getLogger().severe("Could not load completed achievements: " + e.getMessage());
        }
    }

    private void loadConfigAchievements(File file, Set<String> achievementSet, String type, List<String> defaultAchievements) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                writeDefaultAchievements(file, defaultAchievements);
            } catch (IOException e) {
                getLogger().severe("Could not create " + type + " achievements file: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                achievementSet.add(line.trim());
            }
        } catch (IOException e) {
            getLogger().severe("Could not load " + type + " achievements: " + e.getMessage());
        }
    }

    private void writeDefaultAchievements(File file, List<String> defaultAchievements) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String achievement : defaultAchievements) {
                writer.write(achievement);
                writer.newLine();
            }
        }
    }

    private void saveCompletedAchievements() {
        try {
            if (!completedAchievementsFile.exists()) {
                completedAchievementsFile.getParentFile().mkdirs();
                completedAchievementsFile.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(completedAchievementsFile))) {
                for (String achievement : completedAchievements) {
                    writer.write(achievement);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            getLogger().severe("Could not save completed achievements: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        String achievementKey = advancement.getKey().toString();

        if (!completedAchievements.contains(achievementKey) && (sipAchievements.contains(achievementKey) || shotAchievements.contains(achievementKey))) {
            completedAchievements.add(achievementKey);

            if (sipAchievements.contains(achievementKey)) {
                broadcastAchievement(event.getPlayer().getName(), "Sip", advancement);
            } else if (shotAchievements.contains(achievementKey)) {
                broadcastAchievement(event.getPlayer().getName(), "Shot", advancement);
            }

            saveCompletedAchievements();
        }
    }

    private void broadcastAchievement(String playerName, String type, Advancement advancement) {
        String title = ChatColor.BOLD + "" + ChatColor.RED + "DRINK";
        String subtitle = ChatColor.YELLOW + "Take a " + type.toLowerCase();
        String chatMessage = title + " " +
                subtitle + " " +
                ChatColor.AQUA + "caused by " + playerName + " for " + advancement.getDisplay().getTitle();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, subtitle, 10, 70, 20);
            player.sendMessage(chatMessage);
        }
    }
}