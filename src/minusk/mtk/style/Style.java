package minusk.mtk.style;

import minusk.mtk.scene.Node;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author MinusKelvin
 */
public abstract class Style<T extends Node> {
	private final ArrayList<WeakReference<T>> nodes = new ArrayList<>();
	
	protected final void requestReflowCB() {
		for (Iterator<WeakReference<T>> iter = nodes.iterator(); iter.hasNext();) {
			T node = iter.next().get();
			if (node == null)
				iter.remove();
			else
				node.requestReflow();
		}
	}
	
	protected final void requestRenderCB() {
		for (Iterator<WeakReference<T>> iter = nodes.iterator(); iter.hasNext();) {
			T node = iter.next().get();
			if (node == null)
				iter.remove();
			else
				node.requestRender();
		}
	}
	
	public void apply(T node) {
		nodes.add(new WeakReference<>(node));
	}
	
	public void unapply(T node) {
		nodes.removeIf(e -> e.get() == node || e.get() == null);
	}
}
