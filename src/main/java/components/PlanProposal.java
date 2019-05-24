package components;

import java.util.List;

public class PlanProposal extends Message {
    private final Task task;
    private Agent agent;
    private List<Action> actions;
    private int agentStartRow;
    private int agentStartCol;

    public PlanProposal(List<Action> actions, Agent agent, Task task) {
	this.actions = actions;
	this.agent = agent;
	this.task = task;
	this.agentStartRow = agent.getRow();
	this.agentStartCol = agent.getCol();
    }

    public String toString() {
	return "Agent " + agent.getAgentNumber() + " has plan for task  " + this.task.getId() + " and the actions: "
		+ this.actions.toString();
    }

    public List<Action> getActions() {
	return actions;
    }

    public Task getTask() {
	return task;
    }

    public Agent getAgent() {
	return agent;
    }

    public int getAgentStartRow() {
	return agentStartRow;
    }

    public int getAgentStartCol() {
	return agentStartCol;
    }
}
