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
	
	public AnimationTimeController(double endTime, double fadeInTime, double visibleTime, double fadeOutTime, long millisecondsPerStep) {
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
        this.currentTime = (System.currentTimeMillis() - this.startTimeStamp) / (double)this.millisecondsPerStep;
    }

	public void setCurrentTime(double currentTime) {
		this.currentTime = currentTime;
	}

    public double getEndTime() {
        return endTime;
    }
    
    public double getAllFadedTime() {
    	return endTime + visibleTime + fadeOutTime;
    }

    public double getFadeInTime() {
        return fadeInTime;
    }

    public double getFadeOutTime() {
        return fadeOutTime;
    }

    public long getMillisecondsPerStep() {
        return millisecondsPerStep;
    }

    public double getVisibleTime() {
        return visibleTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public void setFadeInTime(double fadeInTime) {
        this.fadeInTime = fadeInTime;
    }

    public void setFadeOutTime(double fadeOutTime) {
        this.fadeOutTime = fadeOutTime;
    }

    public void reset() {
    	this.startTimeStamp = System.currentTimeMillis();
    }
    
    public void setMillisecondsPerStep(long millisecondsPerStep) {
        this.millisecondsPerStep = millisecondsPerStep;
    }

    public void setVisibleTime(double visibleTime) {
        this.visibleTime = visibleTime;
    }

}
