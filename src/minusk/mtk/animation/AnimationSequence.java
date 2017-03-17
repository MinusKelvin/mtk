package minusk.mtk.animation;

/**
 * @author MinusKelvin
 */
public class AnimationSequence extends Animation {
	public final Animation[] animations;
	private int playing = -1;
	
	public AnimationSequence(Animation... animations) {
		super(false);
		this.animations = animations;
		for (int i = 0; i < animations.length; i++) {
			final int j = i;
			animations[i].addStopListener(() -> animationEnded(j));
		}
	}
	
	private void animationEnded(int number) {
		if (number == playing) {
			playing++;
			if (playing >= animations.length)
				stop();
			else
				animations[playing].start();
		}
	}
	
	@Override protected void update() {}
	@Override protected void finish() {}
	
	@Override
	protected void begin() {
		playing = 0;
		animations[playing].start();
	}
	
	@Override
	public void cancel() {
		super.cancel();
		if (playing != -1)
			animations[playing].cancel();
		playing = -1;
	}
}
