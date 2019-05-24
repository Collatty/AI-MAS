package components;

public class AbortTaskMessage extends Message {
    private Task task;
    private Agent agent;

    public AbortTaskMessage(Task task, Agent agent) {
	this.task = task;
	this.agent = agent;
    }

    public Agent getAgent() {
	return agent;
    }

    public Task getTask() {
	return task;
    }
}
