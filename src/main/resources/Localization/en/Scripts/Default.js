/*global SXItem, Bukkit, Arrays, Utils*/
/**
 * /si script Default testFunction
 * @param args
 * @returns string
 */
function testFunction(args) {
    return args + " is " + (args != null);
}

/**
 * /si script Default testPlayer <player> <args>
 * @param player playerName
 * @param args args
 */
function testPlayer(player, args) {
    if (player != null) {
        player.sendMessage("received a message: " + args);
    } else {
        SXItem.getInst().getLogger().info("Online members can be run as actual parameters inside the /si script method")
    }
}

/**
 * item random <j:Default.itemScript#QAQ,QWQ> is in this format
 * @param docker fixed parameter. For more information, see RandomDocker.java
 * @param args array of input strings. The online "playerName" is automatically converted to "Player".
 * @returns string
 */
function itemScript(docker, args) {
    docker.getPlayer().sendMessage("Send parameters to players: " + args[0] + args[1]);
    return args[SXItem.getRandom().nextInt(args.length)];
}