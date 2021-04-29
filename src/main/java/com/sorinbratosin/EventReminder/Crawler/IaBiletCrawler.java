package com.sorinbratosin.EventReminder.Crawler;

import com.sorinbratosin.EventReminder.DAO.Event;
import com.sorinbratosin.EventReminder.Service.EventService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IaBiletCrawler {

    Logger LOG = LoggerFactory.getLogger(IaBiletCrawler.class);

    private Map<String, Integer> monthToInt = new HashMap<>();

    @Autowired
    private EventService eventService;

    public IaBiletCrawler() {
        monthToInt.put("ian", 1);
        monthToInt.put("feb", 2);
        monthToInt.put("mar", 3);
        monthToInt.put("apr", 4);
        monthToInt.put("mai", 5);
        monthToInt.put("iun", 6);
        monthToInt.put("iul", 7);
        monthToInt.put("aug", 8);
        monthToInt.put("sep", 9);
        monthToInt.put("oct", 10);
        monthToInt.put("nov", 11);
        monthToInt.put("dec", 12);
    }

    @Scheduled(fixedRate = 30000)
    public void run() throws IOException {
        LOG.info("A pornit IaBiletCrawler");
        extract("https://www.iabilet.ro/bilete-concerte-rock/");
        LOG.info("S-a terminat IaBiletCrawler");
    }

    public List<Event> extract(String html) throws IOException {
        LOG.info("Incep sa parsez fisierul html: " + html);
        List<Event> events = new ArrayList<>();

        Document doc = Jsoup.connect(html).get();
        Elements elements = doc.select(".event-list-item");

        for (Element eventElement : elements) {

            Element elementTitle = eventElement.selectFirst(".title span");
            String dateStartDay = eventElement.select(".date-start .date-day").text();
            String dateStartMonth = eventElement.select(".date-start .date-month").text();
            String dateStartYear = eventElement.select(".date-start .date-year").text().replace("'", "");
            String location = eventElement.select(".location .venue span").get(0).text();
            String description = eventElement.select(".main-info div").get(2).text();
            String city = eventElement.select(".location .venue span").get(1).text();
            if(city.toLowerCase().equals("romania")) {
                city = "N/A";
            }
            Element link = eventElement.select("div.col-xs-3 > a").first();
            String url = "iabilet.ro" + link.attr("href");
            //LOG.info("The URL " + url);

            Event event = new Event();

            int year = LocalDate.now().getYear();
            if(!dateStartYear.isEmpty()) {
                year = Integer.parseInt(20 + dateStartYear);
            }
            LocalDate eventStartDate = LocalDate.of(year, monthToInt.get(dateStartMonth), Integer.parseInt(dateStartDay));

            event.setName(elementTitle.text());
            event.setDateStart(eventStartDate);
            event.setLocation(location);
            event.setDescription(description);
            event.setCity(city);
            event.setUrl(url);

            events.add(event);
        }
        for (Event event : events) {
            Integer existingEventsWithSameName = eventService.countAllByName(event.getName());
            if (existingEventsWithSameName == 0) {
                eventService.save(event);
            }
        }
        LOG.info("Am terminat sa parsez fisierul html: " + html + ". Am extras " + events);

        return events;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
}
