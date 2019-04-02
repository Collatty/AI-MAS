package Components;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;



public abstract class BlackBoard extends Observable {

    private static List listOfThings = new ArrayList();
    private static List<Observer> observers = new ArrayList<Observer>();

    public BlackBoard(){

    }

    public static void addElementToList(Object object) {
        listOfThings.add(object);

    }

    /*public void fireToSubscriber() {
        for (Observer observer : observers) {
            observer.update(this, listOfThings);
        }
    }*/

    /*public void addListener(Observer observer) {
        this.addObserver(observer);
    }*/ //REDUNDANT



}
