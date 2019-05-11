package Components;


import java.util.List;

public class PlanProposal extends Message {
    private final Task task;
    private Agent agent;
    private List<Action> actions;

    public PlanProposal(List<Action> actions, Agent agent, Task task) {
	this.actions = actions;
	this.agent = agent;
	this.task = task;
    }

    public String toString() {
	return "Agent " + agent.getAgentNumber() + " has plan for task  " + this.task.getId() + " and the actions: " + this.actions.toString();
    }

    public List<Action> getActions() {
        return actions;
    }

    public Agent getAgent() {
        return agent;
    }



    public Task getTask() {
        return task;
    }
}
