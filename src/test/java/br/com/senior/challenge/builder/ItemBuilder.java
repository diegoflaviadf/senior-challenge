package br.com.senior.challenge.builder;

import br.com.senior.challenge.entities.Item;
import br.com.senior.challenge.entities.enums.ItemType;

public class ItemBuilder {

    private final Item item;

    private ItemBuilder(Item item){
        this.item = item;
    }

    public static ItemBuilder newItem(){
        return new ItemBuilder(new Item());
    }

    public ItemBuilder withName(String value){
        item.setName(value);
        return this;
    }

    public ItemBuilder withType(ItemType value){
        item.setType(value);
        return this;
    }

    public ItemBuilder withDescription(String value){
        item.setDescription(value);
        return this;
    }

    public ItemBuilder withActive(Boolean value){
        item.setActive(value);
        return this;
    }

    public Item build(){
        return item;
    }

}
