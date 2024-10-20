/*global SXItem, Bukkit, Arrays, Utils*/

/**
 * Befehl: /si script Standard testPlayer <Spielername> <Parameter>
 * <p/>
 * Wenn alle Parameter ein online 'Spielername' sind, wird dieser automatisch in ein 'Spielerobjekt' umgewandelt.
 * @param player Spielername
 * @param args Parameter
 * @see [Player](https://bukkit.windit.net/javadoc/org/bukkit/entity/Player.html)
 */
function testPlayer(player, args) {
    if (player != null) {
        player.sendMessage("Eine Nachricht erhalten: " + args)
    } else {
        SXItem.getInst().getLogger().info("Online-Mitglieder können als tatsächliche Parameter innerhalb der /si script-Methode ausgeführt werden.")
    }
}

/**
 * JS-Zufallsformat:<j:Standard.itemScript#QAQ,QWQ>
 * @param space JS-Zufall für das aktuelle ExpressionSpace, siehe [ExpressionSpace](https://github.com/Saukiya/SX-Item/blob/master/src/main/java/github/saukiya/sxitem/data/expression/ExpressionSpace.java)
 * @param args JS-Zufalls-String-Array, zum Beispiel ['QAQ', 'QWQ'].
 * @returns string
 */
function itemScript(space, args) {
    if (space.getPlayer() != null) {
        space.getPlayer().sendMessage("Parameter an den Spieler senden: " + args[0] + args[1])
    }
    return args[SXItem.getRandom().nextInt(args.length)];
}

// Nach dem Laden des Skripts werden Bukkit-Events registriert. Einzelheiten findest du in Event.js.
// registerNormalEvent("org.bukkit.event.player.PlayerItemHeldEvent", function (event) {
//     let player = event.getPlayer();
//     SXItem.getInst().getLogger().info("JS-" + event.getEventName() + ": " + player.getName())
// });

// Codebeispiel
//
// Implementierbare Listenmethoden
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
// // ArrayList wird im Global.js-Ordner deklariert, jedoch wird die Verwendung nicht empfohlen. 
// // Es ist besser, die eigenen Funktionen von JS zu verwenden und die Interaktion mit Java so weit wie möglich zu reduzieren.
// function newList4() {
//     let list = new ArrayList();
//     list.add("TEST2");
//     list.add("ABC");
//     list.add("BCD");
//     list.add("CDE");
//     return list
// }