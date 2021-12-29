package net.tharow.tantalum.ui.controls;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SimpleMouseListener implements MouseListener {
    private final Runnable mouseClicked;
    private final Runnable mousePressed;
    private final Runnable mouseReleased;
    private final Runnable mouseEntered;
    private final Runnable mouseExited;


    /**
     * Creates The Most Basic on Click Mouse Listener
     * @param mouseClicked Invoked when the mouse button has been clicked
     *                     (pressed and released) on a component
     */
    public SimpleMouseListener(Runnable mouseClicked){
        this(mouseClicked, false);
    }

    /**
     * Creates The Most Basic Mouse Listener
     * @param mouseClicked Invoked when the mouse button has been clicked
     *                     (pressed and released) on a component
     * @param forAllEvents Should @param mouseClicked be used for all events
     *                     default of false
     */
    public SimpleMouseListener(Runnable mouseClicked, boolean forAllEvents){
        if(forAllEvents){
            this.mouseClicked = mouseClicked;
            this.mousePressed = mouseClicked;
            this.mouseReleased = mouseClicked;
            this.mouseEntered = mouseClicked;
            this.mouseExited = mouseClicked;
        } else{
            this.mouseClicked = mouseClicked;
            this.mousePressed = null;
            this.mouseReleased = null;
            this.mouseEntered = null;
            this.mouseExited = null;
        }
    }

    /**
     * Creates a new mouse listener with the selected runnable
     * Setting Any of the values to null will result in it not being run.
     * @param mouseClicked Invoked when the mouse button has been clicked (pressed and released) on a component.
     * @param mousePressed Invoked when a mouse button has been pressed on a component.
     * @param mouseReleased Invoked when a mouse button has been released on a component.
     * @param mouseEntered Invoked when the mouse enters a component.
     * @param mouseExited Invoked when the mouse exits a component.
     */
    public SimpleMouseListener(Runnable mouseClicked, Runnable mousePressed, Runnable mouseReleased, Runnable mouseEntered, Runnable mouseExited){
        this.mouseClicked = mouseClicked;
        this.mousePressed = mousePressed;
        this.mouseReleased = mouseReleased;
        this.mouseEntered = mouseEntered;
        this.mouseExited = mouseExited;
    }

    private void toRun(Runnable runnable){
        if (runnable != null) {
            runnable.run();
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }
    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
