/*global Bukkit, Arrays, Utils, SXItem, listener*/
let EventPriority = Java.type("org.bukkit.event.EventPriority");
let EventExecutor = Java.type("org.bukkit.plugin.EventExecutor");
let plugin = SXItem.getInst();

let createExecutor = function (eventFunction) {
    let Executor = Java.extend(EventExecutor, {
        execute: function (listener, event) {
            eventFunction(event);
        }
    });
    return new Executor();
}

/**
 * Register for the specified Bukkit event
 *
 * @example Code example-Place the code outside the function and can be called directly after loading/reloading
 * registerNormalEvent("org.bukkit.event.player.PlayerItemHeldEvent", function (event) {
 *     let player = event.getPlayer();
 *     SXItem.getInst().getLogger().info("JS-" + event.getEventName() + ": " + player.getName())
 *     // implementation function
 * });
 * @param eventName Full name of the event class
 * @param eventFunction Event function(event)
 * @see [Bukkit API](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/package-summary.html)
 */
registerNormalEvent = function (eventName, eventFunction) {
    if (typeof eventFunction !== 'function') {
        throw new TypeError('eventFunction must be a function');
    }
    let eventClass = Java.type(eventName).class;
    let priority = EventPriority.NORMAL;
    let executor = createExecutor(eventFunction);
    plugin.getLogger().info("JS-RegisterEvent: " + eventName);
    Bukkit.getPluginManager().registerEvent(eventClass, listener, priority, executor, plugin);
}