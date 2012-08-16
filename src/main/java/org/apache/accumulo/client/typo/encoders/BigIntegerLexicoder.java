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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.apache.accumulo.client.typo.encoders.util.FixedByteArrayOutputStream;
import org.apache.accumulo.core.iterators.ValueFormatException;

/**
 * 
 */
public class BigIntegerLexicoder implements Lexicoder<BigInteger> {
  
  @Override
  public byte[] encode(BigInteger v) {
    
    try {
      byte[] bytes = v.toByteArray();
      
      byte[] ret = new byte[4 + bytes.length];
      
      DataOutputStream dos = new DataOutputStream(new FixedByteArrayOutputStream(ret));
      
      // flip the sign bit
      bytes[0] = (byte) (0x80 ^ bytes[0]);
      
      int len = bytes.length;
      if (v.signum() < 0)
        len = -len;
      
      len = len ^ 0x80000000;
      
      dos.writeInt(len);
      dos.write(bytes);
      
      return ret;
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
    

  }
  
  @Override
  public BigInteger decode(byte[] b) throws ValueFormatException {
    
    try {
      DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
      int len = dis.readInt();
      len = len ^ 0x80000000;
      len = Math.abs(len);
      
      byte[] bytes = new byte[len];
      dis.readFully(bytes);
      
      bytes[0] = (byte) (0x80 ^ bytes[0]);
      
      return new BigInteger(bytes);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }
  
}
