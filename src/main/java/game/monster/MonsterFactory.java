package game.monster;

public class MonsterFactory {
    public static Monster getMonster(int mid) {
        return new Monster(mid);
    }
}
