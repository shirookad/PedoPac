package com.naronco.pedopac;

import java.util.*;

public abstract class GameObject {
	protected List<GameObject> gameObjects = new ArrayList<GameObject>();
	protected List<IComponent> components = new ArrayList<IComponent>();

	public void addGameObject(GameObject obj) {
		if (obj == this)
			return;
		gameObjects.add(obj);
	}

	public void addComponent(IComponent component) {
		components.add(component);
	}

	protected abstract void update(float delta);

	protected abstract void render();

	public void performUpdate(float delta) {
		for (IComponent component : components)
			component.preUpdate(delta);
		update(delta);
		for (GameObject object : gameObjects)
			object.performUpdate(delta);
		for (IComponent component : components)
			component.update(delta);
	}

	public void performRender() {
		for (IComponent component : components)
			component.preRender();
		render();
		for (GameObject object : gameObjects)
			object.performRender();
		for (IComponent component : components)
			component.render();
	}
}
