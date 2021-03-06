/*******************************************************************************
 *
 * Pentaho Big Data
 *
 * Copyright (C) 2002-2012 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.hadoop.mapreduce.converter.converters;

import org.apache.hadoop.io.BytesWritable;
import org.junit.Test;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.hadoop.mapreduce.converter.TypeConversionException;

import static org.junit.Assert.*;

public class KettleTypeToBytesWritableConverterTest {
  @Test
  public void canConvert() throws Exception {
    KettleTypeToBytesWritableConverter c = new KettleTypeToBytesWritableConverter();

    assertTrue(c.canConvert(String.class, BytesWritable.class));
    assertTrue(c.canConvert(Long.class, BytesWritable.class));
    assertTrue(c.canConvert(String.class, BytesWritable.class));
    assertFalse(c.canConvert(Object.class, BytesWritable.class));
    assertFalse(c.canConvert(BytesWritable.class, BytesWritable.class));
    assertFalse(c.canConvert(null, null));
    assertFalse(c.canConvert(BytesWritable.class, Object.class));
    assertFalse(c.canConvert(Object.class, Long.class));
  }

  @Test
  public void convert() throws Exception {
    KettleTypeToBytesWritableConverter c = new KettleTypeToBytesWritableConverter();
    String value = "false";
    BytesWritable expected = new BytesWritable(value.getBytes());

    // Convert from a normal String
    ValueMeta normalMeta = new ValueMeta("test", ValueMetaInterface.TYPE_STRING, ValueMetaInterface.STORAGE_TYPE_NORMAL);
    assertEquals(expected, c.convert(normalMeta, value));

    // Convert from a byte array
    ValueMeta binaryMeta = new ValueMeta("test", ValueMetaInterface.TYPE_STRING, ValueMetaInterface.STORAGE_TYPE_BINARY_STRING);
    ValueMeta storageMeta = new ValueMeta("test", ValueMetaInterface.TYPE_STRING, ValueMetaInterface.STORAGE_TYPE_NORMAL);
    binaryMeta.setStorageMetadata(storageMeta);
    byte[] rawValue = value.getBytes("UTF-8");
    assertEquals(expected, c.convert(binaryMeta, rawValue));

    try {
      c.convert(null, null);
      fail();
    } catch (TypeConversionException ex) {
      assertTrue(ex.getMessage().contains("Error converting to"));
    }

    try {
      ValueMeta integerMeta = new ValueMeta("test", ValueMetaInterface.TYPE_INTEGER);
      c.convert(integerMeta, "not an integer");
      fail();
    } catch (TypeConversionException ex) {
      assertTrue(ex.getMessage().contains("Error converting to"));
    }
  }
}
