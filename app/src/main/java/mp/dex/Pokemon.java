package mp.dex;


public class Pokemon {
    //The abilities of the Pokemon
    private Ability[] abilities;
    private Form[] forms;
    private GameIndex[] gameIndices;
    //Items held by the Pokemon when caught, if any
    private HeldItem[] heldItems;
    //Moves learned by the Pokemon
    private Move[] moves;
    //Name of the Pokemon
    private String name;
    private Species species;
    private Sprites sprites;
    private Stats[] stats;
    private Type[] types;
    private int baseExperience;
    private int height;
    private int id;
    private boolean isDefault;
    private int order;
    private int weight;
    Pokemon(final int id) {

    }
    Pokemon(final String name) {

    }
}
