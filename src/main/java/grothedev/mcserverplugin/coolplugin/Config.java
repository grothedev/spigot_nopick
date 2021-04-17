public class Config{
    public static float CREEPER_BLAST;
    public static float DIRT_DROP_CHANCE;
    public static float SKELE_HP_MULT;
    public static float CREEPER_FUSETIME;

    public static Material[] WALL_BLOCK_TYPES_0 = {
        Material.CHISELED_POLISHED_BLACKSTONE,
        Material.CHISELED_STONE_BRICKS,
        Material.COBBLESTONE,
        Material.CRACKED_STONE_BRICKS,
        Material.CRACKED_POLISHED_BLACKSTONE_BRICKS,
        Material.MOSSY_COBBLESTONE,
        Material.MOSSY_STONE_BRICKS,
        Material.POLISHED_ANDESITE,
        Material.POLISHED_BLACKSTONE,
        Material.POLISHED_DIORITE,
        Material.POLISHED_BLACKSTONE_BRICKS,
        Material.POLISHED_GRANITE
    };
    public static Material[] WALL_BLOCK_TYPES_1 = {};
    public static Material[] WALL_BLOCK_TYPES_2 = {};

    public static HashSet<Material> BANNED_ITEMS = new HashSet<Material>();
    {
        BANNED_ITEMS.add(Material.CHARCOAL);
        BANNED_ITEMS.add(Material.BUCKET);
        BANNED_ITEMS.add(Material.IRON_PICKAXE);
        BANNED_ITEMS.add(Material.DIAMOND_PICKAXE);
        BANNED_ITEMS.add(Material.GOLDEN_PICKAXE);
        BANNED_ITEMS.add(Material.NETHERITE_PICKAXE);
        BANNED_ITEMS.add(Material.STONE_PICKAXE);
        BANNED_ITEMS.add(Material.WOODEN_PICKAXE);
        
    }
}