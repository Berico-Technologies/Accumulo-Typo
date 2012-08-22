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
package org.apache.accumulo.typo.encoders;

/**
 * 
 */
public class DoubleLexicoder implements Lexicoder<Double> {
  
  private ULongLexicoder longEncoder = new ULongLexicoder();
  
  @Override
  public byte[] encode(Double d) {
    long l = Double.doubleToRawLongBits(d);
    if (l < 0)
      l = ~l;
    else
      l = l ^ 0x8000000000000000l;
    
    return longEncoder.encode(l);
  }
  
  @Override
  public Double decode(byte[] data) {
    long l = longEncoder.decode(data);
    if (l < 0)
      l = l ^ 0x8000000000000000l;
    else
      l = ~l;
    return Double.longBitsToDouble(l);
  }
  
}