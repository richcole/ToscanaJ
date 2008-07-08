/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

public class AnimationTimeController {
    private long startTimeStamp;
    private double endTime;
    private double fadeInTime;
    private double visibleTime;
    private double fadeOutTime;
    private long millisecondsPerStep;
    private double currentTime;

    public AnimationTimeController(final double endTime,
            final double fadeInTime, final double visibleTime,
            final double fadeOutTime, final long millisecondsPerStep) {
        this.startTimeStamp = System.currentTimeMillis();
        this.endTime = endTime;
        this.fadeInTime = fadeInTime;
        this.visibleTime = visibleTime;
        this.fadeOutTime = fadeOutTime;
        this.millisecondsPerStep = millisecondsPerStep;
        this.currentTime = 0;
    }

    public double getCurrentTime() {
        return this.currentTime;
    }

    public void calculateCurrentTime() {
        this.currentTime = (System.currentTimeMillis() - this.startTimeStamp)
                / (double) this.millisecondsPerStep;
    }

    public void setCurrentTime(final double currentTime) {
        this.currentTime = currentTime;
    }

    public double getEndTime() {
        return this.endTime;
    }

    public double getAllFadedTime() {
        return this.endTime + this.visibleTime + this.fadeOutTime;
    }

    public double getFadeInTime() {
        return this.fadeInTime;
    }

    public double getFadeOutTime() {
        return this.fadeOutTime;
    }

    public long getMillisecondsPerStep() {
        return this.millisecondsPerStep;
    }

    public double getVisibleTime() {
        return this.visibleTime;
    }

    public void setEndTime(final double endTime) {
        this.endTime = endTime;
    }

    public void setFadeInTime(final double fadeInTime) {
        this.fadeInTime = fadeInTime;
    }

    public void setFadeOutTime(final double fadeOutTime) {
        this.fadeOutTime = fadeOutTime;
    }

    public void reset() {
        this.startTimeStamp = System.currentTimeMillis();
        this.currentTime = 0;
    }

    public void setMillisecondsPerStep(final long millisecondsPerStep) {
        this.millisecondsPerStep = millisecondsPerStep;
    }

    public void setVisibleTime(final double visibleTime) {
        this.visibleTime = visibleTime;
    }

}
