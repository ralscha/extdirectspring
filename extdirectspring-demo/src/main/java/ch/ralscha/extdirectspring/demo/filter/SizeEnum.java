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

package ch.ralscha.extdirectspring.demo.filter;

public enum SizeEnum {
  SMALL("small"), MEDIUM("medium"), LARGE("large"), EXTRA_LARGE("extra large");

  private String label;

  private SizeEnum(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static SizeEnum find(String label) {
    for (SizeEnum sizeEnum : SizeEnum.values()) {
      if (sizeEnum.getLabel().equals(label)) {
        return sizeEnum;
      }
    }
    return null;
  }
}
