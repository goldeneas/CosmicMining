package io.github.goldeneas.cosmicmining.enchants;

public abstract class CustomEnchant {
    private int id;
    private String enchantName;

    public CustomEnchant(String enchantName, int id) {
        this.id = id;
        this.enchantName = enchantName;
    }
}
