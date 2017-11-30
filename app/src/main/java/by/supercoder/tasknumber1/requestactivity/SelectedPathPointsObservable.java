package by.supercoder.tasknumber1.requestactivity;

/**
 * Created by user on 12.08.2017.
 */
public interface SelectedPathPointsObservable {
    void addObserver(SelectedPathPointsObserver selectedPathPointsObserver);

    void removeObserver(SelectedPathPointsObserver selectedPathPointsObserver);

    void removeObservers();

    void setChanged();

    void notifyObservers();
}
