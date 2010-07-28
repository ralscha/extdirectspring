/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.ralscha.extdirectspring.filter;

public enum ComparisonEnum {
  LESS_THAN("lt"), GREATER_THAN("gt"), EQUAL("eq");

  private String raw;

  private ComparisonEnum(String raw) {
    this.raw = raw;
  }

  public String getRaw() {
    return raw;
  }

  public static ComparisonEnum find(String raw) {
    for (ComparisonEnum comparisonEnum : ComparisonEnum.values()) {
      if (comparisonEnum.getRaw().equals(raw)) {
        return comparisonEnum;
      }
    }
    return null;
  }

}
