/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hp.hpl.jena.sparql.path;

import com.hp.hpl.jena.sparql.util.NodeIsomorphismMap ;

public class P_ZeroOrMore extends P_Path1
{
    // Not strictly - it's the same as {0,} - but useful as it directly
    // reflects the syntax so preserving {0,} and *
    
    public P_ZeroOrMore(Path path)
    {
        super(path) ;
    }

    @Override
    public boolean equalTo(Path path2, NodeIsomorphismMap isoMap)
    {
        if ( ! ( path2 instanceof P_ZeroOrMore ) ) return false ;
        P_ZeroOrMore other = (P_ZeroOrMore)path2 ;
        return getSubPath().equalTo(other.getSubPath(), isoMap)  ;
    }

    @Override
    public int hashCode()
    {
        return hashZeroOrMore ^ getSubPath().hashCode() ;
    }

    @Override
    public void visit(PathVisitor visitor)
    { visitor.visit(this) ; }
}
