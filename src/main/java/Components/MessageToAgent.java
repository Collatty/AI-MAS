package Components;

enum MessageType {
    HEURISTIC,
    PLAN,
    CONFLICT
}

public class MessageToAgent {
    Boolean toAll;
    Color toColor;
    Integer toAgent;
    Task task;
    MessageType messageType;

    public MessageToAgent(Boolean toAll, Color toColor, Integer toAgent, MessageType messageType, Task task){
        this.toAgent = toAgent;
        this.toAll = toAll;
        this.toColor = toColor;
        this.task = task;
        this.messageType = messageType;
    }

}
