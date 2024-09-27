/*global SXItem, Bukkit, Arrays, Utils*/
/** @link https://bukkit.windit.net/javadoc/ **/

globalValue = "globalField" // const 也可以
/**
 * @class java.util.ArrayList
 */
ArrayList = Java.type("java.util.ArrayList")

function globalFunc(arg) {
    let list = new ArrayList()
    list.add(arg);
    return list.get(0)
}

/**
 *
 * @param [Player]([Player](https://bukkit.windit.net/javadoc/org/bukkit/entity/Player.html))
 * @type {java(org.bukkit.entity.Player)}
 */
function send(player) {
}