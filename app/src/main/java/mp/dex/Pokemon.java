package mp.dex;


public class Pokemon {
    //The abilities of the Pokemon
    private Ability[] abilities;
    //Forms the Pokemon can take on
    private Form[] forms;
    //Game indicies relevant to the Pokemon by generation
    private GameIndex[] gameIndices;
    //Items held by the Pokemon when caught, if any
    private HeldItem[] heldItems;
    //Moves learned by the Pokemon
    private Move[] moves;
    //Name of the Pokemon
    private String name;
    //The species the Pokemon belongs to
    private Species species;
    //Collection of sprites
    private Sprites sprites;
    //Base stats of the Pokemon
    private Stats[] stats;
    //Types the Pokemon has
    private Type[] types;
    //The base experience gained from defeating the Pokemon
    private int baseExperience;
    //The height of the Pokemon in decimeters
    private int height;
    //National Pokedex number
    private int id;
    //Represents whether this is the default form of the Pokemon
    private boolean isDefault;
    //Order for sorting; almost national order, except families are grouped together
    private int order;
    //The weight of the Pokemon in hectograms
    private int weight;
    Pokemon(final int id) {
        Integer urlId = new Integer(id);
        String url = String.format("https://pokeapi.co/api/v2/%s", urlId.toString());
        createPokemonObject(url);
    }
    Pokemon(final String name) {
        String url = String.format("https://pokeapi.co/api/v2/%s", name);
        createPokemonObject(url);
    }
    private void createPokemonObject(final String urlBase) {

    }
    public Ability[] getAbilities() {return abilities;}
    public Form[] getForms() {return forms;}
    public GameIndex[] getGameIndices() {return gameIndices;}
    public HeldItem[] getHeldItems() {return heldItems;}
    public Move[] getMoves() {return moves;}
    public String getName() {return name;}
    public Species getSpecies() {return species;}
    public Sprites getSprites() {return sprites;}
    public Stats[] getStats() {return stats;}
    public Type[] getTypes() {return types;}
    public int getBaseExperience() {return baseExperience;}
    public int getHeight() {return height;}
    public int getId() {return id;}
    public boolean getIsDefault() {return isDefault;}
    public int getOrder() {return order;}
    public int getWeight() {return weight;}
}
