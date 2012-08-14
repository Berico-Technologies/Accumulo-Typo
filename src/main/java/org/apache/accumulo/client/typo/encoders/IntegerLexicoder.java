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

/**
 * 
 */
public class IntegerLexicoder implements Lexicoder<Integer> {
  
  private UIntegerLexicoder uil = new UIntegerLexicoder();
  
  @Override
  public byte[] toBytes(Integer i) {
    return uil.toBytes(i ^ 0x80000000);
  }
  
  @Override
  public Integer fromBytes(byte[] data) {
    return uil.fromBytes(data) ^ 0x80000000;
  }
  
}