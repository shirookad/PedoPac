// automatically generated, do not modify

package com.naronco.pedopac.schemas;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

public class VehicleInfo extends Table {
  public static VehicleInfo getRootAsVehicleInfo(ByteBuffer _bb) { return getRootAsVehicleInfo(_bb, new VehicleInfo()); }
  public static VehicleInfo getRootAsVehicleInfo(ByteBuffer _bb, VehicleInfo obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public VehicleInfo __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public VehicleWheelInfo wheels(int j) { return wheels(new VehicleWheelInfo(), j); }
  public VehicleWheelInfo wheels(VehicleWheelInfo obj, int j) { int o = __offset(4); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int wheelsLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public double engineForce() { int o = __offset(6); return o != 0 ? bb.getDouble(o + bb_pos) : 1000; }
  public double breakForce() { int o = __offset(8); return o != 0 ? bb.getDouble(o + bb_pos) : 44; }
  public double steeringIncrement() { int o = __offset(10); return o != 0 ? bb.getDouble(o + bb_pos) : 0.04; }
  public double steeringClamp() { int o = __offset(12); return o != 0 ? bb.getDouble(o + bb_pos) : 0.25; }
  public double frictionSlip() { int o = __offset(14); return o != 0 ? bb.getDouble(o + bb_pos) : 40.5; }
  public double maxSuspensionTravelCm() { int o = __offset(16); return o != 0 ? bb.getDouble(o + bb_pos) : 500; }
  public double suspensionStiffness() { int o = __offset(18); return o != 0 ? bb.getDouble(o + bb_pos) : 35.88; }
  public double suspensionDamping() { int o = __offset(20); return o != 0 ? bb.getDouble(o + bb_pos) : 0.88; }
  public double suspensionCompression() { int o = __offset(22); return o != 0 ? bb.getDouble(o + bb_pos) : 0.83; }
  public double rollInfluence() { int o = __offset(24); return o != 0 ? bb.getDouble(o + bb_pos) : 0.1; }
  public double suspensionRestLength() { int o = __offset(26); return o != 0 ? bb.getDouble(o + bb_pos) : 0.18; }
  public float width() { int o = __offset(28); return o != 0 ? bb.getFloat(o + bb_pos) : 0; }
  public float height() { int o = __offset(30); return o != 0 ? bb.getFloat(o + bb_pos) : 0; }
  public float depth() { int o = __offset(32); return o != 0 ? bb.getFloat(o + bb_pos) : 0; }
  public float mass() { int o = __offset(34); return o != 0 ? bb.getFloat(o + bb_pos) : 0; }
  public float offsetHeight() { int o = __offset(36); return o != 0 ? bb.getFloat(o + bb_pos) : 0; }

  public static int createVehicleInfo(FlatBufferBuilder builder,
      int wheels,
      double engineForce,
      double breakForce,
      double steeringIncrement,
      double steeringClamp,
      double frictionSlip,
      double maxSuspensionTravelCm,
      double suspensionStiffness,
      double suspensionDamping,
      double suspensionCompression,
      double rollInfluence,
      double suspensionRestLength,
      float width,
      float height,
      float depth,
      float mass,
      float offsetHeight) {
    builder.startObject(17);
    VehicleInfo.addSuspensionRestLength(builder, suspensionRestLength);
    VehicleInfo.addRollInfluence(builder, rollInfluence);
    VehicleInfo.addSuspensionCompression(builder, suspensionCompression);
    VehicleInfo.addSuspensionDamping(builder, suspensionDamping);
    VehicleInfo.addSuspensionStiffness(builder, suspensionStiffness);
    VehicleInfo.addMaxSuspensionTravelCm(builder, maxSuspensionTravelCm);
    VehicleInfo.addFrictionSlip(builder, frictionSlip);
    VehicleInfo.addSteeringClamp(builder, steeringClamp);
    VehicleInfo.addSteeringIncrement(builder, steeringIncrement);
    VehicleInfo.addBreakForce(builder, breakForce);
    VehicleInfo.addEngineForce(builder, engineForce);
    VehicleInfo.addOffsetHeight(builder, offsetHeight);
    VehicleInfo.addMass(builder, mass);
    VehicleInfo.addDepth(builder, depth);
    VehicleInfo.addHeight(builder, height);
    VehicleInfo.addWidth(builder, width);
    VehicleInfo.addWheels(builder, wheels);
    return VehicleInfo.endVehicleInfo(builder);
  }

  public static void startVehicleInfo(FlatBufferBuilder builder) { builder.startObject(17); }
  public static void addWheels(FlatBufferBuilder builder, int wheelsOffset) { builder.addOffset(0, wheelsOffset, 0); }
  public static int createWheelsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startWheelsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addEngineForce(FlatBufferBuilder builder, double engineForce) { builder.addDouble(1, engineForce, 1000); }
  public static void addBreakForce(FlatBufferBuilder builder, double breakForce) { builder.addDouble(2, breakForce, 44); }
  public static void addSteeringIncrement(FlatBufferBuilder builder, double steeringIncrement) { builder.addDouble(3, steeringIncrement, 0.04); }
  public static void addSteeringClamp(FlatBufferBuilder builder, double steeringClamp) { builder.addDouble(4, steeringClamp, 0.25); }
  public static void addFrictionSlip(FlatBufferBuilder builder, double frictionSlip) { builder.addDouble(5, frictionSlip, 40.5); }
  public static void addMaxSuspensionTravelCm(FlatBufferBuilder builder, double maxSuspensionTravelCm) { builder.addDouble(6, maxSuspensionTravelCm, 500); }
  public static void addSuspensionStiffness(FlatBufferBuilder builder, double suspensionStiffness) { builder.addDouble(7, suspensionStiffness, 35.88); }
  public static void addSuspensionDamping(FlatBufferBuilder builder, double suspensionDamping) { builder.addDouble(8, suspensionDamping, 0.88); }
  public static void addSuspensionCompression(FlatBufferBuilder builder, double suspensionCompression) { builder.addDouble(9, suspensionCompression, 0.83); }
  public static void addRollInfluence(FlatBufferBuilder builder, double rollInfluence) { builder.addDouble(10, rollInfluence, 0.1); }
  public static void addSuspensionRestLength(FlatBufferBuilder builder, double suspensionRestLength) { builder.addDouble(11, suspensionRestLength, 0.18); }
  public static void addWidth(FlatBufferBuilder builder, float width) { builder.addFloat(12, width, 0); }
  public static void addHeight(FlatBufferBuilder builder, float height) { builder.addFloat(13, height, 0); }
  public static void addDepth(FlatBufferBuilder builder, float depth) { builder.addFloat(14, depth, 0); }
  public static void addMass(FlatBufferBuilder builder, float mass) { builder.addFloat(15, mass, 0); }
  public static void addOffsetHeight(FlatBufferBuilder builder, float offsetHeight) { builder.addFloat(16, offsetHeight, 0); }
  public static int endVehicleInfo(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
  public static void finishVehicleInfoBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
};

