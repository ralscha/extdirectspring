package ch.ralscha.extdirectspring.demo.touch;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

import com.google.common.collect.Lists;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@Service
public class CarouselService {

	private static final String RSS_URL = "http://www.acme.com/jef/apod/rss.xml?num=20";
	private static Pattern IMG_PATTERN = Pattern.compile(".*img src=\"([^\"]+)\".*", Pattern.DOTALL);
	
	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "touchcarousel")
	public List<CarouselPicture> readPictures(HttpServletRequest request) throws IllegalArgumentException, FeedException, IOException {
		
		URL feedUrl = new URL(RSS_URL);
		List<CarouselPicture> pictures = Lists.newArrayList();

		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(feedUrl));

		@SuppressWarnings("unchecked")
		List<SyndEntry> entries = feed.getEntries();
		for (SyndEntry entry : entries) {
			CarouselPicture pic = new CarouselPicture();
			pic.setId(entry.getUri());
			pic.setAuthor(entry.getAuthor());
			pic.setLink(entry.getLink());
			pic.setTitle(entry.getTitle());
			//pic.setContent();
			Matcher matcher = IMG_PATTERN.matcher(entry.getDescription().getValue());
			if (matcher.matches()) {
				String imageUrl = matcher.group(1);
				pic.setImage(request.getContextPath() + "/controller/picresize?url=" + imageUrl);
			}
			pictures.add(pic);
		}

		return pictures;
	}

}
