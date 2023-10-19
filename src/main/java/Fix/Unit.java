package Fix;

import javax.swing.*;

public class Unit {
    int maxHp, hp, atk, mov, range, type;
    String name;
    ImageIcon sprite;
    Boolean canMove, canAttack;

    public Unit(String name, ImageIcon sprite, int maxHp, int atk, int mov, int range, int type){
        this.name = name;
        this.sprite = sprite;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.atk = atk;
        this.mov = mov;
        this.range = range;
        this.type = type;
        this.canMove = true;
        this.canAttack = true;
    }

    public Unit(Unit unit){
        this.name = unit.name;
        this.sprite = unit.sprite;
        this.maxHp = unit.maxHp;
        this.hp = unit.maxHp;
        this.atk = unit.atk;
        this.mov = unit.mov;
        this.range = unit.range;
        this.type = unit.type;
        this.canMove = true;
        this.canAttack = true;
    }

    public void attack(Unit target){
        target.hp -= this.atk;
        if(target.hp <= 0){
            target.destroy();
        }
        this.canAttack = false;
        this.canMove = false;
    }

    public void destroy(){
        //Remove from list and map;
    }
}
