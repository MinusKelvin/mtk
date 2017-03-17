package minusk.mtk.animation;

import minusk.mtk.core.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MinusKelvin
 */
public abstract class Animation {
	private final List<StartListener> startListeners = new ArrayList<>();
	private final List<StopListener> stopListeners = new ArrayList<>();
	private final boolean shouldBeUpdated;
	private boolean playing, looping;
	
	/** @param shouldBeUpdated flag on whether <code>update()</code> should be called */
	public Animation(boolean shouldBeUpdated) {
		this.shouldBeUpdated = shouldBeUpdated;
	}
	
	/** Internal */
	public boolean _tick() {
		if (!playing)
			return true;
		update();
		return !playing;
	}
	
	/** Called every frame this animation is playing. */
	protected abstract void update();
	
	/** Called when this animation is stopped. */
	protected abstract void finish();
	
	public void start() {
		if (shouldBeUpdated)
			Application._addAnimation(this);
		stop();
		playing = true;
		begin();
		startListeners.forEach(StartListener::onStart);
	}
	
	/** Called when this animation is started. */
	protected abstract void begin();
	
	/** Stops the current play of this animation. Does not stop looping. */
	public void stop() {
		if (playing) {
			finish();
			stopListeners.forEach(StopListener::onStop);
			playing = false;
			if (looping)
				start();
		}
	}
	
	/** Cancels this animation. Stops it without calling <code>finish()</code> or any <code>StopListener</code>s */
	public void cancel() {
		playing = false;
	}
	
	public void addStartListener(StartListener listener) {
		startListeners.add(listener);
	}
	
	public void addStopListener(StopListener listener) {
		stopListeners.add(listener);
	}
	
	public void removeStartListener(StartListener listener) {
		startListeners.remove(listener);
	}
	
	public void removetopListener(StopListener listener) {
		stopListeners.remove(listener);
	}
	
	/** Plays this animation on a loop. */
	public void loop() {
		looping = true;
		start();
	}
	
	/** Stops looping this animation. The current play of the animation will continue. */
	public void stopLooping() {
		looping = false;
	}
	
	public interface StartListener {
		void onStart();
	}
	
	public interface StopListener {
		void onStop();
	}
}
