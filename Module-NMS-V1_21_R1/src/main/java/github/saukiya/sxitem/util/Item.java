package github.saukiya.sxitem.util;

import lombok.*;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Content;

import java.util.Map;

@ToString
@Getter
@AllArgsConstructor
class Item extends Content {

    public String id;
    public int count;
    private Map<String, Object> components;

    @Override
    public HoverEvent.Action requiredAction() {
        return HoverEvent.Action.SHOW_ITEM;
    }
}

