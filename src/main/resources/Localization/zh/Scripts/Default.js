/*global SXItem, Bukkit, Arrays, Utils*/
/**
 * /si script Default testFunction
 * @param args 参数
 * @returns string
 */
function testFunction(args) {
    return args + " is " + (args != null)
}

/**
 * 指令: /si script Default testPlayer <玩家名> <参数>
 * @param player 玩家名
 * @param args 参数
 */
function testPlayer(player, args) {
    if (player != null) {
        player.sendMessage("收到了一条信息: " + args)
    } else {
        SXItem.getInst().getLogger().info("可以将在线成员作为实际参数运行在/si script 方法内")
    }
}

/**
 * 物品内随机<j:Default.itemScript#QAQ,QWQ>是这个格式
 * @param docker 固定参数，具体参考RandomDocker.java
 * @param args 输入字符串数组, 在线的'玩家名'自动转成'玩家对象', 自动转化功能后续考虑是否砍掉或保留
 * @returns string
 */
function itemScript(docker, args) {
    if (docker.getPlayer() != null) {
        docker.getPlayer().sendMessage("把参数发给玩家: " + args[0] + args[1])
    }
    return args[SXItem.getRandom().nextInt(args.length)];
}