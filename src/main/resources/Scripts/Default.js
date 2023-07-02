// 指令: /si script Default testFunction
function testFunction(args) {
    return args + " is " + (args != null);
}

// 指令: /si script Default testPlayer <玩家名> <参数>
function testPlayer(player, args) {
    if (player != null) {
        player.sendMessage("收到了一条信息: " + args);
    } else {
        SXItem.getInst().getLogger().info("可以将在线成员作为实际参数运行在/si script 方法内")
    }
}