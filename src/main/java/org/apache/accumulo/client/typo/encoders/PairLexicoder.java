/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.accumulo.client.typo.encoders;

import java.util.ArrayList;

import org.apache.accumulo.client.typo.tuples.Pair;

/**
 * 
 */
public class PairLexicoder<A,B> implements Lexicoder<Pair<A,B>> {

  private Lexicoder<A> firstLexicoder;
  private Lexicoder<B> secondLexicoder;

  public PairLexicoder(Lexicoder<A> firstLexicoder, Lexicoder<B> secondLexicoder) {
    this.firstLexicoder = firstLexicoder;
    this.secondLexicoder = secondLexicoder;
  }

  @Override
  public byte[] encode(Pair<A,B> data) {
    return concat(escape(firstLexicoder.encode(data.getFirst())), escape(secondLexicoder.encode(data.getSecond())));
  }


  @Override
  public Pair<A,B> decode(byte[] data) {
    
    byte[][] fields = split(data);
    if (fields.length != 2) {
      throw new RuntimeException("Data does not have 2 fields, it has " + fields.length);
    }
    
    return new Pair<A,B>(firstLexicoder.decode(unescape(fields[0])), secondLexicoder.decode(unescape(fields[1])));
  }
  
  public static byte[][] split(byte[] data) {
    ArrayList<Integer> offsets = new ArrayList<Integer>();
    
    for (int i = 0; i < data.length; i++) {
      if (data[i] == 0x00) {
        offsets.add(i);
      }
    }
    
    offsets.add(data.length);
    
    byte[][] ret = new byte[offsets.size()][];
    
    int index = 0;
    for (int i = 0; i < offsets.size(); i++) {
      ret[i] = new byte[offsets.get(i) - index];
      System.arraycopy(data, index, ret[i], 0, ret[i].length);
      index = offsets.get(i) + 1;
    }
    
    return ret;
  }

  public static byte[] concat(byte[]... fields) {
    int len = 0;
    for (byte[] field : fields) {
      len += field.length;
    }
    
    byte ret[] = new byte[len + fields.length - 1];
    int index = 0;
    
    for (byte[] field : fields) {
      System.arraycopy(field, 0, ret, index, field.length);
      index += field.length;
      if (index < ret.length)
        ret[index++] = 0x00;
    }
    
    return ret;
  }

  /**
   * Escapes 0x00 with 0x01 0x01 and 0x01 with 0x01 0x02
   * 
   * @param in
   * @return
   */
  public static byte[] escape(byte[] in) {
    int escapeCount = 0;
    for (int i = 0; i < in.length; i++) {
      if (in[i] == 0x00 || in[i] == 0x01) {
        escapeCount++;
      }
    }
    
    if (escapeCount == 0)
      return in;
    
    byte ret[] = new byte[escapeCount + in.length];
    int index = 0;
    
    for (int i = 0; i < in.length; i++) {
      switch (in[i]) {
        case 0x00:
          ret[index++] = 0x01;
          ret[index++] = 0x01;
          break;
        case 0x01:
          ret[index++] = 0x01;
          ret[index++] = 0x02;
          break;
        default:
          ret[index++] = in[i];
      }
    }
    
    return ret;
  }

  public static byte[] unescape(byte[] in) {
    int escapeCount = 0;
    for (int i = 0; i < in.length; i++) {
      if (in[i] == 0x01) {
        escapeCount++;
        i++;
      }
    }
    
    if (escapeCount == 0)
      return in;

    byte ret[] = new byte[in.length - escapeCount];
    
    int index = 0;
    for (int i = 0; i < in.length; i++) {
      if (in[i] == 0x01) {
        i++;
        ret[index++] = (byte) (in[i] - 1);
      } else {
        ret[index++] = in[i];
      }
      
    }
    
    return ret;
  }

}
