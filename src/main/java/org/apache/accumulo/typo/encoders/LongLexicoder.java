package org.apache.accumulo.typo.encoders;
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

/**
 * Signed long encoder. The lexicographic encoding sorts Long.MIN_VALUE first and Long.MAX_VALUE last. The lexicographic encoding sorts -2l before -1l. It
 * corresponds to the sort order of Long.
 */

public class LongLexicoder extends ULongLexicoder {
  @Override
  public byte[] encode(Long l) {
    return super.encode(l ^ 0x8000000000000000l);
  }
  
  @Override
  public Long decode(byte[] data) {
    return super.decode(data) ^ 0x8000000000000000l;
  }
}
