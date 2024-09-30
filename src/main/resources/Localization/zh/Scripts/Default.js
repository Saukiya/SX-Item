/*global SXItem, Bukkit, Arrays, Utils*/

/**
 * 指令: /si script Default testPlayer <玩家名> <参数>
 * <p/>
 * 所有参数里为在线的'玩家名', 则自动转成'玩家对象'
 * @param player 玩家名
 * @param args 参数
 * @see [Player](https://bukkit.windit.net/javadoc/org/bukkit/entity/Player.html)
 */
function testPlayer(player, args) {
    if (player != null) {
        player.sendMessage("收到了一条信息: " + args)
    } else {
        SXItem.getInst().getLogger().info("可以将在线成员作为实际参数运行在/si script 方法内")
    }
}

/**
 * JS随机格式: <j:Default.itemScript#QAQ,QWQ>
 * @param docker JS随机当前的RandomDocker，具体参考 [RandomDocker](https://github.com/Saukiya/SX-Item/blob/master/src/main/java/github/saukiya/sxitem/data/random/RandomDocker.java)
 * @param args JS随机所带的字符串数组 例如['QAQ', 'QWQ']
 * @returns string
 */
function itemScript(docker, args) {
    if (docker.getPlayer() != null) {
        docker.getPlayer().sendMessage("把参数发给玩家: " + args[0] + args[1])
    }
    return args[SXItem.getRandom().nextInt(args.length)];
}

// 脚本加载后 注册Bukkit事件 具体查阅Event.js
// registerNormalEvent("org.bukkit.event.player.PlayerItemHeldEvent", function (event) {
//     let player = event.getPlayer();
//     SXItem.getInst().getLogger().info("JS-" + event.getEventName() + ": " + player.getName())
// });

// 代码示例
//
// 可实现列表方法
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
// // ArrayList 在 Global.js文件夹中声明, 但是不推荐使用, 尽可能调用js自有函数, 尽量减少与java之间的交互.
// function newList4() {
//     let list = new ArrayList();
//     list.add("TEST2");
//     list.add("ABC");
//     list.add("BCD");
//     list.add("CDE");
//     return list
// }