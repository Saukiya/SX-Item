// 指令: /si script Default testFunction
function testFunction(args) {
    return args + " is " + (args != null);
}

// 指令: /si script Default testPlayer <玩家名> <参数>
function testPlayer(player, args) {
    if (player != null) {
        player.sendMessage("received a message: " + args);
    } else {
        SXItem.getInst().getLogger().info("Online members can be run as actual parameters inside the /si script method")
    }
}

// item random <j:Default.itemScript#QAQ,QWQ> is in this format
// docker is a fixed parameter. For more information, see RandomDocker.java
// args is an array of input strings. The online "player name" is automatically converted to "player object".
// The automatic conversion function will consider whether to cut or keep it later.
function itemScript(docker, args) {
    docker.getPlayer().sendMessage("Send parameters to players: " + args[0] + args[1]);
    return args[SXItem.getRandom().nextInt(args.length)];
}