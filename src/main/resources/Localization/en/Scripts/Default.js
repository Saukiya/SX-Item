/*global SXItem, Bukkit, Arrays, Utils*/

/**
 * Command: /si script Default testPlayer <player> <args>
 * <p/>
 * If all parameters are online 'player name', it will be automatically converted to 'player object',
 * @param player playerName
 * @param args args
 * @see [Player](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/Player.html)
 */
function testPlayer(player, args) {
    if (player != null) {
        player.sendMessage("received a message: " + args);
    } else {
        SXItem.getInst().getLogger().info("Online members can be run as actual parameters inside the /si script method")
    }
}

/**
 * JS random format: <j:Default.itemScript#QAQ,QWQ>
 * @param space JS random current ExpressionSpace, specific reference [ExpressionSpace](https://github.com/Saukiya/SX-Item/blob/master/src/main/java/github/saukiya/sxitem/data/expression/ExpressionSpace.java)
 * @param args JS random string array such as ['QAQ', 'QWQ']
 * @returns string
 */
function itemScript(space, args) {
    space.getPlayer().sendMessage("Send parameters to players: " + args[0] + args[1]);
    return args[SXItem.getRandom().nextInt(args.length)];
}

// After the script is loaded, register the Bukkit event. See Event.js for details.
// registerNormalEvent("org.bukkit.event.player.PlayerItemHeldEvent", function (event) {
//     let player = event.getPlayer();
//     SXItem.getInst().getLogger().info("JS-" + event.getEventName() + ": " + player.getName())
// });

// Code Sample
//
// List methods can be implemented
// function newList1() {
//     return new Array("TEST4", "WER", "SDF", "SCV");
// }
//
// function newList2() {
//     return Arrays.asList("TEST3", "QAZ", "WSX", "EDC");
// }
//
// function newList3() {
//     return ["TEST6", "ERT", "DFG", "CVB"];
// }
//
// // The ArrayList is declared in the Global.js folder, but it is not recommend to use, to minimize the interaction with Java.
// function newList4() {
//     let list = new ArrayList();
//     list.add("TEST2");
//     list.add("ABC");
//     list.add("BCD");
//     list.add("CDE");
//     return list
// }