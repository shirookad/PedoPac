// automatically generated, do not modify

package com.naronco.pedopac.schemas;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

public class VehicleWheelInfo extends Table {
  public static VehicleWheelInfo getRootAsVehicleWheelInfo(ByteBuffer _bb) { return getRootAsVehicleWheelInfo(_bb, new VehicleWheelInfo()); }
  public static VehicleWheelInfo getRootAsVehicleWheelInfo(ByteBuffer _bb, VehicleWheelInfo obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public VehicleWheelInfo __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public Vec3 position() { return position(new Vec3()); }
  public Vec3 position(Vec3 obj) { int o = __offset(4); return o != 0 ? obj.__init(o + bb_pos, bb) : null; }
  public boolean isFront() { int o = __offset(6); return o != 0 ? 0!=bb.get(o + bb_pos) : false; }
  public double wheelRadius() { int o = __offset(8); return o != 0 ? bb.getDouble(o + bb_pos) : 0.287; }

  public static void startVehicleWheelInfo(FlatBufferBuilder builder) { builder.startObject(3); }
  public static void addPosition(FlatBufferBuilder builder, int positionOffset) { builder.addStruct(0, positionOffset, 0); }
  public static void addIsFront(FlatBufferBuilder builder, boolean isFront) { builder.addBoolean(1, isFront, false); }
  public static void addWheelRadius(FlatBufferBuilder builder, double wheelRadius) { builder.addDouble(2, wheelRadius, 0.287); }
  public static int endVehicleWheelInfo(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

