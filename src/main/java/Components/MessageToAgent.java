package Components;

enum MessageType {
    HEURISTIC,
    PLAN,
    CONFLICT
}

//TODO: Consider if attributes should be private
public class MessageToAgent {
    private Boolean toAll;
    private Color toColor;
    private Integer toAgent;
    private MessageType messageType;
    private Task task;

    public MessageToAgent(Boolean toAll, Color toColor, Integer toAgent, MessageType messageType, Task task) {
        this.toAgent = toAgent;
        this.toAll = toAll;
        this.toColor = toColor;
        this.messageType = messageType;
        this.task = task;
    }
}
