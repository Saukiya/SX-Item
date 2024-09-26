/*global SXItem, Bukkit, Arrays, Utils*/
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