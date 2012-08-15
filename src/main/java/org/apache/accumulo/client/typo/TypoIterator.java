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
package org.apache.accumulo.client.typo;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

class TypoIterator<RT,CFT,CQT,VT> implements Iterator<Entry<TypoKey<RT,CFT,CQT>,VT>> {
  
  private Iterator<Entry<Key,Value>> iter;
  private TypoEncoders<RT,CFT,CQT,VT> ae;
  
  private class TypoEntry implements Map.Entry<TypoKey<RT,CFT,CQT>,VT> {
    
    private Entry<Key,Value> srcEntry;
    private TypoKey<RT,CFT,CQT> tk;
    private VT val;
    
    public TypoEntry(Entry<Key,Value> srcEntry, TypoKey<RT,CFT,CQT> tk, VT val) {
      this.srcEntry = srcEntry;
      this.tk = tk;
      this.val = val;
    }
    
    @Override
    public TypoKey<RT,CFT,CQT> getKey() {
      return tk;
    }
    
    @Override
    public VT getValue() {
      return val;
    }
    
    @Override
    public VT setValue(VT value) {
      return ae.valEnc.fromBytes(srcEntry.setValue(new Value(ae.valEnc.toBytes(value))).get());
    }
    
    public String toString() {
      return tk + " " + val;
    }
  }
  
  public TypoIterator(Iterator<Entry<Key,Value>> iterator, TypoEncoders<RT,CFT,CQT,VT> ae) {
    this.iter = iterator;
    this.ae = ae;
  }

  @Override
  public boolean hasNext() {
    return iter.hasNext();
  }
  
  @Override
  public Entry<TypoKey<RT,CFT,CQT>,VT> next() {
    Entry<Key,Value> srcEntry = iter.next();
    TypoKey<RT,CFT,CQT> tk = new TypoKey<RT,CFT,CQT>(srcEntry.getKey(), ae.rowLexEnc, ae.colfLexEnc, ae.colqLexEnc);
    VT val = ae.valEnc.fromBytes(srcEntry.getValue().get());
    
    return new TypoEntry(srcEntry, tk, val);
  }
  
  @Override
  public void remove() {
    iter.remove();
  }
  
}