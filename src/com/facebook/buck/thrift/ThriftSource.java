/*
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.thrift;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.nio.file.Path;

public class ThriftSource {

  private final ThriftCompiler compileRule;
  private final ImmutableList<String> services;
  private final Path outputDir;

  public ThriftSource(
      ThriftCompiler compileRule,
      ImmutableList<String> services,
      Path outputDir) {

    this.compileRule = Preconditions.checkNotNull(compileRule);
    this.services = Preconditions.checkNotNull(services);
    this.outputDir = Preconditions.checkNotNull(outputDir);
  }

  public ThriftCompiler getCompileRule() {
    return compileRule;
  }

  public ImmutableList<String> getServices() {
    return services;
  }

  public Path getOutputDir() {
    return outputDir;
  }

}