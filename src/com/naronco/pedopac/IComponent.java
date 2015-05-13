package com.naronco.pedopac;

public interface IComponent {
	void preRender();
	void render();
	void preUpdate(float delta);
	void update(float delta);
}
