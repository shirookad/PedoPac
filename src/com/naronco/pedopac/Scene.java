package com.naronco.pedopac;

public abstract class Scene extends GameObject {
	protected Scene next = null;
	
	abstract void debug();
	
	public Scene getNext() { return next; }
}
