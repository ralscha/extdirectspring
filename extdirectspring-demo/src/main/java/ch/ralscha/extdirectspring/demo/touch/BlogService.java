/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.demo.touch;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

import com.google.common.collect.Lists;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@Service
public class BlogService {

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "touch")
	@SuppressWarnings("unchecked")
	public List<Post> getBlogPosts() throws IllegalArgumentException, FeedException, IOException {
		List<Post> posts = Lists.newArrayList();
		
		URL feedUrl = new URL("http://feeds.feedburner.com/SenchaBlog");
		 
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));

		List<SyndEntry> entries = feed.getEntries();
		for (SyndEntry entry : entries) {
			
			Post post = new Post();
			post.setTitle(entry.getTitle());
			post.setLeaf(true);
			post.setContent(((SyndContentImpl)entry.getContents().iterator().next()).getValue());
			posts.add(post);
        }
    
		return posts;
		
	}

}
