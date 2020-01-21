package game.item;


public class Item implements Comparable<Item> {

    public final int id;
    public int position;
    public int quantity;
    public short flag;
    public long expiration = -1;
    public long inventoryItemId;
    public int uniqueId;
    public String owner = "";
    public int exp;

    public Item(final int id, final int position, final int quantity, final short flag, final int uniqueid) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
        this.uniqueId = uniqueid;
    }

    public Item(final int id, final int position, final int quantity, final short flag) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
        this.uniqueId = -1;
    }

    public Item(int id, int position, int quantity) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.uniqueId = -1;
    }

    public Item(int id) {
        this.id = id;
    }

    public Item copy() {
        final Item ret = new Item(id, position, quantity, flag, uniqueId);
        ret.owner = owner;
        ret.expiration = expiration;
        ret.exp = exp;
        return ret;
    }

    public Item copyWithQuantity(final short qq) {
        final Item ret = new Item(id, position, qq, flag, uniqueId);
        ret.owner = owner;
        ret.expiration = expiration;
        ret.exp = exp;
        return ret;
    }

    @Override
    public int compareTo(Item other) {
        if (Math.abs(position) < Math.abs(other.position)) {
            return -1;
        } else if (Math.abs(position) == Math.abs(other.position)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item)) {
            return false;
        }
        final Item ite = (Item) obj;
        return uniqueId == ite.uniqueId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.id;
        hash = 53 * hash + this.position;
        hash = 53 * hash + this.quantity;
        hash = 53 * hash + this.uniqueId;
        return hash;
    }

    @Override
    public String toString() {
        return "Item: " + id + " quantity: " + quantity;
    }
}

