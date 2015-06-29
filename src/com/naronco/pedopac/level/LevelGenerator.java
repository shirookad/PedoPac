package com.naronco.pedopac.level;

import java.util.*;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

import com.naronco.pedopac.*;
import com.naronco.pedopac.physics.*;
import com.naronco.pedopac.rendering.*;

public class LevelGenerator {
	List<ControlDot> dots;
	Random random;
	float maxChange;

	public LevelGenerator(Random random) {
		this.random = random;
		dots = new ArrayList<ControlDot>();

		dots.add(new ControlDot(-10, 0));
		dots.add(new ControlDot(0, -10));
		dots.add(new ControlDot(10, 0));
		dots.add(new ControlDot(0, 10));

		maxChange = 1.0f;
	}

	public void generate(int subdivisions) {
		dots = new ArrayList<ControlDot>();

		dots.add(new ControlDot(-10, 0));
		dots.add(new ControlDot(0, -10));
		dots.add(new ControlDot(10, 0));
		dots.add(new ControlDot(0, 10));

		maxChange = 1.0f;

		for (int i = 0; i < subdivisions; i++) {
			step();
		}

		for (int i = 0; i < 20; i++)
			simplify();
		subdivide();
	}

	public void step() {
		mutate(maxChange);
		subdivide();
		maxChange *= 0.75f;
	}

	public List<ControlDot> getDots() {
		return dots;
	}

	public ControlDot get(int i) {
		while (i < 0)
			i += dots.size();
		i = i % dots.size();
		return dots.get(i);
	}

	public ControlDot getAt(float t) {
		if (t < 0)
			t = 0;
		if (t > 1)
			t = 1;
		t *= dots.size();
		ControlDot start = get((int) t);
		ControlDot end = get((int) t + 1);
		float step = t - (int) t;
		return start.copy().lerp(end, step);
	}

	public Mesh generateMesh() {
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Integer> indices = new ArrayList<Integer>();

		final float TRACK_WIDTH = 50.0f;
		final float TRACK_SCALE = 70.0f;

		for (int i = 0; i < dots.size() - 1; i++) {
			ControlDot normal1 = get(i).copy().sub(get(i + 1)).getNormal();
			ControlDot normal2 = get(i + 1).copy().sub(get(i + 2)).getNormal();
			ControlDot normal = normal1.copy().add(normal2).normalize();
			ControlDot A = get(i).copy().mul(TRACK_SCALE)
					.add(normal.copy().mul(TRACK_WIDTH));
			ControlDot B = get(i).copy().mul(TRACK_SCALE);
			vertices.add(new Vertex(A.x, 0, A.y));
			vertices.add(new Vertex(B.x, 0, B.y));

			indices.add(i * 2 + 0);
			indices.add(i * 2 + 3);
			indices.add(i * 2 + 1);

			indices.add(i * 2 + 0);
			indices.add(i * 2 + 2);
			indices.add(i * 2 + 3);
		}

		ControlDot normal1 = get(-1).copy().sub(get(0)).getNormal();
		ControlDot normal2 = get(0).copy().sub(get(1)).getNormal();
		ControlDot normal = normal1.copy().add(normal2).normalize();
		ControlDot A = get(-1).copy().mul(TRACK_SCALE)
				.add(normal.copy().mul(TRACK_WIDTH));
		ControlDot B = get(-1).copy().mul(TRACK_SCALE);
		vertices.add(new Vertex(A.x, 0, A.y));
		vertices.add(new Vertex(B.x, 0, B.y));

		indices.add(dots.size() * 2 - 2);
		indices.add(1);
		indices.add(dots.size() * 2 - 1);

		indices.add(dots.size() * 2 - 2);
		indices.add(0);
		indices.add(1);

		Vertex[] vert = new Vertex[vertices.size()];
		int i = 0;
		for (Vertex v : vertices)
			vert[i++] = v;

		return new Mesh(vert, Util.toIntArray(indices));
	}

	public void makePhysics(PhysicsWorld world) {
		BodyDef bodyDef = PhysicsWorld.createRigidBodyDefinition(0);
		Body body = world.getDynamicsWorld().createBody(bodyDef);

		final float TRACK_WIDTH = 50.0f;
		final float TRACK_SCALE = 70.0f;

		for (int i = 0; i < dots.size() - 1; i++) {
			ControlDot normal1 = get(i).copy().sub(get(i + 1)).getNormal();
			ControlDot normal2 = get(i + 1).copy().sub(get(i + 2)).getNormal();
			ControlDot normal = normal1.copy().add(normal2).normalize();
			ControlDot A = get(i).copy().mul(TRACK_SCALE)
					.add(normal.copy().mul(TRACK_WIDTH));
			ControlDot B = get(i + 1).copy().mul(TRACK_SCALE)
					.add(normal.copy().mul(TRACK_WIDTH));
			ControlDot C = get(i).copy().mul(TRACK_SCALE)
					.add(normal.copy().mul(TRACK_WIDTH + 1));
			ControlDot D = get(i + 1).copy().mul(TRACK_SCALE)
					.add(normal.copy().mul(TRACK_WIDTH + 1));
			
			PolygonShape poly = new PolygonShape();
			poly.set(new Vec2[] { A.v(), B.v(), D.v(), C.v() }, 4);
			
			FixtureDef fixDef = new FixtureDef();
			fixDef.restitution = 0.01f;
			fixDef.shape = poly;
			fixDef.density = 1000;
			
			body.createFixture(fixDef);
		}
	}

	public void simplify() {
		simplify(1.0f);
	}

	public void simplify(float pointDist) {
		List<ControlDot> simplified = new ArrayList<ControlDot>();
		ControlDot last = get(0);
		simplified.add(last);

		for (float t = 0; t < 1; t += 0.0001f) {
			if (last.distance(getAt(t)) >= pointDist) {
				last = getAt(t);
				simplified.add(last);
			}
		}

		dots = simplified;
	}

	private void mutate(float maxChange) {
		for (int i = 0; i < dots.size() - 1; i++) {
			get(i).mul(random.nextFloat() * maxChange + 1);
		}
	}

	public void subdivide() {
		List<ControlDot> divided = new ArrayList<ControlDot>();

		for (int i = 0; i < dots.size() - 1; i++) {
			divided.add(get(i));
			divided.add(get(i).copy().add(get(i + 1)).mul(0.5f));
		}
		dots = divided;

		for (int i = 0; i < dots.size() - 1; i += 2) {
			smooth(get(i - 1), get(i + 1), get(i));
		}
	}

	private void smooth(ControlDot sub1, ControlDot sub2, ControlDot edge) {
		edge.set(sub1.copy().add(sub2).add(edge).mul(0.333333333333333333333f));
	}
}