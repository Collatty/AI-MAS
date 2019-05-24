package components;

enum MessageType {
    HEURISTIC, PLAN, CONFLICT
}

public class MessageToAgent {
    private Boolean toAll;
    private Color toColor;
    private Integer toAgent;
    private MessageType messageType;
    private Task task;

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

    public MessageToAgent(Boolean toAll, Color toColor, Integer toAgent, MessageType messageType, Task task) {
	this.toAgent = toAgent;
	this.toAll = toAll;
	this.toColor = toColor;
	this.messageType = messageType;
	this.task = task;
    }
}
