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

package ch.ralscha.extdirectspring.demo.tree;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

@Named
public class TreeProvider {

  public static class Node {

    public String id;
    public String text;
    public boolean leaf;
  }

  @ExtDirectMethod(group = "tree")
  public List<Node> getTree(String id) {
    List<Node> result = new ArrayList<Node>();
    if (id.equals("root")) {
      for (int i = 1; i <= 5; ++i) {
        Node node = new Node();
        node.id = "n" + i;
        node.text = "Node " + i;
        node.leaf = false;
        result.add(node);
      }
    } else if (id.length() == 2) {
      String num = id.substring(1);
      for (int i = 1; i <= 5; ++i) {
        Node node = new Node();
        node.id = "id" + i;
        node.text = "Node " + num + "." + i;
        node.leaf = true;
        result.add(node);
      }
    }
    return result;
  }

}
