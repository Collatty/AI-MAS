package Components;

import Components.State.Block;

enum MessageType {
    HEURISTIC,
    PLAN,
    CONFLICT
}

//TODO: Consider if attributes should be private
public class MessageToAgent {
    public Block getBlock() {
        return block;
    }

    private final Block block;
    private Boolean toAll;
    private Color toColor;
    private Integer toAgent;
    private MessageType messageType;

    public Boolean getToAll() {
        return toAll;
    }

    public Color getToColor() {
        return toColor;
    }

    public Integer getToAgent() {
        return toAgent;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Task getTask() {
        return task;
    }

    private Task task;

    public MessageToAgent(Boolean toAll, Color toColor, Integer toAgent, MessageType messageType, Task task,
                          Block block) {
        this.toAgent = toAgent;
        this.toAll = toAll;
        this.toColor = toColor;
        this.messageType = messageType;
        this.task = task;
        this.block = block;
    }
}
