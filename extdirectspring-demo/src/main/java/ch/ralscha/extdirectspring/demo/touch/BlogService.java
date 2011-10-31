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
