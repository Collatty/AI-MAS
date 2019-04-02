package Components;

import java.util.Observable;
import java.util.Observer;

public class Agent implements Observer {

    public final String name;

    public Agent (String name){
        this.name = name;

    }

    @Override
    public void update(Observable o, Object arg) {

    }


    //TODO implement agents
}
