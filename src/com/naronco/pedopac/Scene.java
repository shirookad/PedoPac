package com.naronco.pedopac;

public abstract class Scene extends GameObject {
	protected Scene next = null;
	
	public Scene getNext() { return next; }
}
