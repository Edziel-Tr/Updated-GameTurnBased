package com.mycompany.gameat;

import java.util.*;

public class GameAT {
    static Scanner scan = new Scanner(System.in);
    static Random randomizer = new Random();

    public static void main(String[] args) {
        Player player = createPlayer();

        List<Item> inventory = new ArrayList<>();
        Set<String> completedQuests = new HashSet<>();
        int gold = 500;

        // Starter gear
        player.equip(new Equipment("Rusty Sword", "Weapon", "Common", 10, 0, 0, null));
        player.equip(new Equipment("Old Leather Armor", "Armor", "Common", 0, 20, 0, null));

        System.out.println("Welcome to the Isekai Adventure!");

        // Main game loop
        while (true) {
            System.out.println("\n[Your Stats] " + player.statsSummary());
            displayTravelOptions();
            String location = scan.nextLine().trim();
            if (location.equals("8") || location.equalsIgnoreCase("exit")) {
                System.out.println("Thank you for playing!");
                break;
            }
            handleTravel(player, inventory, completedQuests, location, gold);
        }
    }

    // --- GAME STRUCTURE / TRAVEL ---

    static void displayTravelOptions() {
        System.out.println("\nWhere would you like to travel?");
        System.out.println("1. Local Village");
        System.out.println("2. Mountain of Shraal");
        System.out.println("3. Kingdom of Dimitri");
        System.out.println("4. Empire of Chrono");
        System.out.println("5. Elven Realm");
        System.out.println("6. Human City");
        System.out.println("7. Enter Dungeon");
        System.out.println("8. Exit Game");
        System.out.print("Choose your destination: ");
    }

    static void handleTravel(Player player, List<Item> inventory, Set<String> completedQuests, String choice, int gold) {
        switch (choice) {
            case "1":
            case "village":
                enterVillage(player, inventory, gold, completedQuests);
                break;
            case "2":
            case "mountain":
                travelToMountain(player, inventory, completedQuests, gold);
                break;
            case "3":
            case "kingdom":
                travelToKingdom(player, inventory, completedQuests, gold);
                break;
            case "4":
            case "empire":
                travelToEmpire(player, inventory, completedQuests, gold);
                break;
            case "5":
            case "elven":
                travelToElvenRealm(player, inventory, completedQuests, gold);
                break;
            case "6":
            case "city":
                travelToHumanCity(player, inventory, completedQuests, gold);
                break;
            case "7":
            case "dungeon":
                enterDungeonFloors(player, inventory, completedQuests, gold);
                break;
            default:
                System.out.println("Unknown destination.");
        }
    }

    // --- CHARACTER CREATION ---

    static Player createPlayer() {
        System.out.println("A dazzling light envelops you...");
        System.out.println("A godly voice echoes: 'Welcome, chosen one! You are summoned to save this world!'");
        System.out.println("You are now blessed with the title: 'Hero of Another World.'");
        System.out.println("Before you begin, choose your path of power!");
        while (true) {
            System.out.println("Choose a Class: ");
            System.out.println("1. Knight - High HP, balanced damage.");
            System.out.println("2. Mage - Lower HP, high burst damage.");
            System.out.println("3. Ranger - Fast attacks, moderate HP.");
            System.out.println("4. Warrior - Balanced HP and damage.");
            System.out.print("Enter your class choice: ");
            String chosenClass = scan.nextLine().trim();
            switch (chosenClass.toLowerCase()) {
                case "1":
                case "knight":
                    return new Player("Qiann The Great", "Knight", 1200, 150, 1800);
                case "2":
                case "mage":
                    return new Player("Qiann The Great", "Mage", 800, 300, 2500);
                case "3":
                case "ranger":
                    return new Player("Qiann The Great", "Ranger", 900, 200, 2200);
                case "4":
                case "warrior":
                    return new Player("Qiann The Great", "Warrior", 1100, 180, 2000);
                default:
                    System.out.println("Invalid class choice.");
            }
        }
    }

    // --- VILLAGE / HIDDEN NPC LOGIC ---

    static void enterVillage(Player player, List<Item> inventory, int gold, Set<String> completedQuests) {
        System.out.println("\nYou arrive at a bustling local village.");
        List<String> presentNPCs = generateVillageNPCs();
        if (!completedQuests.contains("Hidden Herb Quest") && randomizer.nextDouble() < 0.4) {
            presentNPCs.add("Mysterious Hermit"); // Hidden NPC
        }
        System.out.println("You see the following NPCs:");
        for (int i = 0; i < presentNPCs.size(); i++) {
            System.out.println((i + 1) + ". " + presentNPCs.get(i));
        }
        boolean villageLoop = true;
        while (villageLoop) {
            System.out.println("\nTalk to whom? (number/name or 'leave')");
            String choice = scan.nextLine().trim();
            if (choice.equalsIgnoreCase("leave")) break;
            int npcIdx = getNpcIndex(choice, presentNPCs);
            if (npcIdx == -1) {
                System.out.println("No such NPC found. Try again.");
                continue;
            }
            String npc = presentNPCs.get(npcIdx);
            if (npc.equals("Mysterious Hermit")) {
                hiddenHermitQuest(inventory, completedQuests);
            } else {
                gold = interactWithNPC(npc, inventory, gold);
            }
            System.out.println("Gold: " + gold + ", Inventory: " + inventory);
        }
    }

    static void hiddenHermitQuest(List<Item> inventory, Set<String> completedQuests) {
        if (completedQuests.contains("Hidden Herb Quest")) {
            System.out.println("Mysterious Hermit: 'Thank you again, hero. My blessings are with you.'");
            return;
        }
        System.out.println("A Mysterious Hermit: 'Psst. Hero, can you find me a Silverleaf Herb from the Mountain of Shraal? I will reward you greatly.' (Accept quest? yes/no)");
        String accept = scan.nextLine().trim();
        if (accept.equalsIgnoreCase("yes")) {
            System.out.println("Quest accepted! Find a Silverleaf Herb in the Mountain of Shraal.");
        } else {
            System.out.println("Hermit: 'Perhaps another time...'");
        }
        // If they already have the herb
        Optional<Item> herb = inventory.stream().filter(i -> i.name.equals("Silverleaf Herb")).findFirst();
        if (herb.isPresent()) {
            System.out.println("You hand over the Silverleaf Herb.");
            inventory.remove(herb.get());
            completedQuests.add("Hidden Herb Quest");
            inventory.add(new Equipment("Elixir of Transcendence", "Potion", "Legendary", 0, 0, 0, "Restore all HP and grant +10% all stats for next fight"));
            inventory.add(new Equipment("Divine Shield Scroll", "Scroll", "Epic", 0, 0, 0, "Blocks next attack"));
            System.out.println("Hermit: 'My gratitude! Take this Elixir of Transcendence and a Divine Shield scroll!'");
        }
    }

    // --- LOCATION TEMPLATES (MEET RACES/NPCS) ---

    static void travelToMountain(Player player, List<Item> inventory, Set<String> completedQuests, int gold) {
        System.out.println("\nYou climb the treacherous slopes of the Mountain of Shraal...");
        if (randomizer.nextDouble() < 0.3 && inventory.stream().noneMatch(i -> i.name.equals("Silverleaf Herb"))) {
            System.out.println("You discover a rare Silverleaf Herb glimmering among the rocks!");
            inventory.add(new Equipment("Silverleaf Herb", "Herb", "Rare", 0, 0, 0, null));
        }
        if (randomizer.nextDouble() < 0.5) {
            Monster monster = randomizer.nextDouble() < 0.25
                    ? Monster.createMutated("Yeti", 1)
                    : Monster.createNormal("Yeti", 1);
            System.out.println("A wild " + monster.name + " attacks!");
            encounterMonster(player, monster, inventory);
        }
        if (randomizer.nextDouble() < 0.3) {
            System.out.println("You meet a Frost Dwarf. He trades you a Frost Axe for an Apple if you have one.");
            Optional<Item> apple = inventory.stream().filter(i -> i.name.equals("Apple")).findFirst();
            if (apple.isPresent()) {
                System.out.println("You trade your Apple for a Frost Axe!");
                inventory.remove(apple.get());
                inventory.add(new Equipment("Frost Axe", "Weapon", "Rare", 50, 0, 10, "Chance to freeze enemy"));
            }
        }
    }

    static void travelToKingdom(Player player, List<Item> inventory, Set<String> completedQuests, int gold) {
        System.out.println("\nYou travel to the Kingdom of Dimitri, home of chivalrous knights and grand tournaments.");
        if (randomizer.nextDouble() < 0.3) {
            System.out.println("You meet a Royal Knight who gifts you a Knight's Helm!");
            inventory.add(new Equipment("Knight's Helm", "Helm", "Rare", 0, 60, 0, "Reduce damage from bosses"));
        }
    }

    static void travelToEmpire(Player player, List<Item> inventory, Set<String> completedQuests, int gold) {
        System.out.println("\nYou visit the Empire of Chrono, land of powerful magic and time sorcery.");
        if (randomizer.nextDouble() < 0.2) {
            System.out.println("You meet a Time Sage who teaches you 'Skill Scroll: Time Stop'!");
            inventory.add(new Equipment("Time Stop Scroll", "Scroll", "Epic", 0, 0, 0, "Skip enemy turn in battle"));
        }
    }

    static void travelToElvenRealm(Player player, List<Item> inventory, Set<String> completedQuests, int gold) {
        System.out.println("\nYou walk under the ancient trees of the Elven Realm, where rare herbs and wise elves reside.");
        if (randomizer.nextDouble() < 0.4) {
            System.out.println("An Elf Healer shares a 'Potion of Eternal Youth' with you!");
            inventory.add(new Equipment("Potion of Eternal Youth", "Potion", "Epic", 0, 0, 0, "Resurrects once on death"));
        }
    }

    static void travelToHumanCity(Player player, List<Item> inventory, Set<String> completedQuests, int gold) {
        System.out.println("\nYou arrive in a bustling human city. Merchants hawk their wares, and children play in the streets.");
        if (randomizer.nextDouble() < 0.25) {
            System.out.println("You find a 'Lucky Coin'. Maybe it will bring fortune!");
            inventory.add(new Equipment("Lucky Coin", "Trinket", "Uncommon", 0, 0, 0, "Increases gold drops"));
        }
    }

    // --- DUNGEON SYSTEM ---

    static void enterDungeonFloors(Player player, List<Item> inventory, Set<String> completedQuests, int gold) {
        System.out.println("\nYou descend into the dungeon. There are 10 floors, each more dangerous than the last...");
        for (int floor = 1; floor <= 10; floor++) {
            String bossName = floor < 10 ? "Mini Boss (Floor " + floor + ")" : "Dungeon Overlord";
            Monster monster = (floor < 10)
                ? (randomizer.nextDouble() < 0.2
                   ? Monster.createMutated(bossName, floor)
                   : Monster.createNormal(bossName, floor))
                : Monster.createBoss("Dungeon Overlord");

            System.out.println("\n--- Floor " + floor + " ---");
            System.out.println("You encounter " + monster.name + " (HP: " + monster.hp + ")");
            boolean win = encounterMonster(player, monster, inventory);
            if (!win) {
                System.out.println("You are forced to retreat from the dungeon.");
                return;
            }
            if (floor == 10) {
                System.out.println("You have conquered the dungeon and defeated the Dungeon Overlord!");
                System.out.println("You gain a Legendary Sword and 2000 gold!");
                inventory.add(new Equipment("Legendary Sword", "Weapon", "Legendary", 200, 0, 50, "Double attack vs bosses"));
                break;
            } else {
                System.out.println("You find a rare chest and advance to the next floor...");
                grantRandomDungeonReward(inventory);
            }
        }
    }

    static void grantRandomDungeonReward(List<Item> inventory) {
        Equipment[] rewards = {
            new Equipment("Rare Potion", "Potion", "Rare", 0, 0, 0, "Heal 300 HP"),
            new Equipment("Skill Scroll: Meteor Strike", "Scroll", "Epic", 0, 0, 0, "Deal huge damage once"),
            new Equipment("Enchanted Armor", "Armor", "Epic", 0, 150, 0, "Reduce all damage by 10%"),
            new Equipment("Mystic Ring", "Ring", "Epic", 0, 40, 0, "Regenerate 20 HP per turn"),
            new Equipment("Elven Bow", "Weapon", "Epic", 80, 0, 0, "Chance to double hit")
        };
        Equipment reward = rewards[randomizer.nextInt(rewards.length)];
        System.out.println("You found: " + reward.getSummary() + "!");
        inventory.add(reward);
    }

    // --- ENCOUNTER / BATTLE ---

    static boolean encounterMonster(Player player, Monster monster, List<Item> inventory) {
        int playerHP = player.getTotalHP();
        int monsterHP = monster.hp;
        while (playerHP > 0 && monsterHP > 0) {
            System.out.println("\nYour HP: " + playerHP + " | " + monster.name + " HP: " + monsterHP);
            System.out.println("1. Attack\n2. Use Potion\n3. Attempt to Flee\n4. Equip Item");
            String action = scan.nextLine().trim();
            if (action.equals("1")) {
                int dmg = player.getTotalMinDmg() + randomizer.nextInt(player.getTotalMaxDmg() - player.getTotalMinDmg() + 1);
                if (player.hasPassive("Double attack vs bosses") && monster.name.contains("Boss")) {
                    System.out.println("Your Legendary Sword strikes twice!");
                    dmg *= 2;
                }
                monsterHP -= dmg;
                System.out.println("You dealt " + dmg + " damage!");
                // Life Steal example
                if (player.hasPassive("Life Steal")) {
                    int steal = Math.max(1, dmg / 10);
                    playerHP = Math.min(player.getTotalHP(), playerHP + steal);
                    System.out.println("Your Life Steal restores " + steal + " HP!");
                }
                // Meteor Strike (use scroll if available)
                Optional<Item> meteor = inventory.stream().filter(i -> i.name.contains("Meteor Strike")).findFirst();
                if (meteor.isPresent()) {
                    System.out.println("You unleash Meteor Strike!");
                    monsterHP -= 300;
                    inventory.remove(meteor.get());
                }
            } else if (action.equals("2")) {
                Optional<Item> potion = inventory.stream().filter(i -> i.name.contains("Potion")).findFirst();
                if (potion.isPresent()) {
                    int heal = potion.get().name.equals("Rare Potion") ? 300 : 100;
                    playerHP = Math.min(player.getTotalHP(), playerHP + heal);
                    System.out.println("You use a potion and restore " + heal + " HP!");
                    inventory.remove(potion.get());
                } else {
                    System.out.println("You have no Potions!");
                }
            } else if (action.equals("3")) {
                if (randomizer.nextDouble() < 0.5) {
                    System.out.println("You successfully fled!");
                    return false;
                } else {
                    System.out.println("You failed to flee!");
                }
            } else if (action.equals("4")) {
                showInventory(inventory);
                System.out.println("Type the item name to equip/use, or 'back': ");
                String eqName = scan.nextLine().trim();
                Optional<Item> eq = inventory.stream().filter(i -> i.name.equalsIgnoreCase(eqName)).findFirst();
                if (eq.isPresent() && eq.get() instanceof Equipment) {
                    player.equip((Equipment) eq.get());
                    System.out.println("Equipped " + eq.get().name + "!");
                } else if (eqName.equalsIgnoreCase("back")) {
                    continue;
                } else {
                    System.out.println("Not a valid equipment.");
                }
            } else {
                System.out.println("Invalid action.");
            }
            // Monster attacks
            if (monsterHP > 0) {
                int mdmg = monster.minDmg + randomizer.nextInt(monster.maxDmg - monster.minDmg + 1);
                if (monster.passive != null && monster.passive.equals("Poison")) {
                    System.out.println(monster.name + " poisons you!");
                    mdmg += 20;
                }
                if (player.hasPassive("Reduce damage from bosses") && monster.name.contains("Boss")) {
                    mdmg = (int)(mdmg * 0.7);
                    System.out.println("Your helm reduces damage from bosses!");
                }
                if (player.hasPassive("Reduce all damage by 10%")) {
                    mdmg = (int)(mdmg * 0.9);
                }
                playerHP -= mdmg;
                System.out.println(monster.name + " attacks for " + mdmg + " damage!");
                if (player.hasPassive("Regenerate 20 HP per turn")) {
                    playerHP = Math.min(player.getTotalHP(), playerHP + 20);
                    System.out.println("Your Mystic Ring regenerates 20 HP!");
                }
            }
        }
        if (playerHP <= 0) {
            if (player.hasPassive("Resurrects once on death")) {
                System.out.println("Your Potion of Eternal Youth revives you!");
                playerHP = player.getTotalHP() / 2;
            } else {
                System.out.println("You have fallen. Your adventure ends here.");
                System.exit(0);
            }
        }
        return true;
    }

    static void showInventory(List<Item> inventory) {
        System.out.println("Inventory:");
        for (Item i : inventory) {
            System.out.println(" - " + (i instanceof Equipment ? ((Equipment) i).getSummary() : i.name));
        }
    }

    // --- NPC SHOPS / INTERACTIONS (stub for brevity) ---

    static List<String> generateVillageNPCs() {
        String[] npcTypes = {"Blacksmith", "Mage", "Merchant", "Healer", "Guard", "Farmer", "Alchemist", "Hunter", "Priest"};
        int npcCount = randomizer.nextInt(3) + 3;
        List<String> presentNPCs = new ArrayList<>();
        while (presentNPCs.size() < npcCount) {
            String npc = npcTypes[randomizer.nextInt(npcTypes.length)];
            if (!presentNPCs.contains(npc)) presentNPCs.add(npc);
        }
        return presentNPCs;
    }

    static int getNpcIndex(String choice, List<String> npcs) {
        try {
            int idx = Integer.parseInt(choice) - 1;
            if (idx >= 0 && idx < npcs.size()) return idx;
        } catch (NumberFormatException e) {
            for (int i = 0; i < npcs.size(); i++) {
                if (npcs.get(i).equalsIgnoreCase(choice)) return i;
            }
        }
        return -1;
    }

    static int interactWithNPC(String npc, List<Item> inventory, int gold) {
        System.out.println("Interacting with " + npc + "... (shop logic goes here)");
        // Fill in with your shop/merchant logic as needed!
        return gold;
    }

    // --- ITEM/EQUIPMENT SYSTEM ---

    static abstract class Item {
        String name;
        Item(String name) { this.name = name; }
        public String toString() { return name; }
    }

    static class Equipment extends Item {
        String slot; // "Weapon", "Armor", "Helm", etc.
        String grade; // Common, Rare, Epic, Legendary
        int bonusDmg, bonusHP, bonusMinDmg;
        String passiveSkill; // e.g. "Life Steal", "Fire Immunity"
        Equipment(String name, String slot, String grade, int bonusDmg, int bonusHP, int bonusMinDmg, String passiveSkill) {
            super(name);
            this.slot = slot; this.grade = grade;
            this.bonusDmg = bonusDmg; this.bonusHP = bonusHP; this.bonusMinDmg = bonusMinDmg;
            this.passiveSkill = passiveSkill;
        }
        public String getSummary() {
            return String.format("%s [%s] (+Dmg:%d, +MinDmg:%d, +HP:%d)%s",
                    name, grade, bonusDmg, bonusMinDmg, bonusHP,
                    passiveSkill != null ? " [Passive: " + passiveSkill + "]" : "");
        }
    }

    // --- PLAYER ---

    static class Player {
        String name, chosenClass;
        int baseHP, baseMinDmg, baseMaxDmg;
        Map<String, Equipment> equipment = new HashMap<>(); // slot -> Equipment

        Player(String name, String chosenClass, int hp, int minDmg, int maxDmg) {
            this.name = name; this.chosenClass = chosenClass;
            this.baseHP = hp; this.baseMinDmg = minDmg; this.baseMaxDmg = maxDmg;
        }

        void equip(Equipment eq) {
            equipment.put(eq.slot, eq);
            System.out.println("Equipped: " + eq.getSummary());
        }

        int getTotalHP() {
            int hp = baseHP;
            for (Equipment eq : equipment.values()) hp += eq.bonusHP;
            return hp;
        }
        int getTotalMinDmg() {
            int min = baseMinDmg;
            for (Equipment eq : equipment.values()) min += eq.bonusMinDmg;
            return min;
        }
        int getTotalMaxDmg() {
            int max = baseMaxDmg;
            for (Equipment eq : equipment.values()) max += eq.bonusDmg;
            return max;
        }
        String statsSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("HP: " + getTotalHP())
                .append(", MinDmg: " + getTotalMinDmg())
                .append(", MaxDmg: " + getTotalMaxDmg());
            for (Equipment eq : equipment.values()) {
                sb.append("\n  - " + eq.getSummary());
            }
            return sb.toString();
        }
        boolean hasPassive(String passive) {
            for (Equipment eq : equipment.values()) {
                if (eq.passiveSkill != null && eq.passiveSkill.equals(passive)) return true;
            }
            return false;
        }
    }

    // --- MONSTER ---

    static class Monster {
        String name;
        int hp, minDmg, maxDmg;
        String passive;
        Monster(String name, int hp, int minDmg, int maxDmg, String passive) {
            this.name = name; this.hp = hp;
            this.minDmg = minDmg; this.maxDmg = maxDmg;
            this.passive = passive;
        }
        static Monster createNormal(String base, int floor) {
            int hp = 200 + floor * 50;
            int minDmg = 30 + floor * 8, maxDmg = 60 + floor * 10;
            return new Monster(base, hp, minDmg, maxDmg, null);
        }
        static Monster createMutated(String base, int floor) {
            int hp = 300 + floor * 100;
            int minDmg = 50 + floor * 15, maxDmg = 90 + floor * 18;
            String[] passives = {"Poison", "Frenzy", "Regeneration"};
            String passive = passives[randomizer.nextInt(passives.length)];
            return new Monster("Mutated " + base, hp, minDmg, maxDmg, passive);
        }
        static Monster createBoss(String name) {
            return new Monster(name, 2500, 180, 350, "Frenzy");
        }
    }
}
