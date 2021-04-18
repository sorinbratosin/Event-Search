package com.sorinbratosin.EventReminder;


import com.sorinbratosin.EventReminder.Crawler.IaBiletCrawler;
import com.sorinbratosin.EventReminder.DAO.Event;
import com.sorinbratosin.EventReminder.Service.EventService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class IaBiletCrawlerTest {

    @Test
    public void testParsing() throws IOException {
        IaBiletCrawler iaBiletCrawler = new IaBiletCrawler();
        EventService eventService = Mockito.mock(EventService.class);
        iaBiletCrawler.setEventService(eventService);

        String html = Files.readString(Path.of("src/test/resources/ia-bilet-rock.html"));
        List<Event> events = iaBiletCrawler.extract(html);

        assertEquals(events.size(), 24);
        assertEquals(events.get(0).getName(), "KUMM live | concert aniversar Different Parties - 15 ani");
        assertEquals(events.get(0).getDateStart(), LocalDate.of(2021,3,19));
        assertEquals(events.get(0).getDescription(), "Tuturor ne este dor de concerte, de experiența muzicii live, de schimbul de energie dintre scenă și public. Așa că am readus pe scenă cele mai…");
        assertEquals(events.get(0).getLocation(), "Online");
        assertEquals(events.get(0).getCity(), "N/A");

    }

    @Test
    public void testParsingEventsAreNotDuplicateInTheDatabase() throws IOException {
        IaBiletCrawler iaBiletCrawler = new IaBiletCrawler();
        EventService eventService = Mockito.mock(EventService.class);

        Mockito.when(eventService.countAllByName("KUMM live | concert aniversar Different Parties - 15 ani")).thenReturn(1);

        iaBiletCrawler.setEventService(eventService);

        String html = Files.readString(Path.of("src/test/resources/ia-bilet-rock.html"));
        List<Event> events = iaBiletCrawler.extract(html);

        assertEquals(events.size(), 24 );
        Mockito.verify(eventService, Mockito.times(23)).save(any(Event.class));
    }
}
