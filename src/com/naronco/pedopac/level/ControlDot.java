package com.naronco.pedopac.level;

import org.jbox2d.common.*;

public class ControlDot {
	public float x = 0, y = 0;

	public ControlDot() {
	}

	public ControlDot(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2 v() {
		return new Vec2(x, y);
	}

	public ControlDot add(ControlDot b) {
		x += b.x;
		y += b.y;
		return this;
	}

	public ControlDot sub(ControlDot b) {
		x -= b.x;
		y -= b.y;
		return this;
	}

	public ControlDot mul(float b) {
		x *= b;
		y *= b;
		return this;
	}

	public ControlDot set(ControlDot b) {
		x = b.x;
		y = b.y;
		return this;
	}

	public ControlDot lerp(ControlDot b, float t) {
		x = b.x * t + x * (1 - t);
		y = b.y * t + y * (1 - t);
		return this;
	}

	public ControlDot normalize() {
		float il = 1.0f / length();
		x *= il;
		y *= il;
		return this;
	}

	public ControlDot getNormal() {
		float temp = x;
		x = -y;
		y = temp;
		return normalize();
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public float distance(ControlDot b) {
		return copy().sub(b).length();
	}

	public ControlDot copy() {
		return new ControlDot(x, y);
	}
}