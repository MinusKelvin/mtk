package minusk.mtk.animation;

import minusk.mtk.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MinusKelvin
 */
public abstract class Animation {
	private List<StartListener> startListeners = new ArrayList<>();
	private List<StopListener> stopListeners = new ArrayList<>();
	private boolean playing;
	
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
		Application._addAnimation(this);
		stop();
		playing = true;
		begin();
		startListeners.forEach(StartListener::onStart);
	}
	
	/** Called when this animation is started. */
	protected abstract void begin();
	
	public void stop() {
		if (playing) {
			finish();
			stopListeners.forEach(StopListener::onStop);
		}
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
	
	public interface StartListener {
		void onStart();
	}
	
	public interface StopListener {
		void onStop();
	}
}
