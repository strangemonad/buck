/*
 * Copyright 2012-present Facebook, Inc.
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

package com.facebook.buck.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

/**
 * Traverses all files in a directory. This class <em>will</em> follow symlinks.
 */
public abstract class DirectoryTraversal {

  private final File root;
  private final ImmutableSet<String> ignorePaths;

  /** @param root must be a directory */
  public DirectoryTraversal(File root, ImmutableSet<String> ignorePaths) {
    this.root = Preconditions.checkNotNull(root);
    this.ignorePaths = Preconditions.checkNotNull(ignorePaths);
  }

  public DirectoryTraversal(File root) {
    this(root, ImmutableSet.<String>of());
  }

  public File getRoot() {
    return root;
  }

  public final void traverse() {
    Preconditions.checkState(root.isDirectory(), "Must be a directory: %s", root);

    final Path rootPath = root.toPath();
    FileVisitor<Path> visitor = new FileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (ignorePaths.contains(rootPath.relativize(dir).toString())) {
          return FileVisitResult.SKIP_SUBTREE;
        } else {
          return FileVisitResult.CONTINUE;
        }
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        visit(file.toFile(), rootPath.relativize(file).toString());
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
      }
    };

    try {
      Files.walkFileTree(
          rootPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), /* maxDepth */ Integer.MAX_VALUE,
          visitor);
    } catch (IOException e) {
      // IOException is only thrown if it's thrown by the visitor functions.
      // Our visitor should not throw.
      throw new RuntimeException(e);
    }
  }

  /**
   * @param file an ordinary file (not a directory)
   * @param relativePath a path such as "foo.txt" or "foo/bar.txt"
   */
  public abstract void visit(File file, String relativePath);

  public static void main(String[] args) {
    File directory = new File(args[0]);
    new DirectoryTraversal(directory) {

      @Override
      public void visit(File file, String relativePath) {
        System.out.println(relativePath);
      }
    }.traverse();
  }
}
